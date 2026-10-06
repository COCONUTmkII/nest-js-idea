package com.coconutmkii.nestjsidea.inspections

import com.coconutmkii.nestjsidea.framework.model.NestJSBeanType

class NestJSInjectableIsNotProvidedInspection : NestJSBeanNotReferencedInspection(
    NestJSBeanType.INJECTABLE,
    "nestjs.inspection.injectable.is.not.used",
)
