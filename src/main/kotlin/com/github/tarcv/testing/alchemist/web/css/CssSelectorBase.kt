package com.github.tarcv.testing.alchemist.web.css

import com.github.tarcv.testing.alchemist.Selector

sealed interface CssSelectorBase: Selector
data class CssSelector(
    val locator: String,
    override val cost: Int
): CssSelectorBase {
    override fun toString(): String = "${this::class.simpleName}: $locator ($cost)"
}

data class CssOrPartialSelector(
    val locators: List<CssSelector>
): CssSelectorBase {
    override val cost = -1
}