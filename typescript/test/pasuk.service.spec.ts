import { Test, TestingModule } from '@nestjs/testing';
import { PasukService } from '../src/pasuk.service';

describe('PasukService', () => {
  let service: PasukService;

  beforeAll(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [PasukService],
    }).compile();
    service = module.get<PasukService>(PasukService);
    await service.onModuleInit();
  }, 30000);

  it('empty name returns 0', () => {
    expect(service.count('', false)).toBe(0);
  });

  it('returns count for Hebrew name without containsName', () => {
    const count = service.count('שחר', false);
    expect(count).toBe(25);
  });

  it('returns count for Hebrew name with containsName', () => {
    const count = service.count('שחר', true);
    expect(count).toBe(75);
  });
});
