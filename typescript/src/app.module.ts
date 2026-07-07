import { Module } from '@nestjs/common';
import { PsukimController } from './psukim.controller';
import { PasukService } from './pasuk.service';

@Module({
  controllers: [PsukimController],
  providers: [PasukService],
})
export class AppModule {}
