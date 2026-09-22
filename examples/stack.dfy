method test(x: int)
{
  var stack: Stack<int>;
  var z: int;

  stack := new Stack<int>();

  stack.push(x);
  z := stack.pop();
  assert z == x;
}