package com.github.tarcv.testing.alchemist.web.css

import com.github.tarcv.testing.alchemist.Combiner

object CssCombiner: Combiner<CssSelectorBase> {
    override fun combine(selectors: List<CssSelectorBase>): CssSelector {
        return selectors
            .map {
                when (it) {
                    is CssOrPartialSelector -> it.locators
                    is CssSelector -> listOf(it)
                }
            }
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
            .let {
                CssSelector(
                    it.joinToString(", ") { it.locator },
                    it.sumOf { it.cost }
                )
            }
    }
}