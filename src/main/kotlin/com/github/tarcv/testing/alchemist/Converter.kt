package com.github.tarcv.testing.alchemist

import org.kodein.type.TypeToken

interface Converter<IN: Predicate, OUT: Selector> {
    fun inPredicate(): TypeToken<IN> {
        @Suppress("UNCHECKED_CAST")
        return getGenericParametersOfSuper<Converter<*, *>>()[0] as TypeToken<IN>
    }
    fun outSelector(): TypeToken<OUT> {
        @Suppress("UNCHECKED_CAST")
        return getGenericParametersOfSuper<Converter<*, *>>()[1] as TypeToken<OUT>
    }

    fun convert(predicate: IN): OUT?

    fun convertRaw(predicate: Any): OUT? {
        @Suppress("UNCHECKED_CAST")
        return convert(predicate as IN)
    }
}

