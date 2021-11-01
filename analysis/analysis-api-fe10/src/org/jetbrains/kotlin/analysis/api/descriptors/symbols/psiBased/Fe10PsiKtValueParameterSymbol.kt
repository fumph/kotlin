/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.descriptors.symbols.psiBased

import org.jetbrains.kotlin.analysis.api.descriptors.Fe10AnalysisContext
import org.jetbrains.kotlin.analysis.api.descriptors.Fe10AnalysisFacade.AnalysisMode
import org.jetbrains.kotlin.analysis.api.descriptors.symbols.descriptorBased.base.toKtTypeAndAnnotations
import org.jetbrains.kotlin.analysis.api.descriptors.symbols.pointers.Fe10NeverRestoringKtSymbolPointer
import org.jetbrains.kotlin.analysis.api.descriptors.symbols.psiBased.base.Fe10PsiKtSymbol
import org.jetbrains.kotlin.analysis.api.descriptors.symbols.psiBased.base.createErrorTypeAndAnnotations
import org.jetbrains.kotlin.analysis.api.descriptors.utils.cached
import org.jetbrains.kotlin.analysis.api.symbols.KtValueParameterSymbol
import org.jetbrains.kotlin.analysis.api.symbols.markers.KtTypeAndAnnotations
import org.jetbrains.kotlin.analysis.api.symbols.pointers.KtPsiBasedSymbolPointer
import org.jetbrains.kotlin.analysis.api.symbols.pointers.KtSymbolPointer
import org.jetbrains.kotlin.analysis.api.withValidityAssertion
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.BindingContext

internal class Fe10PsiKtValueParameterSymbol(
    override val psi: KtParameter,
    override val analysisContext: Fe10AnalysisContext
) : KtValueParameterSymbol(), Fe10PsiKtSymbol<KtParameter, VariableDescriptor> {
    override val descriptor: VariableDescriptor? by cached {
        val bindingContext = analysisContext.analyze(psi, AnalysisMode.PARTIAL)
        bindingContext[BindingContext.VALUE_PARAMETER, psi]
    }

    override val hasDefaultValue: Boolean
        get() = withValidityAssertion { psi.hasDefaultValue() }

    override val isVararg: Boolean
        get() = withValidityAssertion { psi.isVarArg }

    override val annotatedType: KtTypeAndAnnotations
        get() = withValidityAssertion {
            val type = (descriptor as? ValueParameterDescriptor)?.varargElementType ?: descriptor?.type
            return type?.toKtTypeAndAnnotations(analysisContext) ?: createErrorTypeAndAnnotations()
        }

    override val name: Name
        get() = withValidityAssertion { psi.nameAsSafeName }

    override fun createPointer(): KtSymbolPointer<KtValueParameterSymbol> = withValidityAssertion {
        return KtPsiBasedSymbolPointer.createForSymbolFromSource(this) ?: Fe10NeverRestoringKtSymbolPointer()
    }
}