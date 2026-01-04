package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.LeaguesInfo
import com.mzs.basket_krk.domain.repository.SeasonRepository

interface GetLeaguesInfo : SuspendOutUseCase<Either<Failure, LeaguesInfo>>

class GetLeaguesInfoUseCase(private val seasonRepository: SeasonRepository) : GetLeaguesInfo {
    override suspend fun invoke(): Either<Failure, LeaguesInfo> = seasonRepository.getLeaguesInfo()
}
