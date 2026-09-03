package com.coconutmkii.nestjsidea.index

import com.intellij.util.indexing.ScalarIndexExtension

import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor

class NestJSRootModuleIndex : ScalarIndexExtension<String>() {
    override fun getName(): ID<String, Void> = KEY

    override fun getInputFilter(): FileBasedIndex.InputFilter =
        DefaultFileTypeSpecificInputFilter(TypeScriptFileType.INSTANCE)

    override fun dependsOnFileContent(): Boolean = true

    override fun getIndexer(): DataIndexer<String, Void, FileContent> = DataIndexer { content ->
        if (!content.contentAsText.contains("NestFactory")) return@DataIndexer emptyMap()

        val result = mutableMapOf<String, Void?>()
        PsiTreeUtil.findChildrenOfType(content.psiFile, JSCallExpression::class.java).forEach { call ->
            val method = call.methodExpression as? JSReferenceExpression ?: return@forEach
            if (method.referenceName != "create" || method.qualifier?.text != "NestFactory") return@forEach
            (call.arguments.firstOrNull() as? JSReferenceExpression)?.referenceName?.let { result[it] = null }
        }
        result
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getVersion(): Int = 1

    companion object {
        val KEY: ID<String, Void> = ID.create("nestjs.root.module.index")
    }
}
