package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.SeasonsInfo
import com.mzs.basket_krk.domain.repository.SeasonRepository

interface GetSeasonsInfo : SuspendOutUseCase<Either<Failure, SeasonsInfo>>

class GetSeasonsInfoUseCase(private val seasonRepository: SeasonRepository) : GetSeasonsInfo {
    override suspend fun invoke(): Either<Failure, SeasonsInfo> = seasonRepository.getSeasonsInfo()
}
