/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.descriptors.symbols.descriptorBased

import org.jetbrains.kotlin.analysis.api.descriptors.Fe10AnalysisContext
import org.jetbrains.kotlin.analysis.api.descriptors.symbols.descriptorBased.base.Fe10DescMemberKtSymbol
import org.jetbrains.kotlin.analysis.api.descriptors.symbols.descriptorBased.base.callableIdIfNotLocal
import org.jetbrains.kotlin.analysis.api.descriptors.symbols.descriptorBased.base.toKtTypeAndAnnotations
import org.jetbrains.kotlin.analysis.api.descriptors.symbols.pointers.Fe10NeverRestoringKtSymbolPointer
import org.jetbrains.kotlin.analysis.api.symbols.KtJavaFieldSymbol
import org.jetbrains.kotlin.analysis.api.symbols.markers.KtTypeAndAnnotations
import org.jetbrains.kotlin.analysis.api.symbols.pointers.KtPsiBasedSymbolPointer
import org.jetbrains.kotlin.analysis.api.symbols.pointers.KtSymbolPointer
import org.jetbrains.kotlin.analysis.api.withValidityAssertion
import org.jetbrains.kotlin.load.java.descriptors.JavaPropertyDescriptor
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorUtils

internal class Fe10DescKtJavaFieldSymbol(
    override val descriptor: JavaPropertyDescriptor,
    override val analysisContext: Fe10AnalysisContext
) : KtJavaFieldSymbol(), Fe10DescMemberKtSymbol<JavaPropertyDescriptor> {
    override val name: Name
        get() = withValidityAssertion { descriptor.name }

    override val isStatic: Boolean
        get() = withValidityAssertion { DescriptorUtils.isStaticDeclaration(descriptor) }

    override val isVal: Boolean
        get() = withValidityAssertion { !descriptor.isVar }

    override val callableIdIfNonLocal: CallableId?
        get() = withValidityAssertion { descriptor.callableIdIfNotLocal }

    override val annotatedType: KtTypeAndAnnotations
        get() = withValidityAssertion { descriptor.returnType.toKtTypeAndAnnotations(analysisContext) }

    override fun createPointer(): KtSymbolPointer<KtJavaFieldSymbol> = withValidityAssertion {
        return KtPsiBasedSymbolPointer.createForSymbolFromSource(this) ?: Fe10NeverRestoringKtSymbolPointer()
    }
}