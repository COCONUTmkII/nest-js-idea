import { DynamicModule, Module } from '@nestjs/common';

@Module({})
export class ConfigModule {
    static forRoot(): DynamicModule {
        return {
            module: ConfigModule,
        };
    }
}
