import { Controller, Get, Query, BadRequestException } from '@nestjs/common';
import { PasukService } from './pasuk.service';

@Controller()
export class PsukimController {
  constructor(private readonly pasukService: PasukService) {}

  @Get('psukim')
  getPsukim(
    @Query('name') name: string,
    @Query('containsName') containsName?: string,
  ) {
    if (!name) {
      throw new BadRequestException({ error: "missing 'name' query parameter" });
    }
    const count = this.pasukService.count(name, containsName === 'true');
    return { count };
  }
}
