package horn

import scala.collection.mutable

case class Problem(decls: List[PredDecl], clauses: List[Clause])

case class PredDecl(
    method: String,
    name: String,
    index: Int,
    types: List[Type]
) {
  val identifier =
    if (index >= 0)
      method + "_" + name + "_" + index
    else method + "_" + name

  def apply(args: List[Expr]) = App(
    identifier,
    args take types.length
  ) // XXX: bad hack to generate pre/post without locals
}

case class Clause(
    vars: List[VarDecl],
    prems: List[Expr],
    concl: Expr,
    comment: String
) {
  def formatPrems = if (prems.isEmpty) "true"
  else prems.mkString("(and ", "\n             ", ")")
}

class Horn(val method: Method) {
  val Method(_, formals, results, requires, ensures, Body(locals, progs)) =
    method

  var index = 0
  val preds = mutable.Buffer[PredDecl]()
  val clauses = mutable.Buffer[Clause]()

  val inputs = formals // ++ (locals.filter(!_.typ.isInstanceOf[ADT]))

  val scope0 = formals ++ results ++ locals
  val scope = flatten(scope0)
  val vars = scope map (_.x)
  val types = scope map (_.typ)

  val pre = newPred("pre", formals map (_.typ), -1)
  val post = newPred("post", (formals ++ results) map (_.typ), -1)

  def not(phi: Expr) = {
    App("!", List(phi))
  }

  def newPred(name: String, types: List[Type] = types, next: Int = index) = {
    val decl = PredDecl(method.name, name, next, types)
    if (next >= 0) index += 1
    preds += decl
    decl
  }

  def newClause(prems: List[Expr], concl: Expr, comment: String): Unit = {
    newClause(Nil, prems, concl, comment)
  }

  def newClause(
      fresh: List[VarDecl],
      prems: List[Expr],
      concl: Expr,
      comment: String
  ): Unit = {
    val clause = Clause(scope ++ fresh, prems, concl, "method " + method.name + ": " + comment)
    clauses += clause
  }

  def flatten(types: List[VarDecl]): List[VarDecl] = {
    types match {
      case Nil =>
        Nil

      case VarDecl(Var(var_), ADT(adt, params)) :: rest =>
        for (
          (name, types) <- Builtin.preds(var_, adt, params, inputs.map(_.typ))
        ) {
          newPred(name, types, -1)
        }

        Builtin.state(var_, adt, params) ::: flatten(rest)

      case decl :: rest =>
        decl :: flatten(rest)
    }
  }

  def problem(all: List[Horn]): Problem = {
    def translate(
        pre: PredDecl,
        progs: List[Prog],
        post: PredDecl,
        comment: String
    ): Unit = {
      progs match {
        case Nil =>
          newClause(List(pre(vars)), post(vars), comment)

        case Assume(phi) :: rest =>
          val pred = newPred("assume")
          newClause(List(pre(vars), phi), pred(vars), "assume")
          translate(pred, rest, post, comment)

        case Assert(phi) :: rest =>
          newClause(List(pre(vars)), phi, "assert")
          translate(pre, rest, post, comment)

        // ADT constructor call
        case Assign(List(Var(var_)), New(ADT(adt, params), args)) :: rest =>
          val lhs = Builtin.obj(var_, adt, params)

          val (fresh, rhs) = Builtin.new_(var_, adt, params, args)

          val map = Map(lhs zip rhs*)

          val vars_ = vars map {
            case x if map contains x => map(x)
            case y                   => y
          }

          val pred = newPred("new")
          newClause(fresh, List(pre(vars)), pred(vars_), "new " + adt)
          translate(pred, rest, post, comment)

        // ADT method call
        case Assign(lhs, Call(x @ Var(var_), op, args)) :: rest =>
          val Some(VarDecl(_, ADT(adt, params))) = scope0.find(_.x == x)
          val obj = Builtin.obj(var_, adt, params)

          val (checks, transitions) =
            Builtin.call(method.name, var_, adt, params, obj, op, args, inputs.map(_.x))
          val pred = newPred("call")

          for ((fresh, phi, check) <- checks) {
            newClause(
              fresh,
              pre(vars) :: phi,
              check,
              adt + "." + op + " precondition"
            )
          }

          for ((fresh, rhs, upd, phi, comment) <- transitions) {
            assert(lhs.length == rhs.length, op -> lhs -> rhs)
            assert(obj.length == upd.length, op -> obj -> upd)

            val map = Map((lhs ++ obj) zip (rhs ++ upd)*)

            val vars_ = vars map {
              case x if map contains x => map(x)
              case y                   => y
            }

            newClause(
              fresh,
              pre(vars) :: phi,
              pred(vars_),
              adt + "." + op + " " + comment
            )

          }

          translate(pred, rest, post, comment)

        // global method call
        case Assign(lhs, Exprs(List(App(fun, args)))) :: rest
            if all.exists(_.method.name == fun) =>
          val Some(that) = all.find(_.method.name == fun)
          val call1 = that.pre(args)
          newClause(List(pre(vars)), call1, fun + " call")
          val call2 = that.post(args ++ lhs)
          val pred = newPred("call")
          newClause(List(pre(vars), call2), pred(vars), fun + " return")
          translate(pred, rest, post, comment)

        // regular assignment
        case Assign(lhs, Exprs(rhs)) :: rest =>
          assert(lhs.length == rhs.length)

          val map = Map(lhs zip rhs*)

          val vars_ = vars map {
            case x if map contains x => map(x)
            case y                   => y
          }

          val pred = newPred("assign")
          newClause(List(pre(vars)), pred(vars_), "assign")
          translate(pred, rest, post, comment)

        case If(test, left, right) :: rest =>
          val pred = newPred("join")
          translate(pre, Assume(test) :: left, pred, "if left")
          translate(pre, Assume(not(test)) :: right, pred, "if right")
          translate(pred, rest, post, comment)

        case While(test, body) :: rest =>
          val pred = newPred("invariant")
          newClause(List(pre(vars)), pred(vars), "invariant initially")
          translate(pred, Assume(test) :: body, pred, "invariant preserved")
          translate(pred, Assume(not(test)) :: rest, post, comment)
      }
    }

    newClause(requires, pre(vars), "requires")

    translate(pre, progs, post, "return")

    for (phi <- ensures)
      newClause(List(post(vars)), phi, "ensures")

    Problem(preds.toList, clauses.toList)
  }
}
