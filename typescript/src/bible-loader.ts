import { gunzipSync } from 'node:zlib';

let verses: string[] | null = null;

export async function loadBible(): Promise<string[]> {
  if (verses) return verses;

  const url = 'https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.gz';
  const resp = await fetch(url);
  if (!resp.ok) throw new Error(`HTTP ${resp.status} fetching bible`);
  const buf = Buffer.from(await resp.arrayBuffer());
  const text = gunzipSync(buf).toString('utf-8');
  const lines = text.split('\n');
  verses = [];
  for (const line of lines) {
    const trimmed = line.trim();
    if (!trimmed) continue;
    const comma = trimmed.indexOf(',');
    if (comma !== -1) {
      verses.push(trimmed.slice(comma + 1));
    }
  }
  console.log(verses.length);
  return verses;
}
