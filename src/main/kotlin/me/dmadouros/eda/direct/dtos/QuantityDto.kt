package me.dmadouros.eda.direct.dtos

import java.math.BigDecimal

data class QuantityDto(
    val value: BigDecimal,
    val quomCode: String,
)
