(set-logic HORN)
(set-option :produce-models true)
(declare-fun abs_pre (Int) Bool)
(declare-fun abs_post (Int Int) Bool)
(declare-fun abs_join_2 (Int Int) Bool)
(declare-fun abs_assume_3 (Int Int) Bool)
(declare-fun abs_assign_4 (Int Int) Bool)
(declare-fun abs_assume_5 (Int Int) Bool)
(declare-fun abs_assign_6 (Int Int) Bool)
(declare-fun caller_pre () Bool)
(declare-fun caller_post () Bool)
(declare-fun caller_call_2 (Int Int) Bool)
(assert
  (forall ((x Int) (y Int))
    (=> true
        (abs_pre x))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_pre x)
             (> x 0))
        (abs_assume_3 x y))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_assume_3 x y))
        (abs_assign_4 x x))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_assign_4 x y))
        (abs_join_2 x y))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_pre x)
             (not (> x 0)))
        (abs_assume_5 x y))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_assume_5 x y))
        (abs_assign_6 x (- x)))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_assign_6 x y))
        (abs_join_2 x y))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_join_2 x y))
        (abs_post x y))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_post x y))
        (>= y 0))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_post x y))
        (or (= y x) (= y (- x))))))
(assert
  (forall ((a Int) (b Int))
    (=> true
        caller_pre)))
(assert
  (forall ((a Int) (b Int))
    (=> (and caller_pre
             (abs_post a b))
        (caller_call_2 a b))))
(assert
  (forall ((a Int) (b Int))
    (=> (and (caller_call_2 a b))
        (>= b 0))))
(assert
  (forall ((a Int) (b Int))
    (=> (and (caller_call_2 a b))
        caller_post)))
(check-sat)
(get-model)
