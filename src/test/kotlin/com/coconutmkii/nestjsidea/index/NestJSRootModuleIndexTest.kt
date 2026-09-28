package com.coconutmkii.nestjsidea.index

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.indexing.FileBasedIndex

class NestJSRootModuleIndexTest : BasePlatformTestCase() {

    fun `test module bootstrapped by NestFactory create is indexed`() {
        val main = addMain(
            """
            import { NestFactory } from '@nestjs/core';
            import { AppModule } from './app.module';

            async function bootstrap() {
              const app = await NestFactory.create(AppModule);
              await app.listen(3000);
            }
            """
        )

        assertEquals(listOf(main), filesBootstrapping("AppModule"))
    }

    fun `test explicit type argument does not break detection`() {
        val main = addMain(
            """
            import { NestFactory } from '@nestjs/core';
            import { NestExpressApplication } from '@nestjs/platform-express';
            import { AppModule } from './app.module';

            async function bootstrap() {
              const app = await NestFactory.create<NestExpressApplication>(AppModule);
              await app.listen(3000);
            }
            """
        )

        assertEquals(listOf(main), filesBootstrapping("AppModule"))
    }

    fun `test only the bootstrapped module is indexed`() {
        addMain(
            """
            import { NestFactory } from '@nestjs/core';
            import { AppModule } from './app.module';
            import { UserModule } from './user.module';

            async function bootstrap() {
              const app = await NestFactory.create(AppModule);
              await app.listen(3000);
            }
            """
        )

        assertEmpty(filesBootstrapping("UserModule"))
    }

    fun `test create call on another qualifier is not indexed`() {
        // "NestFactory" is present in the text, so the cheap pre-filter passes and the qualifier check has to reject it.
        addMain(
            """
            import { AppModule } from './app.module';

            // not the NestFactory from @nestjs/core
            const app = TestingModuleFactory.create(AppModule);
            """
        )

        assertEmpty(filesBootstrapping("AppModule"))
    }

    fun `test file without NestFactory is skipped`() {
        addMain(
            """
            import { AppModule } from './app.module';

            const app = create(AppModule);
            """
        )

        assertEmpty(filesBootstrapping("AppModule"))
    }

    fun `test non reference argument is not indexed`() {
        addMain(
            """
            import { NestFactory } from '@nestjs/core';
            import { AppModule } from './app.module';

            const app = await NestFactory.create(AppModule.forRoot());
            """
        )

        assertEmpty(filesBootstrapping("AppModule"))
    }

    fun `test microservice root module is indexed`() {
        val main = addMain(
            """
            import { NestFactory } from '@nestjs/core';
            import { MicroserviceOptions } from '@nestjs/microservices';
            import { AppModule } from './app.module';

            async function bootstrap() {
              const app = await NestFactory.createMicroservice<MicroserviceOptions>(AppModule, {});
              await app.listen();
            }
            """
        )

        assertEquals(listOf(main), filesBootstrapping("AppModule"))
    }

    fun `test standalone application context root module is indexed`() {
        val main = addMain(
            """
            import { NestFactory } from '@nestjs/core';
            import { AppModule } from './app.module';

            async function bootstrap() {
              const app = await NestFactory.createApplicationContext(AppModule);
              await app.close();
            }
            """
        )

        assertEquals(listOf(main), filesBootstrapping("AppModule"))
    }

    private fun addMain(text: String): VirtualFile =
        myFixture.addFileToProject("main.ts", text.trimIndent()).virtualFile

    private fun filesBootstrapping(moduleName: String): List<VirtualFile> =
        FileBasedIndex.getInstance()
            .getContainingFiles(NestJSRootModuleIndex.KEY, moduleName, GlobalSearchScope.projectScope(project))
            .toList()
}
