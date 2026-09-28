package com.coconutmkii.nestjsidea.index

import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex

/**
 * Resolves the TypeScript file type through [FileTypeManager] instead of
 * [com.intellij.lang.javascript.TypeScriptFileType.INSTANCE].
 *
 * That field is a Java `static` on 2024.x and disappears from bytecode when the class
 * becomes a Kotlin `object` in 2025.1+, which Plugin Verifier reports as NoSuchFieldError.
 */
internal fun typescriptInputFilter(): FileBasedIndex.InputFilter =
    DefaultFileTypeSpecificInputFilter(FileTypeManager.getInstance().getFileTypeByExtension("ts"))
