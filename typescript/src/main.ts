import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  const port = parseInt(process.env.PORT, 10) || 8080;
  await app.listen(port);
  console.log(`heb-bible, listening on :${port}`);
}
bootstrap();
