method test(x: int)
{
  var stack: Stack<int>;
  var z: int;

  stack := new Stack<int>();

  stack.push(x);
  stack.push(x + 1);

  z := stack.pop();
  assert z == x + 1;

  z := stack.pop();
  assert z == x;
}