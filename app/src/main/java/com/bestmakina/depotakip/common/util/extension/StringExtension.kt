package com.bestmakina.depotakip.common.util.extension

fun String.removeLeadingZeros(): String {
    return this.trimStart { it == '0' }
}