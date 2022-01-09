package horn

import arse._
import arse.implicits._
import scala.io.Source
import java.io.File

object Parser {
  implicit object whitespace extends Whitespace("(\\s|(//.*\\n))*")

  def parens[A](p: Parser[A]) = "(" ~ p ~ ")"
  def braces[A](p: Parser[A]) = "{" ~ p ~ "}"

  val name = S("[a-zA-Z][a-zA-Z0-9]*")
  val op = L("+", "-", "*", "/", "&&", "||", "<=", "<", ">=", ">", "==>", "==")

  val typ = IntType("int")

  val num = P(Num(int))
  val vr = P(Var(name))

  val expr: Parser[Expr] = M(inner, op, App, Syntax)
  val inner = parens(expr) | num | vr

  val prog: Parser[Prog] = P(assume | assert | if_ | while_ | assign)
  val progs = prog.*
  val block = braces(progs)

  val assume = Assume("assume" ~ expr ~ ";")
  val assert = Assert("assert" ~ expr ~ ";")
  val assign = Assign(vr ~ ":=" ~ expr ~ ";")

  val else_ = "else" ~ block | ret(Nil)
  val if_ = If("if" ~ expr ~ block ~ else_)

  val while_ = While("while" ~ expr ~ block)

  val vardecl = VarDecl(vr ~ ":" ~ typ)
  val vardecls = vardecl ~* ","

  val local = VarDecl("var" ~ vr ~ ":" ~ typ ~ ";")
  val locals = local.*
  val body = Body(braces(locals ~ progs))

  val require = "requires" ~ expr ~ ";";
  val requires = require.*
  val ensure = "ensures" ~ expr ~ ";";
  val ensures = ensure.*

  val method = Method(
    "method" ~ name ~ parens(vardecls) ~ "returns" ~ parens(vardecls) ~ requires ~ ensures ~ body
  )

  val methods = method.*
}

class Parseable[T](p: Parser[T]) {
  def from(str: String): T = {
    import Parser._
    p.parseAll(str)
  }

  def from(file: File): T = {
    from(Source.fromFile(file).getLines().mkString)
  }

  def fromFile(file: String): T = {
    from(new File(file))
  }
}
