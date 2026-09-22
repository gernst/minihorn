(set-logic HORN)
(set-option :produce-models true)

(declare-fun test_stack.inv (Int Int Int) Bool)
(declare-fun test_pre (Int) Bool)
(declare-fun test_post (Int) Bool)
(declare-fun test_new_0 (Int Int Int Int Int Int) Bool)
(declare-fun test_call_1 (Int Int Int Int Int Int) Bool)
(declare-fun test_call_2 (Int Int Int Int Int Int) Bool)

; requires
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> true
        (test_pre x))))

; new Stack
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (Stack.fresh Int))
    (=> (and (test_pre x))
        (test_new_0 x 0 0 (- 1) Stack.fresh z))))

; Stack.push don't track
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_new_0 x stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0))
        (test_call_1 x stack.pops (+ stack.size 1) stack.offs stack.elem z))))

; Stack.push start tracking
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_new_0 x stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0))
        (test_call_1 x stack.pops (+ stack.size 1) 0 x z))))

; Stack.push already tracking
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_new_0 x stack.pops stack.size stack.offs stack.elem z)
             (>= stack.offs 0))
        (test_call_1 x stack.pops (+ stack.size 1) (+ stack.offs 1) stack.elem z))))

; Stack.pop precondition
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_1 x stack.pops stack.size stack.offs stack.elem z))
        (< 0 stack.size))))

; Stack.pop precondition
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_1 x stack.pops stack.size stack.offs stack.elem z)
             (= stack.offs 0))
        (test_stack.inv stack.pops x stack.elem))))

; Stack.pop tracked element
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_1 x stack.pops stack.size stack.offs stack.elem z)
             (= stack.offs 0))
        (test_call_2 x (+ stack.pops 1) (- stack.size 1) (- stack.offs 1) stack.elem_ stack.elem))))

; Stack.pop not tracking
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_1 x stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0)
             (test_stack.inv stack.pops x stack.elem_))
        (test_call_2 x (+ stack.pops 1) (- stack.size 1) stack.offs stack.elem stack.elem_))))

; Stack.pop untracked element
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_1 x stack.pops stack.size stack.offs stack.elem z)
             (< 0 stack.offs)
             (test_stack.inv stack.pops x stack.elem_))
        (test_call_2 x (+ stack.pops 1) (- stack.size 1) (- stack.offs 1) stack.elem stack.elem_))))

; assert
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_2 x stack.pops stack.size stack.offs stack.elem z))
        (= z x))))

; return
(assert
  (forall ((x Int) (stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_2 x stack.pops stack.size stack.offs stack.elem z))
        (test_post x))))

(check-sat)
(get-model)
