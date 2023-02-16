package com.github.tarcv.testing.alchemist.web.css

import com.github.tarcv.testing.alchemist.Converter
import com.github.tarcv.testing.alchemist.web.Attribute

object CssAttributeConverter: Converter<Attribute, CssSelectorBase> {
    override fun convert(predicate: Attribute): CssSelectorBase = CssSelector("[${predicate.name}=${predicate.value}]", 2)
}