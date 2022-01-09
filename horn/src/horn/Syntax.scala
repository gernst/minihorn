package horn

import arse._

sealed trait Type

case object IntType extends Type {
  override def toString = "Int"
}

case class VarDecl(x: Var, typ: Type) {
  override def toString = "(" + x + " " + typ + ")"
}

sealed trait Expr

case class Num(n: Int) extends Expr {
  override def toString = n.toString
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
      (fun :: args) mkString ("(", " ", ")")
  }
}

sealed trait Prog

object Prog extends Parseable(Parser.methods) {}

case class Assume(phi: Expr) extends Prog
case class Assert(phi: Expr) extends Prog
case class Assign(x: Var, rhs: Expr) extends Prog
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

object Syntax extends Syntax[String] {
  val infix_ops = Map(
    ("==>", (Left, 1)),
    ("||", (Left, 2)),
    ("&&", (Left, 3)),
    ("==", (Left, 5)),
    ("!=", (Left, 5)),
    (">=", (Left, 5)),
    (">", (Left, 5)),
    ("<", (Left, 5)),
    ("<=", (Left, 5)),
    ("+", (Left, 6)),
    ("-", (Left, 6)),
    ("*", (Left, 7)),
    ("/", (Left, 7))
  )
  val postfix_ops = Map(
  )
  val prefix_ops = Map(
    ("!", 4),
    ("-", 8)
  )
}
