package com.github.tarcv.testing.alchemist

import org.kodein.type.TypeToken
import org.kodein.type.erased
import org.kodein.type.erasedComp

inline fun <reified T: Any> Any.getGenericParametersOfSuper(): Array<TypeToken<*>> {
    return erasedComp(this::class)
        .getSuper()
        .single { it.getRaw() == erased<T>() }
        .getGenericParameters()
}
