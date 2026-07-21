import { Injectable } from '@nestjs/common';
import { loadBible } from './bible-loader';

@Injectable()
export class PasukService {
  private verses: string[] = [];
  private encoder = new TextEncoder();

  async onModuleInit() {
    console.debug('Pasuk Service loaded');
    this.verses = await loadBible();
  }

  count(name: string, containsName: boolean): number {
    if (!name) return 0;

    const nameBytes = this.encoder.encode(name);

    let count = 0;
    for (const txt of this.verses) {
      if (containsName && txt.includes(name)) {
        console.log(txt);
        count++;
        continue;
      }
      if (txt.length > 1 && name.length > 1) {
        const txtBytes = this.encoder.encode(txt);
        if (txtBytes[1] === nameBytes[1] && txtBytes[txtBytes.length - 1] === nameBytes[nameBytes.length - 1]) {
          console.log(txt);
          count++;
        }
      }
    }
    return count;
  }
}
