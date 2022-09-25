infix fun <P1, R, R2> Function1<P1, R>.then(fn: (R) -> R2):  (P1) -> R2 = {
    fn(this(it))
}

object ReaderMonad {
    data class Reader<From, To>(val f: (From) -> To) {
        operator fun invoke(input: From): To = f(input)
        fun <NewTo>map(mapper: (To) -> NewTo): Reader<From, NewTo> = Reader { mapper(f(it)) }
        fun <NewTo>flatMap(mapper: (To) -> Reader<From, NewTo> ): Reader<From, NewTo> = Reader { mapper(f(it))(it) }
    }
    fun <From, To> pure(arg: To): Reader<From, To> = Reader { arg }
}