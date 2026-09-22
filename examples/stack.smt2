(set-logic HORN)
(set-option :produce-models true)

(declare-fun test_stack.inv (Int Int) Bool)
(declare-fun test_pre () Bool)
(declare-fun test_post () Bool)
(declare-fun test_new_0 (Int Int Int Int Int) Bool)
(declare-fun test_call_1 (Int Int Int Int Int) Bool)
(declare-fun test_call_2 (Int Int Int Int Int) Bool)

; requires
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> true
        test_pre)))

; new Stack
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (Stack.fresh Int))
    (=> (and test_pre)
        (test_new_0 0 0 (- 1) Stack.fresh z))))

; Stack.push don't track
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_new_0 stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0))
        (test_call_1 stack.pops (+ stack.size 1) stack.offs stack.elem z))))

; Stack.push start tracking
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_new_0 stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0))
        (test_call_1 stack.pops (+ stack.size 1) 0 1 z))))

; Stack.push already tracking
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_new_0 stack.pops stack.size stack.offs stack.elem z)
             (>= stack.offs 0))
        (test_call_1 stack.pops (+ stack.size 1) (+ stack.offs 1) stack.elem z))))

; Stack.pop precondition
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_1 stack.pops stack.size stack.offs stack.elem z))
        (< 0 stack.size))))

; Stack.pop precondition
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_1 stack.pops stack.size stack.offs stack.elem z)
             (= stack.offs 0))
        (test_stack.inv stack.pops stack.elem))))

; Stack.pop tracked element
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_1 stack.pops stack.size stack.offs stack.elem z)
             (= stack.offs 0))
        (test_call_2 (+ stack.pops 1) (- stack.size 1) (- stack.offs 1) stack.elem_ stack.elem))))

; Stack.pop not tracking
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_1 stack.pops stack.size stack.offs stack.elem z)
             (< stack.offs 0)
             (test_stack.inv stack.pops stack.elem_))
        (test_call_2 (+ stack.pops 1) (- stack.size 1) stack.offs stack.elem stack.elem_))))

; Stack.pop untracked element
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int) (stack.elem_ Int))
    (=> (and (test_call_1 stack.pops stack.size stack.offs stack.elem z)
             (< 0 stack.offs)
             (test_stack.inv stack.pops stack.elem_))
        (test_call_2 (+ stack.pops 1) (- stack.size 1) (- stack.offs 1) stack.elem stack.elem_))))

; assert
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_2 stack.pops stack.size stack.offs stack.elem z))
        (= z 1))))

; return
(assert
  (forall ((stack.pops Int) (stack.size Int) (stack.offs Int) (stack.elem Int) (z Int))
    (=> (and (test_call_2 stack.pops stack.size stack.offs stack.elem z))
        test_post)))

(check-sat)
(get-model)
