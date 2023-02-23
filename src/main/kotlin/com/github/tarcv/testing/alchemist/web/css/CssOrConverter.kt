package com.github.tarcv.testing.alchemist.web.css

import com.github.tarcv.testing.alchemist.CompilerProvider
import com.github.tarcv.testing.alchemist.Converter
import com.github.tarcv.testing.alchemist.common.AnyOf
import org.kodein.type.generic

class CssOrConverter(compilerProvider: CompilerProvider) : Converter<AnyOf, CssSelectorBase> {
    private val compiler by lazy { compilerProvider() }

    override fun convert(predicate: AnyOf): CssSelectorBase? {
        return CssPartialOrSelector(
            compiler.convertInternal(
                predicate.predicates.toTypedArray(),
                setOf(generic<CssSelectorBase>())
            ) as List<CssSelectorBase>? ?: return null
        )
    }
}
