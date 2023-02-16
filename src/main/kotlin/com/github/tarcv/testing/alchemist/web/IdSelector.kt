package com.github.tarcv.testing.alchemist.web

import com.github.tarcv.testing.alchemist.Selector

data class IdSelector(
    val locator: String,
): Selector {
    override val cost: Int = 1
    override fun toString(): String = "${this::class.simpleName}: $locator ($cost)"
}