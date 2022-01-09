package horn

import scala.collection.mutable

case class Problem(decls: List[PredDecl], clauses: List[Clause])

case class PredDecl(
    method: String,
    name: String,
    index: Int,
    args: List[Type]
) {
  val identifier = method + "_" + name + "_" + index
  def apply(args: List[Expr]) = App(identifier, args)
}

case class Clause(vars: List[VarDecl], prems: List[Expr], concl: Expr) {
  def formatPrems = if (prems.isEmpty) "true"
  else prems.mkString("(and ", "\n             ", ")")
}

class Horn(method: Method) {
  val Method(_, formals, results, requires, ensures, Body(locals, progs)) =
    method

  def problem: Problem = {
    ???
  }
}
