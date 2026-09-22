package horn

object Builtin {
  def preds(
      var_ : String,
      adt: String,
      params: List[Type],
      inputs: List[Type]
  ): List[(String, List[Type])] = {
    (adt, params) match {
      case ("Stack", List(typ)) =>
        List((var_ + ".inv", List(Sort.int) ++ inputs ++ List(typ)))

      case _ =>
        ???
    }
  }

  def state(var_ : String, adt: String, params: List[Type]): List[VarDecl] = {
    (adt, params) match {
      case ("Stack", List(typ)) =>
        List(
          VarDecl(Var(var_ + ".pops"), Sort.int),
          VarDecl(Var(var_ + ".size"), Sort.int),
          VarDecl(Var(var_ + ".offs"), Sort.int), // -1 if no entry
          VarDecl(Var(var_ + ".elem"), typ)
        )

      case _ =>
        ???
    }
  }

  def obj(var_ : String, adt: String, params: List[Type]): List[Var] = {
    (adt, params) match {
      case ("Stack", List(typ)) =>
        List(
          Var(var_ + ".pops"),
          Var(var_ + ".size"),
          Var(var_ + ".offs"),
          Var(var_ + ".elem")
        )

      case _ =>
        ???
    }
  }

  def new_(
      adt: String,
      params: List[Type],
      args: List[Expr]
  ): (List[VarDecl], List[Expr]) = {
    (adt, params) match {
      case ("Stack", List(typ)) =>
        val elem = Var(adt + ".fresh")
        val fresh = List(VarDecl(elem, typ))
        val rhs = List(Num(0), Num(0), Num(-1), elem)
        (fresh, rhs)

      case _ =>
        ???
    }
  }

  def call(
    method: String,
      var_ : String,
      adt: String,
      params: List[Type],
      obj: List[Var],
      op: String,
      args: List[Expr],
      inputs: List[Expr]
  ): (
      List[(List[VarDecl], List[Expr], Expr)],
      List[(List[VarDecl], List[Expr], List[Expr], List[Expr], String)]
  ) = {
    (adt, params, obj, op, args) match {
      case (
            "Stack",
            List(typ),
            List(pops, size, offs, elem),
            "push",
            List(arg)
          ) =>
        val checks = List()

        val size_ = App("+", List(size, Num(1)))
        val offs_ = App("+", List(offs, Num(1)))

        val untracked = List(App("<", List(offs, Num(0))))
        val tracked = List(App(">=", List(offs, Num(0))))

        val transitions = List(
          // don't track this push
          (Nil, Nil, List(pops, size_, offs, elem), untracked, "don't track"),

          // don't track this push
          (
            Nil,
            Nil,
            List(pops, size_, Num(0), arg),
            untracked,
            "start tracking"
          ),

          // already trackaing some other element
          (
            Nil,
            Nil,
            List(pops, size_, offs_, elem),
            tracked,
            "already tracking"
          )
        )

        (checks, transitions)

      case ("Stack", List(typ), List(pops, size, offs, elem), "pop", List()) =>

        val pops_ = App("+", List(pops, Num(1)))
        val size_ = App("-", List(size, Num(1)))
        val offs_ = App("-", List(offs, Num(1)))
        val elem_ = Var(elem.name + "_") // fresh nondet value

        val untracked = List(App("<", List(offs, Num(0))))
        val tracked = List(App(">=", List(offs, Num(0))))
        val here = List(App("==", List(offs, Num(0))))
        val there = List(App("<", List(Num(0), offs)))

        def inv(x: Expr) =
          App(method + "_" + var_ + ".inv", List(pops) ++ inputs ++ List(x))

        val checks = List(
          (List(), List(), App("<", List(Num(0), size))),
          (List(), here, inv(elem))
        )

        val transitions = List(
          (
            List(VarDecl(elem_, typ)),
            List(elem),
            List(pops_, size_, offs_, elem_),
            here,
            "tracked element"
          ),
          (
            List(VarDecl(elem_, typ)),
            List(elem_),
            List(pops_, size_, offs, elem), // don't change offset
            untracked ++ List(inv(elem_)),
            "not tracking"
          ),
          (
            List(VarDecl(elem_, typ)),
            List(elem_),
            List(pops_, size_, offs_, elem),
            there ++ List(inv(elem_)),
            "untracked element"
          )
        )

        (checks, transitions)

      case _ =>
        ???
    }
  }
}
