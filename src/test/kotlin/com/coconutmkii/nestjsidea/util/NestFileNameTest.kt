package com.coconutmkii.nestjsidea.util

import com.coconutmkii.nestjsidea.framework.file.NestJSFileTemplate
import org.junit.Assert.assertEquals
import org.junit.Test

class NestFileNameTest {

    @Test
    fun `name with class suffix`() {
        assertEquals("user.controller.ts", nestFileName("UserController", NestJSFileTemplate.CONTROLLER))
        assertEquals("auth.service.ts", nestFileName("AuthService", NestJSFileTemplate.SERVICE))
        assertEquals("app.module.ts", nestFileName("AppModule", NestJSFileTemplate.MODULE))
        assertEquals("user.resolver.ts", nestFileName("UserResolver", NestJSFileTemplate.RESOLVER))
    }

    @Test
    fun `name without class suffix`() {
        assertEquals("user.controller.ts", nestFileName("User", NestJSFileTemplate.CONTROLLER))
        assertEquals("auth.service.ts", nestFileName("Auth", NestJSFileTemplate.SERVICE))
    }

    @Test
    fun `multi word name`() {
        assertEquals("user-profile.controller.ts", nestFileName("UserProfileController", NestJSFileTemplate.CONTROLLER))
    }

    @Test
    fun `consecutive uppercase is kept as one word, like nest generate does`() {
        assertEquals("httpclient.service.ts", nestFileName("HTTPClientService", NestJSFileTemplate.SERVICE))
        assertEquals("apikey.guard.ts", nestFileName("APIKeyGuard", NestJSFileTemplate.GUARD))
    }

    @Test
    fun `digit before uppercase starts a new word`() {
        assertEquals("user2-profile.service.ts", nestFileName("User2ProfileService", NestJSFileTemplate.SERVICE))
    }

    @Test
    fun `unknown template falls back to plain name`() {
        assertEquals("Whatever.ts", nestFileName("Whatever", null))
    }

    @Test
    fun `input is cleaned before naming`() {
        assertEquals("user.controller.ts", nestFileName("  User Controller  ", NestJSFileTemplate.CONTROLLER))
        assertEquals("user.controller.ts", nestFileName("UserController.ts", NestJSFileTemplate.CONTROLLER))
        assertEquals("Whatever.ts", nestFileName(" Whatever.ts ", null))
    }
}
