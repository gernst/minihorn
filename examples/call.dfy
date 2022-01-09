method callee(x: int)
    returns (y: int)
{
    var i: int;
    y := x;
}

method caller()
    returns (z: int)
{
    var j: int;
    z := callee(0);
}