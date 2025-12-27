package com.mzs.basket_krk.domain.base

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure

fun <L, R> Either<L, R>.always(block: () -> Unit): Either<L, R> {
    block()
    return this
}

fun <L, R> Either<L, R>.onSuccess(block: (R) -> Unit): Either<L, R> {
    map { block(it) }
    return this
}

suspend fun <L, R> Either<L, R>.onSuspendSuccess(block: suspend (R) -> Unit): Either<L, R> {
    map { block(it) }
    return this
}

fun <L : Throwable, R> Either<L, R>.onGeneralError(block: (L) -> Unit): Either<L, R> {
    mapLeft { block(it) }
    return this
}

suspend fun <L : Throwable, R> Either<L, R>.onSuspendGeneralError(block: suspend (L) -> Unit): Either<L, R> {
    mapLeft { block(it) }
    return this
}

fun <L : Error, R> Either<L, R>.rightOrThrow(): R =
    fold(
        ifLeft = { throw it },
        ifRight = { it },
    )

inline fun <T> Either.Companion.catchWithError(f: () -> T): Either<Failure, T> =
    catch(f).mapLeft {
        it as? Failure ?: Failure.UnknownError(it)
    }
