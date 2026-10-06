package com.coconutmkii.nestjsidea.marker

import com.coconutmkii.nestjsidea.framework.model.NestJSBeanType

class NestJSControllerLineMarker : NestJSBeanLineMarker(NestJSBeanType.CONTROLLER)

class NestJSInjectableLineMarker : NestJSBeanLineMarker(NestJSBeanType.INJECTABLE)

class NestJSModuleLineMarker : NestJSBeanLineMarker(NestJSBeanType.MODULE)
