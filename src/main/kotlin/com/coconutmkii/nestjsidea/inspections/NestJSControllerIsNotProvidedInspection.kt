package com.coconutmkii.nestjsidea.inspections

import com.coconutmkii.nestjsidea.framework.model.NestJSBeanType

class NestJSControllerIsNotProvidedInspection : NestJSBeanNotReferencedInspection(
    NestJSBeanType.CONTROLLER,
    "nestjs.inspection.controller.is.not.provided",
)
