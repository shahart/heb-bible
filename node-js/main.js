import http from 'node:http';
import { Pasuk } from './pasuk.js';

function psukimHandler(req, res) {
  const url = new URL(req.url, `http://${req.headers.host}`);
  const name = url.searchParams.get('name');
  if (!name) {
    res.writeHead(400, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: "missing 'name' query parameter" }));
    return;
  }
  const containsName = url.searchParams.get('containsName') === 'true';

  const count = Pasuk(name, containsName);

  res.writeHead(200, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ count }));
}

const server = http.createServer((req, res) => {
  if (req.method === 'GET' && req.url.startsWith('/psukim')) {
    psukimHandler(req, res);
  } else {
    res.writeHead(404);
    res.end();
  }
});

const PORT = parseInt(process.env.PORT, 10) || 8080;
server.listen(PORT, () => {
  console.log(`listening on :${PORT}`);
});
