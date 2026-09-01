package com.coconutmkii.nestjsidea.framework.file

import com.coconutmkii.nestjsidea.NestJSIcons.controllerIcon
import com.coconutmkii.nestjsidea.NestJSIcons.serviceIcon
import com.coconutmkii.nestjsidea.NestJSIcons.moduleIcon
import com.coconutmkii.nestjsidea.NestJSIcons.pipeIcon
import com.coconutmkii.nestjsidea.NestJSIcons.guardIcon
import com.coconutmkii.nestjsidea.NestJSIcons.resolverIcon
import javax.swing.Icon

enum class NestJSFileTemplate(
    val templateName: String,
    val fileSuffix: String,
    val classSuffix: String,
    val icon: Icon,
    val titleKey: String,
) {
    CONTROLLER("NestJS Controller", "controller", "Controller", controllerIcon, "nestjs.dialog.title.new.nest.file.controller"),
    SERVICE("NestJS Service", "service", "Service", serviceIcon, "nestjs.dialog.title.new.nest.file.service"),
    MODULE("NestJS Module", "module", "Module", moduleIcon, "nestjs.dialog.title.new.nest.file.module"),
    PIPE("NestJS Pipe", "pipe", "Pipe",  pipeIcon, "nestjs.dialog.title.new.nest.file.pipe"),
    GUARD("NestJS Guard", "guard", "Guard", guardIcon, "nestjs.dialog.title.new.nest.file.guard"),
    RESOLVER("NestJS Resolver", "resolver", "Resolver", resolverIcon, "nestjs.dialog.title.new.nest.file.resolver"),
    ;

    companion object {
        fun byTemplateName(name: String?) = entries.firstOrNull { it.templateName == name }
    }
}
