import { Module } from '@nestjs/common';
import { MyController } from './my.controller';

@Module({
    controllers: [MyController],
})
export class AppModule {}
