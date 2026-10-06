package com.coconutmkii.nestjsidea.inspections

import com.coconutmkii.nestjsidea.framework.model.NestJSBeanType

class NestJSModuleIsNotProvidedInspection : NestJSBeanNotReferencedInspection(
    NestJSBeanType.MODULE,
    "nestjs.inspection.module.is.not.used",
)
