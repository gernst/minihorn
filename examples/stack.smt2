(set-logic HORN)
(set-option :produce-models true)

(declare-fun test_stack.inv (Int Int Int) Bool)
(declare-fun test_pre (Int) Bool)
(declare-fun test_post (Int) Bool)
(declare-fun test_new_0 (Int Int Int Int Int Int) Bool)
(declare-fun test_call_1 (Int Int Int Int Int Int) Bool)
(declare-fun test_call_2 (Int Int Int Int Int Int) Bool)
(declare-fun test_call_3 (Int Int Int Int Int Int) Bool)
(declare-fun test_call_4 (Int Int Int Int Int Int) Bool)

; method test: requires
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> true
        (test_pre x))))

; method test: new Stack
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem Int))
    (=> (and (test_pre x))
        (test_new_0 x 0 0 (- 1) stack.elem z))))

; method test: Stack.push don't track
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_new_0 x stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0))
        (test_call_1 x stack.pops (+ stack.size 1) stack.offs stack.elem z))))

; method test: Stack.push start tracking
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_new_0 x stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0))
        (test_call_1 x stack.pops (+ stack.size 1) 0 x z))))

; method test: Stack.push already tracking
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_new_0 x stack.pops stack.size stack.offs stack.elem z)
             (>= stack.offs 0))
        (test_call_1 x stack.pops (+ stack.size 1) (+ stack.offs 1) stack.elem z))))

; method test: Stack.push don't track
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_1 x stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0))
        (test_call_2 x stack.pops (+ stack.size 1) stack.offs stack.elem z))))

; method test: Stack.push start tracking
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_1 x stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0))
        (test_call_2 x stack.pops (+ stack.size 1) 0 (+ x 1) z))))

; method test: Stack.push already tracking
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_1 x stack.pops stack.size stack.offs stack.elem z)
             (>= stack.offs 0))
        (test_call_2 x stack.pops (+ stack.size 1) (+ stack.offs 1) stack.elem z))))

; method test: Stack.pop precondition
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_2 x stack.pops stack.size stack.offs stack.elem z))
        (< 0 stack.size))))

; method test: Stack.pop precondition
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_2 x stack.pops stack.size stack.offs stack.elem z)
             (= stack.offs 0))
        (test_stack.inv stack.pops x stack.elem))))

; method test: Stack.pop tracked element
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_2 x stack.pops stack.size stack.offs stack.elem z)
             (= stack.offs 0))
        (test_call_3 x (+ stack.pops 1) (- stack.size 1) (- stack.offs 1) stack.elem_ stack.elem))))

; method test: Stack.pop not tracking
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_2 x stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0)
             (test_stack.inv stack.pops x stack.elem_))
        (test_call_3 x (+ stack.pops 1) (- stack.size 1) stack.offs stack.elem stack.elem_))))

; method test: Stack.pop untracked element
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_2 x stack.pops stack.size stack.offs stack.elem z)
             (< 0 stack.offs)
             (test_stack.inv stack.pops x stack.elem_))
        (test_call_3 x (+ stack.pops 1) (- stack.size 1) (- stack.offs 1) stack.elem stack.elem_))))

; method test: assert
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_3 x stack.pops stack.size stack.offs stack.elem z))
        (= z x))))

; method test: Stack.pop precondition
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_3 x stack.pops stack.size stack.offs stack.elem z))
        (< 0 stack.size))))

; method test: Stack.pop precondition
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_3 x stack.pops stack.size stack.offs stack.elem z)
             (= stack.offs 0))
        (test_stack.inv stack.pops x stack.elem))))

; method test: Stack.pop tracked element
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_3 x stack.pops stack.size stack.offs stack.elem z)
             (= stack.offs 0))
        (test_call_4 x (+ stack.pops 1) (- stack.size 1) (- stack.offs 1) stack.elem_ stack.elem))))

; method test: Stack.pop not tracking
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_3 x stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0)
             (test_stack.inv stack.pops x stack.elem_))
        (test_call_4 x (+ stack.pops 1) (- stack.size 1) stack.offs stack.elem stack.elem_))))

; method test: Stack.pop untracked element
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_3 x stack.pops stack.size stack.offs stack.elem z)
             (< 0 stack.offs)
             (test_stack.inv stack.pops x stack.elem_))
        (test_call_4 x (+ stack.pops 1) (- stack.size 1) (- stack.offs 1) stack.elem stack.elem_))))

; method test: assert
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_4 x stack.pops stack.size stack.offs stack.elem z))
        (= z (+ x 1)))))

; method test: return
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_4 x stack.pops stack.size stack.offs stack.elem z))
        (test_post x))))

(check-sat)
(get-model)
