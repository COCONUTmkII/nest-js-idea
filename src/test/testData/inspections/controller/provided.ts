import { Controller, Module } from '@nestjs/common';

@Controller()
export class MyController {}

@Module({
    controllers: [MyController],
})
export class AppModule {}
