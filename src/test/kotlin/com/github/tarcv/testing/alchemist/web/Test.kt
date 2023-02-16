package com.github.tarcv.testing.alchemist.web

import com.github.tarcv.testing.alchemist.Combiner
import com.github.tarcv.testing.alchemist.Compiler
import com.github.tarcv.testing.alchemist.CompilerProvider
import com.github.tarcv.testing.alchemist.Converter
import com.github.tarcv.testing.alchemist.Predicate
import com.github.tarcv.testing.alchemist.web.css.CssAttributeConverter
import com.github.tarcv.testing.alchemist.web.css.CssClassConverter
import com.github.tarcv.testing.alchemist.web.css.CssCombiner
import com.github.tarcv.testing.alchemist.web.css.CssIdConverter
import com.github.tarcv.testing.alchemist.web.css.CssOrConverter
import com.github.tarcv.testing.alchemist.web.css.CssSelector
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.bindSet
import org.kodein.di.instance
import org.kodein.di.singleton
import kotlin.test.Test
import kotlin.test.assertEquals

class Test {
    class CompilerDi(override val di: DI) : DIAware, CompilerProvider {
        private val compiler: Compiler by instance()

        override fun invoke(): Compiler = compiler
    }

    private val di = DI {
        bind<CompilerProvider> { singleton { CompilerDi(di) }}
        bindSet<Converter<*, *>> { 
            add { instance(IdConverter)  }
            add { instance(CssIdConverter) }
            add { instance(CssAttributeConverter) }
            add { instance(CssClassConverter) }
            add { singleton { CssOrConverter(instance()) } }
        }
        bindSet<Combiner<*>> {
            add { instance(IdConverter) }
            add { instance(CssCombiner) }
        }
        bind<Compiler> { singleton {
            Compiler(instance(), instance())
        } }
    }
    private val compiler: Compiler by di.instance()

    @Test
    fun simpleId() {
        assertEquals(
            IdSelector("foo"),
            compiler.compile(Id("foo"))
        )
    }

    @Test
    fun simpleCssClass() {
        assertEquals(
            CssSelector(".foo", 2),
            compiler.compile(ClassName("foo"))
        )
    }

    @Test
    fun cssClassAndAttribute() {
        assertEquals(
            CssSelector(".foo[foo=bar]", 2),
            compiler.compile(
                ClassName("foo"),
                Attribute("foo", "bar")
            )
        )
    }

    @Test
    fun singleLevelOr() {
        assertEquals(
            CssSelector(".foo.bar, [foo=bar].bar", 4),
            compiler.compile(
                Predicate.Or(
                    ClassName("foo"),
                    Attribute("foo", "bar")
                ),
                ClassName("bar"),
            )
        )
    }

    @Test
    fun multiLevelOr() {
        assertEquals(
            CssSelector("[foo=bar].bar, .foo.bar, .fof.bar", 6),
            compiler.compile(
                Predicate.Or(
                    Predicate.Or(
                        ClassName("foo"),
                        ClassName("fof"),
                    ),
                    Attribute("foo", "bar")
                ),
                ClassName("bar"),
            )
        )
    }
}