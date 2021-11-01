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
import org.jetbrains.kotlin.analysis.api.symbols.KtAnonymousFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KtValueParameterSymbol
import org.jetbrains.kotlin.analysis.api.symbols.markers.KtTypeAndAnnotations
import org.jetbrains.kotlin.analysis.api.symbols.pointers.KtPsiBasedSymbolPointer
import org.jetbrains.kotlin.analysis.api.symbols.pointers.KtSymbolPointer
import org.jetbrains.kotlin.analysis.api.withValidityAssertion
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isExtensionDeclaration
import org.jetbrains.kotlin.resolve.BindingContext

internal class Fe10PsiKtAnonymousFunctionSymbol(
    override val psi: KtNamedFunction,
    override val analysisContext: Fe10AnalysisContext
) : KtAnonymousFunctionSymbol(), Fe10PsiKtSymbol<KtNamedFunction, FunctionDescriptor> {
    override val descriptor: FunctionDescriptor? by cached {
        val bindingContext = analysisContext.analyze(psi, AnalysisMode.PARTIAL)
        bindingContext[BindingContext.FUNCTION, psi]
    }

    override val valueParameters: List<KtValueParameterSymbol>
        get() = withValidityAssertion { psi.valueParameters.map { Fe10PsiKtValueParameterSymbol(it, analysisContext) } }

    override val hasStableParameterNames: Boolean
        get() = withValidityAssertion { true }

    override val annotatedType: KtTypeAndAnnotations
        get() = withValidityAssertion {
            descriptor?.returnType?.toKtTypeAndAnnotations(analysisContext) ?: createErrorTypeAndAnnotations()
        }

    override val receiverType: KtTypeAndAnnotations?
        get() = withValidityAssertion {
            if (psi.isExtensionDeclaration()) {
                return null
            }

            val descriptor = this.descriptor ?: return createErrorTypeAndAnnotations()
            val extensionReceiverParameter = descriptor.extensionReceiverParameter ?: return null
            return extensionReceiverParameter.type.toKtTypeAndAnnotations(analysisContext)
        }

    override val isExtension: Boolean
        get() = withValidityAssertion { psi.isExtensionDeclaration() }

    override fun createPointer(): KtSymbolPointer<KtAnonymousFunctionSymbol> = withValidityAssertion {
        return KtPsiBasedSymbolPointer.createForSymbolFromSource(this) ?: Fe10NeverRestoringKtSymbolPointer()
    }
}