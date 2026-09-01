import { DynamicModule, Module } from '@nestjs/common';
import { DatabaseModule } from './database.module';

@Module({})
export class AppModule {
    static forRoot(): DynamicModule {
        return {
            module: AppModule,
            imports: [DatabaseModule],
        };
    }
}
