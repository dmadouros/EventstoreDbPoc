package me.dmadouros.eda.scout.dtos

import java.math.BigDecimal

data class DenormalizeQuantityDto(
    val value: BigDecimal? = null,
    val quom: DenormalizedQuomDto = DenormalizedQuomDto(),
)
