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

  def apply(args: List[Expr]) = App(identifier, args take types.length) // XXX: bad hack to generate pre/post without locals
}

case class Clause(vars: List[VarDecl], prems: List[Expr], concl: Expr) {
  def formatPrems = if (prems.isEmpty) "true"
  else prems.mkString("(and ", "\n             ", ")")
}

class Horn(val method: Method) {
  val Method(_, formals, results, requires, ensures, Body(locals, progs)) =
    method

  val scope = formals ++ results ++ locals
  val vars = scope map (_.x)
  val types = scope map (_.typ)

  var index = 0
  val preds = mutable.Buffer[PredDecl]()
  val clauses = mutable.Buffer[Clause]()

  val pre = newPred("pre", formals map (_.typ), -1)
  val post = newPred("post", (formals ++ results) map (_.typ), -1)

  def not(phi: Expr) = {
    App("!", List(phi))
  }

  def newPred(name: String, types: List[Type] = types, next: Int = index) = {
    val decl = PredDecl(method.name, name, next, types)
    index += 1
    preds += decl
    decl
  }

  def newClause(prems: List[Expr], concl: Expr) {
    val clause = Clause(scope, prems, concl)
    clauses += clause
  }

  def problem(all: List[Horn]): Problem = {
    def translate(pre: PredDecl, progs: List[Prog], post: PredDecl) {
      progs match {
        case Nil =>
          newClause(List(pre(vars)), post(vars))

        case Assume(phi) :: rest =>
          val pred = newPred("assume")
          newClause(List(pre(vars), phi), pred(vars))
          translate(pred, rest, post)

        case Assert(phi) :: rest =>
          newClause(List(pre(vars)), phi)
          translate(pre, rest, post)

        case Assign(lhs, List(App(fun, args))) :: rest
            if all.exists(_.method.name == fun) =>
          val Some(that) = all.find(_.method.name == fun)
          val call1 = that.pre(args)
          newClause(List(pre(vars)), call1)
          val call2 = that.post(args ++ lhs)
          val pred = newPred("call")
          newClause(List(pre(vars), call2), pred(vars))
          translate(pred, rest, post)

        case Assign(lhs, rhs) :: rest =>
          assert(lhs.length == rhs.length)

          val map = Map(lhs zip rhs: _*)

          val vars_ = vars map {
            case x if map contains x => map(x)
            case y                   => y
          }

          val pred = newPred("assign")
          newClause(List(pre(vars)), pred(vars_))
          translate(pred, rest, post)

        case If(test, left, right) :: rest =>
          val pred = newPred("join")
          translate(pre, Assume(test) :: left, pred)
          translate(pre, Assume(not(test)) :: right, pred)
          translate(pred, rest, post)

        case While(test, body) :: rest =>
          val pred = newPred("invariant")
          newClause(List(pre(vars)), pred(vars))
          translate(pred, Assume(test) :: body, pred)
          translate(pred, Assume(not(test)) :: rest, post)
      }
    }

    newClause(requires, pre(vars))

    translate(pre, progs, post)

    for (phi <- ensures)
      newClause(List(post(vars)), phi)

    Problem(preds.toList, clauses.toList)
  }
}
