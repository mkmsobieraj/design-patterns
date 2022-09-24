infix fun <P1, R, R2> Function1<P1, R>.then(fn: (R) -> R2):  (P1) -> R2 = {
    fn(this(it))
}