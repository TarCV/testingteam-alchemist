package com.github.tarcv.testing.alchemist.web

import com.github.tarcv.testing.alchemist.Combiner
import com.github.tarcv.testing.alchemist.Compiler
import com.github.tarcv.testing.alchemist.CompilerProvider
import com.github.tarcv.testing.alchemist.Converter
import com.github.tarcv.testing.alchemist.common.AnyOf
import com.github.tarcv.testing.alchemist.common.HasAscendantWith
import com.github.tarcv.testing.alchemist.common.HasParentWith
import com.github.tarcv.testing.alchemist.web.css.CssAndConverter
import com.github.tarcv.testing.alchemist.web.css.CssAttributeConverter
import com.github.tarcv.testing.alchemist.web.css.CssClassConverter
import com.github.tarcv.testing.alchemist.web.css.CssCombiner
import com.github.tarcv.testing.alchemist.web.css.CssHasAscendantWithConverter
import com.github.tarcv.testing.alchemist.web.css.CssHasParentWithConverter
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
            add { singleton { CssAndConverter(instance()) } }
            add { singleton { CssHasAscendantWithConverter(instance()) } }
            add { singleton { CssHasParentWithConverter(instance()) } }
            add { singleton { CssOrConverter(instance()) } }
        }
        bindSet<Combiner<*>> {
            add { instance(IdConverter) }
            add { singleton { CssCombiner() } }
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
                AnyOf(
                    ClassName("foo"),
                    Attribute("foo", "bar")
                ),
                ClassName("bar"),
            )
        )
    }

    @Test
    fun singleLevelParentOr() {
        assertEquals(
            CssSelector(".foo.fof>.bar, [foo=bar]>.bar", 4),
            compiler.compile(
                AnyOf(
                    HasParentWith(ClassName("foo"), ClassName("fof")),
                    HasParentWith(Attribute("foo", "bar"))
                ),
                ClassName("bar"),
            )
        )
    }

    @Test
    fun multiLevelParentOr() {
        assertEquals(
            CssSelector(".foo[zoo=asdf]>.bar, .fof[zoo=asdf]>.bar, [foo=bar] .bar", 6),
            compiler.compile(
                AnyOf(
                    HasParentWith(
                        AnyOf(
                            ClassName("foo"),
                            ClassName("fof")
                        ),
                        Attribute("zoo", "asdf")
                    ),
                    HasAscendantWith(Attribute("foo", "bar"))
                ),
                ClassName("bar"),
            )
        )
    }

    @Test
    fun multiLevelOr() {
        assertEquals(
            CssSelector(".foo.bar, .fof.bar, [foo=bar].bar", 6),
            compiler.compile(
                AnyOf(
                    AnyOf(
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