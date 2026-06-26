import { gunzipSync } from 'node:zlib';

const encoder = new TextEncoder();

async function loadBible() {
  const url = 'https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.gz';
  const resp = await fetch(url);
  if (!resp.ok) throw new Error(`HTTP ${resp.status} fetching bible`);
  const buf = Buffer.from(await resp.arrayBuffer());
  const text = gunzipSync(buf).toString('utf-8');
  const lines = text.split('\n');
  const verses = [];
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

const verses = await loadBible();

export function Pasuk(name, containsName) {
  if (!name) return 0;

  const nameBytes = encoder.encode(name);

  let count = 0;
  for (const txt of verses) {
    if (containsName && txt.includes(name)) {
      console.log(txt);
      count++;
      continue;
    }
    if (txt.length > 1 && name.length > 1) {
      const txtBytes = encoder.encode(txt);
      if (txtBytes[1] === nameBytes[1] && txtBytes[txtBytes.length - 1] === nameBytes[nameBytes.length - 1]) {
        console.log(txt);
        count++;
      }
    }
  }
  return count;
}
