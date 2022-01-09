(set-logic HORN)
(set-option :produce-models true)
(declare-fun callee_pre (Int) Bool)
(declare-fun callee_post (Int Int) Bool)
(declare-fun callee_assign_2 (Int Int Int) Bool)
(declare-fun caller_pre () Bool)
(declare-fun caller_post (Int) Bool)
(declare-fun caller_call_2 (Int Int) Bool)
(assert
  (forall ((x Int) (y Int) (i Int))
    (=> true
        (callee_pre x))))
(assert
  (forall ((x Int) (y Int) (i Int))
    (=> (and (callee_pre x))
        (callee_assign_2 x x i))))
(assert
  (forall ((x Int) (y Int) (i Int))
    (=> (and (callee_assign_2 x y i))
        (callee_post x y))))
(assert
  (forall ((z Int) (j Int))
    (=> true
        caller_pre)))
(assert
  (forall ((z Int) (j Int))
    (=> (and caller_pre
             (callee_post 0 z))
        (caller_call_2 z j))))
(assert
  (forall ((z Int) (j Int))
    (=> (and (caller_call_2 z j))
        (caller_post z))))
(check-sat)
(get-model)
