method abs(x: int)
    returns (y: int)
{
    if x > 0 {
        y := x;
    } else {
        y := -x;
    }
}

method caller()
    returns ()
{
    var a: int;
    var b: int;

    b := abs(a);

    assert b >= 0;
}