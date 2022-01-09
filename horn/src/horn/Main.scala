package horn

import java.io.FileOutputStream
import java.io.PrintStream

object Main {
  def main(args: Array[String]) {
    for (file <- args)
      verify(file)
  }

  def verify(file: String) {
    val pos = file.lastIndexOf(".dfy")
    assert(pos >= 0)

    val methods = Prog.fromFile(file)

    val smt = file.dropRight(4) + ".smt2"
    val out = new PrintStream(new FileOutputStream(smt))

    out.println("(set-logic HORN)")
    out.println("(set-option :produce-models true)")

    val horns = methods map { method =>
      new Horn(method)
    }

    val results = horns map { horn =>
      val Problem(decls, clauses) = horn.problem(horns)
      (decls, clauses)
    }

    val (decls, clauses) = results.unzip

    for (decl <- decls.flatten)
      print(out, decl)

    for (clause <- clauses.flatten)
      print(out, clause)

    out.println("(check-sat)")
    out.println("(get-model)")
  }

  def print(out: PrintStream, decl: PredDecl) {
    import decl._

    out.println(
      "(declare-fun " + identifier + " " + types.mkString(
        "(",
        " ",
        ")"
      ) + " Bool)"
    )
  }

  def print(out: PrintStream, clause: Clause) {
    import clause._

    out.println("(assert")
    out.println("  (forall " + vars.mkString("(", " ", ")"))
    out.println("    (=> " + formatPrems)
    out.println("        " + concl + ")))")
  }
}
