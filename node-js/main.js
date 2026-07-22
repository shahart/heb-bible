import http from 'node:http';
import {
  googleCallbackHandler,
  googleLoginHandler,
  logoutHandler,
  meHandler
} from './auth.js';
import { Pasuk } from './pasuk.js';

function psukimHandler(req, res) {
  const url = new URL(req.url, `http://${req.headers.host}`);
  const name = url.searchParams.get('name');
  if (!name) {
    res.writeHead(400, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: "missing 'name' query parameter" }));
    return;
  }
  console.log(`/psukim on ` + name);
  const containsName = url.searchParams.get('containsName') === 'true';

  const count = Pasuk(name, containsName);

  res.writeHead(200, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ count }));
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, `http://${req.headers.host}`);

  if (req.method === 'GET' && url.pathname === '/psukim') {
    psukimHandler(req, res);
  } else if (req.method === 'GET' && url.pathname === '/auth/google/callback') {
    googleCallbackHandler(req, res).catch((err) => {
      console.error(err);
      res.writeHead(502, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: 'google oauth callback failed' }));
    });
  } else if (req.method === 'GET' && url.pathname === '/auth/google') {
    googleLoginHandler(req, res);
  } else if (req.method === 'GET' && url.pathname === '/auth/me') {
    meHandler(req, res);
  } else if (req.method === 'POST' && url.pathname === '/auth/logout') {
    logoutHandler(req, res);
  } else {
    res.writeHead(404);
    res.end();
  }
});

const PORT = parseInt(process.env.PORT, 10) || 8080;
server.listen(PORT, () => {
  console.log(`listening on :${PORT}`);
});
