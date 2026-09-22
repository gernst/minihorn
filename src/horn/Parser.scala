package horn

import scala.util.parsing.combinator.RegexParsers
import java.io.File
import scala.io.Source

object Parser extends RegexParsers {
  override val whiteSpace =
     """(\s+|//[^\r\n]*|/\*(?s:.*?)\*/)+""".r

  def parens[A](p: Parser[A]) =
    "(" ~> p <~ ")"

  def braces[A](p: Parser[A]) =
    "{" ~> p <~ "}"

  def angle[A](p: Parser[A]) =
    "<" ~> p <~ ">"

  val int =
    """\d+""".r

  val name =
    """[a-zA-Z_][a-zA-Z0-9_]*""".r

  val typ: Parser[Type] =
    defer(sort | adt)

  val expr: Parser[Expr] =
    defer(nonassoc("<==>", imp, InfixOp))

  val prog: Parser[Prog] =
    defer(assume | assert | if_ | while_ | assign | plain_call)

  val sort =
    ("int" | "bool") ^^ { Sort(_) }

  val gen =
    repsep(typ, ",")

  val adt =
    name ~ angle(gen) ^^ { case n ~ ps =>
      ADT(n, ps)
    }

  val vr =
    name ^^ { Var(_) }

  val vars =
    rep1sep(vr, ",")

  val exprs =
    rep1sep(expr, ",")

  val args =
    repsep(expr, ",")

  val num =
    int ^^ { text => Num(text.toInt) }

  val app =
    name ~ parens(exprs).? ^^ Nary

  val prefix =
    (("-" | "!") ~ expr ^^ Unary)

  val factor: Parser[Expr] =
    prefix | num | app | parens(expr)

  val product =
    assoc("*" | "/" | "%", factor, LeftOp)

  val sum =
    assoc("+" | "-", product, LeftOp)

  val prop =
    nonassoc("<" | "<=" | "==" | "!=" | ">=" | ">", sum, InfixOp)

  val conj =
    assoc("&&", prop, LeftOp)

  val disj =
    assoc("||", conj, LeftOp)

  val imp =
    assoc("==>", disj, RightOp)

  val vardecl =
    vr ~ ":" ~ typ ^^ { case x ~ _ ~ t =>
      VarDecl(x, t)
    }

  val vardecls =
    repsep(vardecl, ",")

  val progs =
    prog.*

  val block =
    braces(progs)

  val assume =
    "assume" ~> expr <~ ";" ^^ { Assume(_) }

  val assert =
    "assert" ~> expr <~ ";" ^^ { Assert(_) }

  val new_ =
    "new" ~> adt ~ parens(args) ^^ { case n ~ es =>
      New(n, es)
    }

  val call_ =
    (expr <~ ".") ~ name ~ parens(args) ^^ { case o ~ m ~ as =>
      Call(o, m, as)
    }

  val exprs_ =
    exprs ^^ { Exprs(_) }

  val rhs =
    new_ | call_ | exprs_

  val assign =
    vars ~ ":=" ~ rhs <~ ";" ^^ { case lhs ~ _ ~ rhs =>
      Assign(lhs, rhs)
    }

  val plain_call =
    call_ <~ ";" ^^ { Assign(Nil, _) }

  val else_ =
    "else" ~> block

  val if_ =
    "if" ~> expr ~ block ~ else_.? ^^ {
      case test ~ left ~ None =>
        If(test, left, Nil)
      case test ~ left ~ Some(right) =>
        If(test, left, right)
    }

  val while_ =
    "while" ~> expr ~ block ^^ { case test ~ body =>
      While(test, body)
    }

  val local =
    "var" ~> vardecl <~ ";"

  val body =
    braces(local.* ~ progs) ^^ { case l ~ p =>
      Body(l, p)
    }

  val requires =
    "requires" ~> expr <~ ";"

  val ensures =
    "ensures" ~> expr <~ ";"

  val formals =
    parens(vardecls)

  val sig =
    name ~ formals ~ ("returns" ~> formals).?

  val spec =
    requires.* ~ ensures.*

  val method =
    "method" ~> sig ~ spec ~ body ^^ {
      case (name ~ xs ~ None) ~ (pre ~ post) ~ prog =>
        Method(name, xs, Nil, pre, post, prog)
      case (name ~ xs ~ Some(ys)) ~ (pre ~ post) ~ prog =>
        Method(name, xs, ys, pre, post, prog)
    }

  val methods =
    method.*

  def assoc(
      ops: Parser[String],
      inner: Parser[Expr],
      app: (Expr ~ List[(String ~ Expr)]) => Expr
  ): Parser[Expr] =
    inner ~ (ops ~ inner).* ^^ app

  def nonassoc(
      ops: Parser[String],
      inner: Parser[Expr],
      app: (Expr ~ Option[(String ~ Expr)]) => Expr
  ): Parser[Expr] =
    inner ~ (ops ~ inner).? ^^ app

  def Unary: (String ~ Expr) => Expr = { case op ~ arg =>
    App(op, List(arg))
  }

  def Nary: (String ~ Option[List[Expr]]) => Expr = {
    case name ~ None =>
      Var(name)
    case fun ~ Some(args) =>
      App(fun, args)
  }

  def InfixOp: (Expr ~ Option[String ~ Expr]) => Expr = {
    case arg1 ~ None =>
      arg1
    case arg1 ~ Some(op ~ arg2) =>
      App(op, List(arg1, arg2))
  }

  def LeftOp: (Expr ~ List[(String ~ Expr)]) => Expr = { case arg ~ args =>
    args.foldLeft(arg) { case (left, op ~ right) =>
      App(op, List(left, right))
    }
  }

  def RightOp: (Expr ~ List[(String ~ Expr)]) => Expr = { case arg ~ args =>
    args.foldRight(arg) { case (op ~ right, left) =>
      App(op, List(left, right))
    }
  }

  class Parseable[T](p: Parser[T]) {
    def from(text: String): T = {
      parseAll(p, text) match {
        case Success(result, next) =>
          result

        case other =>
          println(other)
          ???
      }
    }

    def from(file: File): T = {
      from(Source.fromFile(file).getLines().mkString("\n"))
    }

    def fromFile(file: String): T = {
      from(new File(file))
    }
  }

  def defer[T](p: => Parser[T]) = {
    Defer(() => p)
  }

  class Defer[T](p: () => Parser[T]) extends Parser[T] {
    lazy val q = p()

    def apply(in: Input) = {
      q.apply(in)
    }
  }
}
