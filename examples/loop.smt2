(set-logic HORN)
(set-option :produce-models true)
(declare-fun loop_pre_0 (Int Int) Bool)
(declare-fun loop_post_1 (Int Int) Bool)
(declare-fun loop_assign_2 (Int Int) Bool)
(declare-fun loop_invariant_3 (Int Int) Bool)
(declare-fun loop_assume_4 (Int Int) Bool)
(declare-fun loop_assign_5 (Int Int) Bool)
(declare-fun loop_assume_6 (Int Int) Bool)
(assert
  (forall ((n Int) (i Int))
    (=> (and (>= n 0))
        (loop_pre_0 n i))))
(assert
  (forall ((n Int) (i Int))
    (=> (and (loop_pre_0 n i))
        (loop_assign_2 n 0))))
(assert
  (forall ((n Int) (i Int))
    (=> (and (loop_assign_2 n i))
        (loop_invariant_3 n i))))
(assert
  (forall ((n Int) (i Int))
    (=> (and (loop_invariant_3 n i) (< i n))
        (loop_assume_4 n i))))
(assert
  (forall ((n Int) (i Int))
    (=> (and (loop_assume_4 n i))
        (loop_assign_5 n (+ i 1)))))
(assert
  (forall ((n Int) (i Int))
    (=> (and (loop_assign_5 n i))
        (loop_invariant_3 n i))))
(assert
  (forall ((n Int) (i Int))
    (=> (and (loop_invariant_3 n i) (not (< i n)))
        (loop_assume_6 n i))))
(assert
  (forall ((n Int) (i Int))
    (=> (and (loop_assume_6 n i))
        (loop_post_1 n i))))
(assert
  (forall ((n Int) (i Int))
    (=> (and (loop_post_1 n i))
        (= i n))))
(check-sat)
(get-model)
