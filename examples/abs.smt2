(set-logic HORN)
(set-option :produce-models true)
(declare-fun abs_pre_0 (Int Int) Bool)
(declare-fun abs_post_1 (Int Int) Bool)
(declare-fun abs_join_2 (Int Int) Bool)
(declare-fun abs_assume_3 (Int Int) Bool)
(declare-fun abs_assign_4 (Int Int) Bool)
(declare-fun abs_assume_5 (Int Int) Bool)
(declare-fun abs_assign_6 (Int Int) Bool)
(assert
  (forall ((x Int) (y Int))
    (=> true
        (abs_pre_0 x y))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_pre_0 x y)
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
    (=> (and (abs_pre_0 x y)
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
        (abs_post_1 x y))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_post_1 x y))
        (>= y 0))))
(assert
  (forall ((x Int) (y Int))
    (=> (and (abs_post_1 x y))
        (or (= y x) (= y (- x))))))
(check-sat)
(get-model)
