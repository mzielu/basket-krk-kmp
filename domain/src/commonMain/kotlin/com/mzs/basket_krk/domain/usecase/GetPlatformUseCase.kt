package com.mzs.basket_krk.domain.usecase

import com.mzs.basket_krk.domain.base.OutUseCase
import com.mzs.basket_krk.domain.model.Platform

interface GetPlatform : OutUseCase<Platform>

class GetPlatformUseCase() : GetPlatform {
    override fun invoke(): Platform {
        return platformName
    }
}

expect val platformName: Platform