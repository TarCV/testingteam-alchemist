package com.github.tarcv.testing.alchemist.web.css

import com.github.tarcv.testing.alchemist.CompilerProvider
import com.github.tarcv.testing.alchemist.Converter
import com.github.tarcv.testing.alchemist.Predicate
import org.kodein.type.generic

class CssOrConverter(compilerProvider: CompilerProvider): Converter<Predicate.Or, CssSelectorBase> {
    private val compiler by lazy { compilerProvider() }

    override fun convert(predicate: Predicate.Or): CssOrPartialSelector? {
        val allOrVariants = mutableListOf<Predicate>()
        if (!collectOrVariants(allOrVariants, listOf(predicate))) {
            return null
        }
        return CssOrPartialSelector(
            allOrVariants.map {
                compiler.compileRaw(arrayOf(it), setOf(generic<CssSelectorBase>()))
                    .minByOrNull { it.cost }
                    ?.let { it as CssSelector }
                    ?: return null
            }
        )
    }

    private  inline fun <T: Any, reified S: T> List<T>.partitionIsInstance(): Pair<List<S>, List<T>> {
        val (part1, part2) = partition { it is S }

        @Suppress("UNCHECKED_CAST")
        return Pair(part1 as List<S>, part2)
    }

    private tailrec fun collectOrVariants(result: MutableList<Predicate>, orPredicates: List<Predicate.Or>): Boolean {
        val (innerOrPredicates, otherPredicates) = orPredicates
            .flatMap { it.predicates }
            .partitionIsInstance<Predicate, Predicate.Or>()
        result.addAll(otherPredicates)

        if (innerOrPredicates.isEmpty()) {
            return true
        }
        return collectOrVariants(result, innerOrPredicates)
    }
}