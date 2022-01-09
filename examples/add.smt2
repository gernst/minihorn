(set-logic HORN)
(set-option :produce-models true)
(declare-fun add_pre_0 (Int Int Int Int) Bool)
(declare-fun add_post_1 (Int Int Int Int) Bool)
(declare-fun add_assign_2 (Int Int Int Int) Bool)
(declare-fun add_assign_3 (Int Int Int Int) Bool)
(declare-fun add_invariant_4 (Int Int Int Int) Bool)
(declare-fun add_assume_5 (Int Int Int Int) Bool)
(declare-fun add_assign_6 (Int Int Int Int) Bool)
(declare-fun add_assign_7 (Int Int Int Int) Bool)
(declare-fun add_assume_8 (Int Int Int Int) Bool)
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (>= x 0))
        (add_pre_0 x y s r))))
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (add_pre_0 x y s r))
        (add_assign_2 x y s x))))
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (add_assign_2 x y s r))
        (add_assign_3 x y y r))))
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (add_assign_3 x y s r))
        (add_invariant_4 x y s r))))
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (add_invariant_4 x y s r)
             (> r 0))
        (add_assume_5 x y s r))))
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (add_assume_5 x y s r))
        (add_assign_6 x y s (- r 1)))))
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (add_assign_6 x y s r))
        (add_assign_7 x y (+ s 1) r))))
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (add_assign_7 x y s r))
        (add_invariant_4 x y s r))))
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (add_invariant_4 x y s r)
             (not (> r 0)))
        (add_assume_8 x y s r))))
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (add_assume_8 x y s r))
        (add_post_1 x y s r))))
(assert
  (forall ((x Int) (y Int) (s Int) (r Int))
    (=> (and (add_post_1 x y s r))
        (= s (+ x y)))))
(check-sat)
(get-model)
