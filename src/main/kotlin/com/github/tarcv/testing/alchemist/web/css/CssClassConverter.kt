package com.github.tarcv.testing.alchemist.web.css

import com.github.tarcv.testing.alchemist.Converter
import com.github.tarcv.testing.alchemist.web.ClassName

object CssClassConverter: Converter<ClassName, CssSelectorBase> {
    override fun convert(predicate: ClassName): CssSelectorBase = CssSelector(".${predicate.name}", 2)
}