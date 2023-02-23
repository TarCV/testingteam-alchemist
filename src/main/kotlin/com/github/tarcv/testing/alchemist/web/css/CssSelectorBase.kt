package com.github.tarcv.testing.alchemist.web.css

import com.github.tarcv.testing.alchemist.Selector

sealed interface CssSelectorBase: Selector
data class CssSelector(
    val locator: String,
    override val cost: Int
): CssSelectorBase, BranchOrSimpleSelector {
    override fun toString(): String = "${this::class.simpleName}: $locator ($cost)"
}

sealed interface BranchOrSimpleSelector: CssSelectorBase
sealed interface CombinationOrSimpleSelector: CssSelectorBase

data class CssPartialAndSelector(
    val andParts: List<CssSelectorBase>,
    val suffix: AndSuffix
): CombinationOrSimpleSelector {
    override val cost = -1
}

data class CssPartialOrSelector(
    val orBranches: List<CssSelectorBase>,
): BranchOrSimpleSelector {
    override val cost = -1
}
enum class AndSuffix(val cssOperator: String) {
    ASCENDANT(" "),
    PARENT(">"),
    SIMPLE("")
}