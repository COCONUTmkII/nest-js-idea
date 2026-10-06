package com.coconutmkii.nestjsidea.framework.model

enum class NestJSBeanType(val normalizedName: String) {
    MODULE("Module"),
    CONTROLLER("Controller"),
    INJECTABLE("Injectable"),
    GUARD("Guard"),
    PIPE("Pipe"),
    RESOLVER("Resolver"),
}
