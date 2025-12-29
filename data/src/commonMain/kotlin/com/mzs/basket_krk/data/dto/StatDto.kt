package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.Stat
import kotlinx.serialization.Serializable

@Serializable
data class StatDto(
    val m1: Int,
    val a1: Int,
    val m2: Int,
    val a2: Int,
    val m3: Int,
    val a3: Int,
    val pt: Int,
    val a: Int,
    val r: Int,
    val b: Int,
    val s: Int,
    val t: Int,
    val fl: Int,
    val tch: Int,
    val eff: Int,
    val m: Int? = null,
    val sec: Int? = null,
    val flon: Int? = null,
    val pm: Int? = null,
    val mvp: Int? = null,
    val d_r: Int? = null,
    val o_r: Int? = null,
    val num: Int? = null
)

fun StatDto.toDomain() = Stat(
    m1 = m1,
    a1 = a1,
    m2 = m2,
    a2 = a2,
    m3 = m3,
    a3 = a3,
    pt = pt,
    a = a,
    r = r,
    b = b,
    s = s,
    t = t,
    fl = fl,
    tch = tch,
    eff = eff,
    m = m,
    sec = sec,
    flon = flon,
    pm = pm,
    mvp = mvp,
    dR = d_r,
    oR = o_r,
    num = num
)
