package com.github.tarcv.testing.alchemist

import org.kodein.type.TypeToken
import org.kodein.type.erased
import org.kodein.type.erasedComp

inline fun <reified T: Any> Any.getGenericParametersOfSuper(): Array<TypeToken<*>> {
    var superClasses = erasedComp(this::class).getSuper()
    do {
        val foundClass = superClasses.singleOrNull { it.getRaw() == erased<T>() }
        if (foundClass != null) {
            return foundClass.getGenericParameters()
        }
        superClasses = superClasses.flatMap { it.getSuper() }
    } while (superClasses.isNotEmpty())
    throw NoSuchElementException("Superclass ${T::class} not found")
}
