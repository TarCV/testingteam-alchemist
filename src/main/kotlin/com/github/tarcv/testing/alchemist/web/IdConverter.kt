package com.github.tarcv.testing.alchemist.web

import com.github.tarcv.testing.alchemist.Combiner
import com.github.tarcv.testing.alchemist.Converter

object IdConverter : Converter<Id, IdSelector>, Combiner<IdSelector> {
    override fun convert(predicate: Id): IdSelector = IdSelector(predicate.name)
    override fun combine(selectors: List<IdSelector>): IdSelector? = selectors.singleOrNull()
}