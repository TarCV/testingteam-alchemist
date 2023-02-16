package com.github.tarcv.testing.alchemist

import org.kodein.type.TypeToken
import org.kodein.type.erasedOf

typealias CompilerProvider = () -> Compiler
class Compiler(
    private val converters: Set<Converter<*, *>>,
    private val combiners: Set<Combiner<*>>
) {

    fun compile(vararg predicates: Predicate): Selector {
        return compileRaw(predicates)
            .minByOrNull { it.cost }
            ?: throw IllegalArgumentException(
                "Combination not supported: ${predicates.joinToString { it::class.simpleName ?: "?" }}"
            )
    }

    @JvmOverloads
    fun compileRaw(
        predicates: Array<out Predicate>,
        requireOutSelector: Set<TypeToken<out Selector>>? = null
    ): Set<Selector> {
        return predicates
            .map { p ->
                converters.filter { it.inPredicate().isAssignableFrom(erasedOf(p)) }
            }
            .let {
                val possibleOutTypes = requireOutSelector ?: it.fold(
                    it.firstOrNull()
                        ?.map { it.outSelector() }
                        ?.toSet()
                        ?: setOf()
                ) { acc, item ->
                    acc.intersect(
                        item
                            .map { it.outSelector() }
                            .toSet()
                    )
                }

                it.map { predicateConverterCandidates ->
                    predicateConverterCandidates.filter { converter ->
                        possibleOutTypes.any { outType ->
                            outType.isAssignableFrom(converter.outSelector())
                        }
                    }
                }
            }
            .zip(predicates) { compatibleConverters, predicate ->
                compatibleConverters
                    .mapNotNull { it.convertRaw(predicate) }
                    .minByOrNull { it.cost }
                    ?: throw IllegalArgumentException(
                        "Combination not supported: ${predicates.joinToString { it::class.simpleName ?: "?" }}"
                    )
            }
            .let { converted ->
                combiners
                    .filter {
                        it.inSelector().isAssignableFrom(
                            // TODO: find common super class among all 'converted'
                            erasedOf(converted.first())
                        )
                    }
                    .mapNotNull {
                        @Suppress("UNCHECKED_CAST")
                        (it as Combiner<Selector>).combine(converted)
                    }
            }
            .toSet()
    }
}