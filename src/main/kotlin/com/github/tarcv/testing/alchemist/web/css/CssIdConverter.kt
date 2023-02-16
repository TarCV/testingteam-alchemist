package com.github.tarcv.testing.alchemist.web.css

import com.github.tarcv.testing.alchemist.Converter
import com.github.tarcv.testing.alchemist.web.Id

object CssIdConverter: Converter<Id, CssSelectorBase> {
    override fun convert(predicate: Id): CssSelectorBase = CssSelector("#${predicate.name}", 2)
}