method loop(n: int)
    returns (i: int)
    requires n >= 0;
    ensures  i == n;
{
    i := 0;

    while i < n {
        i := i + 1;
    }
}