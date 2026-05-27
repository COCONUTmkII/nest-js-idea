package com.coconutmkii.nestjsidea.framework.model

enum class NestJSBeanType(val normilizedName: String) {
    MODULE("Module"),
    CONTROLLER("Controller"),
    SERVICE("Service"),
    GUARD("Guard"),
    PIPE("Pipe"),
    RESOLVER("Resolver"),
}