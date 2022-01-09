method add(x: int, y: int)
    returns (s: int)
    requires x >= 0;
    ensures  s == x + y;
{
    var r: int;
    r := x;
    s := y;

    while r > 0 {
        r := r - 1;
        s := s + 1;
    }
}