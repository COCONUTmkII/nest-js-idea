package com.coconutmkii.nestjsidea.marker

import com.coconutmkii.nestjsidea.NestJSBundle
import com.coconutmkii.nestjsidea.NestJSIcons.beanIcon
import com.coconutmkii.nestjsidea.framework.model.NestJSBeanType
import com.coconutmkii.nestjsidea.services.NestJSBeanService
import com.coconutmkii.nestjsidea.services.NestJSDecoratorService
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement

abstract class NestJSBeanLineMarker(private val bean: NestJSBeanType) : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val tsClass = element.parent as? TypeScriptClass ?: return
        if (tsClass.nameIdentifier != element) return

        val decoratorService = element.project.service<NestJSDecoratorService>()
        if (decoratorService.findNestDecorator(tsClass, bean.normalizedName) == null) {
            return
        }

        val modules = element.project.service<NestJSBeanService>().findModulesUsingBean(tsClass, bean)
        if (modules.isEmpty()) return

        val marker = NavigationGutterIconBuilder.create(beanIcon)
            .setTargets(modules)
            .setTooltipText(NestJSBundle.message("nestjs.marker.controller.navigate.to.modules"))
            .createLineMarkerInfo(element)
        result.add(marker)
    }
}
