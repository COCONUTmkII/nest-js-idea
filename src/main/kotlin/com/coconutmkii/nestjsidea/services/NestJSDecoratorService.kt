package com.coconutmkii.nestjsidea.services

import com.coconutmkii.nestjsidea.index.TS_CLASS_TOKENS
import com.coconutmkii.nestjsidea.util.NESTJS_COMMON_PACKAGE
import com.intellij.lang.ecmascript6.psi.ES6ImportDeclaration
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.StubSafe
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClassExpression
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.lang.javascript.psi.util.JSStubBasedPsiTreeUtil
import com.intellij.lang.javascript.psi.util.JSUtils
import com.intellij.openapi.components.Service
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.util.text.StringUtil.contains
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.util.PsiTreeUtil.getContextOfType
import com.intellij.psi.util.PsiTreeUtil.getStubChildrenOfTypeAsList
import com.intellij.util.asSafely

@Service
object NestJSDecoratorService {
    const val CONTROLLER_DECORATOR = "Controller"
    const val MODULE_DECORATOR = "Module"

    @JvmStatic
    @StubSafe
    fun findNestDecorator(attributeListOwner: JSAttributeListOwner, name: String): ES6Decorator? {
        val list = attributeListOwner.attributeList
        if (list == null || name.isEmpty()) return null

        getStubChildrenOfTypeAsList(list, ES6Decorator::class.java).firstOrNull { isNestSupportedDecorator(it, name) }
            ?.let { return it }

        return (attributeListOwner as? TypeScriptClassExpression)?.context?.let { it as? JSAttributeListOwner }
            ?.let { findNestDecorator(it, name) }
    }

    @JvmStatic
    fun isNestSupportedDecorator(decorator: ES6Decorator, name: String): Boolean {
        val decoratorName = decorator.decoratorName ?: return false
        return (contains(decoratorName, name) && (getClassForDecoratorElement(decorator)?.attributeList?.hasModifier(
            JSAttributeList.ModifierType.ABSTRACT
        ) != true)) && hasImportFromNestJSCommonPackage(decoratorName, decorator.containingFile)
    }

    @StubSafe
    @JvmStatic
    fun getObjectLiteralInitializer(
        decorator: ES6Decorator?
    ): JSObjectLiteralExpression? {
        val children = getStubChildrenOfTypeAsList(decorator, PsiElement::class.java)
        for (child in children) {
            when (child) {
                is JSObjectLiteralExpression -> return child
                is JSCallExpression -> {
                    val stub = (child as? StubBasedPsiElement<*>)?.stub
                    return if (stub != null) {
                        stub.childrenStubs.firstNotNullOfOrNull { it.psi as? JSObjectLiteralExpression }
                    } else {
                        child.arguments.asSequence().map(JSUtils::unparenthesize)
                            .filterIsInstance<JSObjectLiteralExpression>().firstOrNull()
                    }
                }
            }
        }
        return null
    }

    @JvmStatic
    fun getClassForDecoratorElement(element: PsiElement?): TypeScriptClass? {
        val decorator = element.asSafely<ES6Decorator>() ?: getContextOfType(element, ES6Decorator::class.java, false)
        ?: return null
        val owner = getContextOfType(decorator, JSAttributeListOwner::class.java) ?: return null
        return owner.asSafely<TypeScriptClass>() ?: JSStubBasedPsiTreeUtil.getChildrenByType(owner, TS_CLASS_TOKENS)
            .firstOrNull() as? TypeScriptClass
    }

    private fun hasImportFromNestJSCommonPackage(name: String, file: PsiFile): Boolean =
        JSStubBasedPsiTreeUtil.resolveLocally(name, file)?.let {
                getContextOfType(
                    it,
                    ES6ImportDeclaration::class.java
                )
            }?.fromClause?.referenceText?.let { StringUtil.unquoteString(it) }
            ?.let { from -> NESTJS_COMMON_PACKAGE == from } ?: false


}
