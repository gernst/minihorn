package horn

sealed trait Type

case class Sort(name: String) extends Type {
  override def toString = name match {
    case "int"  => "Int"
    case "bool" => "Bool"
  }
}

object Sort {
  val int = Sort("int")
  val bool = Sort("bool")
}

case class ADT(name: String, params: List[Type]) extends Type {
  override def toString = (name :: params).mkString("(", " ", ")")
}

case class VarDecl(x: Var, typ: Type) {
  override def toString = "(" + x + " " + typ + ")"
}

sealed trait Expr

case class Num(n: Int) extends Expr {
  override def toString = 
    if(n >= 0) n.toString
    else "(- " + (-n).toString + ")"
}

case class Var(name: String) extends Expr {
  override def toString = name
}

case class App(fun: String, args: List[Expr]) extends Expr {
  override def toString = (fun, args) match {
    case (_, Nil) =>
      fun
    case ("!", List(arg)) =>
      "(not " + arg + ")"
    case ("==", List(arg1, arg2)) =>
      "(= " + arg1 + " " + arg2 + ")"
    case ("!=", List(arg1, arg2)) =>
      "(distinct " + arg1 + " " + arg2 + ")"
    case ("==>", List(arg1, arg2)) =>
      "(=> " + arg1 + " " + arg2 + ")"
    case ("||", List(arg1, arg2)) =>
      "(or " + arg1 + " " + arg2 + ")"
    case ("&&", List(arg1, arg2)) =>
      "(and " + arg1 + " " + arg2 + ")"
    case _ =>
      (fun :: args).mkString("(", " ", ")")
  }
}

sealed trait Rhs
case class New(constr: ADT, args: List[Expr]) extends Rhs
case class Call(obj: Expr, op: String, args: List[Expr]) extends Rhs
case class Exprs(rhs: List[Expr]) extends Rhs

sealed trait Prog

object Prog extends Parser.Parseable[List[Method]](Parser.methods)

case class Assume(phi: Expr) extends Prog
case class Assert(phi: Expr) extends Prog
case class Assign(lhs: List[Var], rhs: Rhs) extends Prog
case class If(test: Expr, left: List[Prog], right: List[Prog]) extends Prog
case class While(test: Expr, body: List[Prog]) extends Prog

case class Body(locals: List[VarDecl], progs: List[Prog])

case class Method(
    name: String,
    formals: List[VarDecl],
    results: List[VarDecl],
    requires: List[Expr],
    ensures: List[Expr],
    body: Body
)
