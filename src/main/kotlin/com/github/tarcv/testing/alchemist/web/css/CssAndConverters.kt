package com.github.tarcv.testing.alchemist.web.css

import com.github.tarcv.testing.alchemist.CompilerProvider
import com.github.tarcv.testing.alchemist.Converter
import com.github.tarcv.testing.alchemist.Predicate
import com.github.tarcv.testing.alchemist.common.AllOf
import com.github.tarcv.testing.alchemist.common.HasAscendantWith
import com.github.tarcv.testing.alchemist.common.HasParentWith
import org.kodein.type.generic

abstract class CssAndConverterBase<T: Predicate>(
    compilerProvider: CompilerProvider,
    private val andSuffix: AndSuffix
): Converter<T, CssSelectorBase> {
    private val compiler by lazy { compilerProvider() }

    override fun convert(predicate: T): CssSelectorBase? {
        val andParts = compiler.convertInternal(
            getSubpredicates(predicate).toTypedArray(),
            setOf(generic<CssSelectorBase>())
        ) as List<CssSelectorBase>? ?: return null
        return CssPartialAndSelector(
            andParts,
            andSuffix
        )
    }

    protected abstract fun getSubpredicates(predicate: T): List<Predicate>
}

class CssAndConverter(compilerProvider: CompilerProvider)
    : CssAndConverterBase<AllOf>(compilerProvider, AndSuffix.SIMPLE) {
    override fun getSubpredicates(predicate: AllOf): List<Predicate> {
        return predicate.predicates
    }
}

class CssHasAscendantWithConverter(compilerProvider: CompilerProvider)
    : CssAndConverterBase<HasAscendantWith>(compilerProvider, AndSuffix.ASCENDANT) {
    override fun getSubpredicates(predicate: HasAscendantWith): List<Predicate> {
        return predicate.predicates
    }
}

class CssHasParentWithConverter(compilerProvider: CompilerProvider)
    : CssAndConverterBase<HasParentWith>(compilerProvider, AndSuffix.PARENT) {
    override fun getSubpredicates(predicate: HasParentWith): List<Predicate> {
        return predicate.predicates
    }
}
