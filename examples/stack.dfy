method test()
{
  var stack: Stack<int>;
  var z: int;

  stack := new Stack<int>();

  stack.push(1);
  z := stack.pop();
  assert z == 1;
}