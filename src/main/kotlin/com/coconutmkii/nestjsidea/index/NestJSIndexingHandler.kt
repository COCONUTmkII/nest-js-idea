package com.coconutmkii.nestjsidea.index

import com.intellij.lang.javascript.JSStubElementTypes
import com.intellij.psi.tree.TokenSet

val TS_CLASS_TOKENS = TokenSet.create(
    JSStubElementTypes.TYPESCRIPT_CLASS,
    JSStubElementTypes.TYPESCRIPT_CLASS_EXPRESSION
)
