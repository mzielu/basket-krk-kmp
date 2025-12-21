package com.mzs.basket_krk.domain.base

interface InOutUseCase<in Input, out Output> {
    operator fun invoke(input: Input): Output
}

interface InUseCase<in Input> {
    operator fun invoke(input: Input)
}

interface OutUseCase<out Output> {
    operator fun invoke(): Output
}

interface UseCase {
    operator fun invoke()
}

interface SuspendInOutUseCase<in Input, out Output> {
    suspend operator fun invoke(input: Input): Output
}

interface SuspendInUseCase<in Input> {
    suspend operator fun invoke(input: Input)
}

interface SuspendOutUseCase<out Output> {
    suspend operator fun invoke(): Output
}

interface SuspendUseCase {
    suspend operator fun invoke()
}
