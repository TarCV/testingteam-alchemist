package com.github.tarcv.testing.alchemist.web.css

import com.github.tarcv.testing.alchemist.Combiner

class CssCombiner : Combiner<CssSelectorBase> {
    private var recursionLock = false

    override fun combine(selectors: List<CssSelectorBase>): CssSelector? {
        require(!recursionLock)
        recursionLock = true
        try {
            val rootSelector = CssPartialAndSelector(selectors, AndSuffix.SIMPLE)
            val branches = collectPredicates(rootSelector) ?: return null
            return CssSelector(
                branches.joinToString(", ") { it.locator },
                branches.sumOf { it.cost }
            )
        } finally {
            recursionLock = false
        }
    }

    private fun collectPredicates(
        predicate: CssSelectorBase
    ): List<CssSelector>? {
        val orBranches = mutableListOf<CssSelector>()
        orBranches.addAll(
            when (predicate) {
                is CssPartialOrSelector -> predicate.orBranches.flatMap { collectPredicates(it) ?: return null }
                is CssPartialAndSelector -> processAndPredicate(predicate.andParts, predicate.suffix) ?: return null
                is CssSelector -> listOf(predicate)
            } as Collection<CssSelector>
        )
        return orBranches
    }

    private fun processAndPredicate(
        subPredicates: List<CssSelectorBase>,
        ascendantSuffix: AndSuffix,
    ): List<CssSelector>? {
        return subPredicates
            .map { collectPredicates(it) ?: return null }
            .reduce { acc, item ->
                acc.flatMap { l ->
                    item.map { r ->
                        CssSelector(
                            l.locator + r.locator,
                            maxOf(l.cost, r.cost)
                        )
                    }
                }
            }
            .map { 
                it.copy(
                    locator = it.locator + ascendantSuffix.cssOperator
                )
            }
    }
}