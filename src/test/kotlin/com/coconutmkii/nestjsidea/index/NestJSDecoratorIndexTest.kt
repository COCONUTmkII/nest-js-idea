package com.coconutmkii.nestjsidea.index

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.indexing.FileBasedIndex

class NestJSDecoratorIndexTest : BasePlatformTestCase() {

    fun `test module decorator is indexed`() {
        val module = addFile(
            "app.module.ts",
            """
            import { Module } from '@nestjs/common';

            @Module({})
            export class AppModule {}
            """
        )

        assertEquals(listOf(module), filesDeclaring("Module"))
    }

    fun `test every decorator of the file is indexed`() {
        val file = addFile(
            "user.controller.ts",
            """
            import { Controller, Get, Injectable } from '@nestjs/common';

            @Injectable()
            export class UserService {}

            @Controller('users')
            export class UserController {
              @Get()
              findAll() {}
            }
            """
        )

        assertEquals(listOf(file), filesDeclaring("Controller"))
        assertEquals(listOf(file), filesDeclaring("Injectable"))
        assertEquals(listOf(file), filesDeclaring("Get"))
    }

    // The index only records decorator names. Whether the decorator really comes from @nestjs/common is decided later
    // by NestJSDecoratorService, so a same-named decorator from another library is expected to be indexed as well.
    fun `test decorator from another library is indexed too`() {
        val file = addFile(
            "widget.ts",
            """
            import { Module } from '@angular/core';
            import { Injectable } from '@nestjs/common';

            @Module({})
            export class WidgetModule {}
            """
        )

        assertEquals(listOf(file), filesDeclaring("Module"))
    }

    // The cheap text pre-filter is what keeps the indexer from parsing every .d.ts in node_modules.
    fun `test file without a nestjs import is skipped`() {
        addFile(
            "widget.ts",
            """
            import { Module } from '@angular/core';

            @Module({})
            export class WidgetModule {}
            """
        )

        assertEmpty(filesDeclaring("Module"))
    }

    fun `test file without decorators produces no keys`() {
        addFile(
            "app.service.ts",
            """
            import { Injectable } from '@nestjs/common';

            export class AppService {
              greet() {}
            }
            """
        )

        assertEmpty(filesDeclaring("Injectable"))
    }

    private fun addFile(name: String, text: String): VirtualFile =
        myFixture.addFileToProject(name, text.trimIndent()).virtualFile

    private fun filesDeclaring(decoratorName: String): List<VirtualFile> =
        FileBasedIndex.getInstance()
            .getContainingFiles(NestJSDecoratorIndex.KEY, decoratorName, GlobalSearchScope.projectScope(project))
            .toList()
}
