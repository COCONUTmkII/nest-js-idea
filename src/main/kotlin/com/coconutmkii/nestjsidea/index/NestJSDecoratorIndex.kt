package com.coconutmkii.nestjsidea.index

import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor

class NestJSDecoratorIndex : ScalarIndexExtension<String>() {
    override fun getName(): ID<String, Void> = KEY

    override fun getInputFilter(): FileBasedIndex.InputFilter =
        DefaultFileTypeSpecificInputFilter(TypeScriptFileType.INSTANCE)

    override fun dependsOnFileContent(): Boolean = true

    override fun getIndexer(): DataIndexer<String, Void, FileContent> = DataIndexer { content ->
        val result = mutableMapOf<String, Void?>()
        val psi = content.psiFile

        if (!content.contentAsText.contains('@')) return@DataIndexer result

        PsiTreeUtil.findChildrenOfType(psi, ES6Decorator::class.java).forEach { decorator ->
            decorator.decoratorName?.let { result[it] = null }
        }
        result
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getVersion(): Int = 1

    companion object {
        val KEY: ID<String, Void> = ID.create("nestjs.decorator.index")
    }
}
