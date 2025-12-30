package com.mzs.basket_krk.datautils

import com.mzs.basket_krk.domain.model.Stat

object StatFakeData {
    fun stat(
        m1: Int = 10,
        m2: Int = 5,
        m3: Int = 7,
        a1: Int = 4,
        a2: Int = 6,
        a3: Int = 3,
        r: Int = 8,
        pt: Int = 39,
        a: Int = 13,
        b: Int = 2,
        s: Int = 1,
        t: Int = 0,
        fl: Int = 3,
        tch: Int = 12,
        eff: Int = 45,
        num: Int? = 23
    ) = Stat(
        m1 = m1,
        m2 = m2,
        m3 = m3,
        a1 = a1,
        a2 = a2,
        a3 = a3,
        r = r,
        b = b,
        pt = pt,
        a = a,
        s = s,
        t = t,
        fl = fl,
        tch = tch,
        eff = eff,
        num = num
    )
}
