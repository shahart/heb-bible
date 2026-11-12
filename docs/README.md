# Hebrew Bible site

The browser application is written in TypeScript under `src/`. The TypeScript compiler emits native JavaScript modules into the site folders (`script.js`, `Repo.js`, and so on), so the static site can be served without a bundler. The current configuration transpiles the existing code without type checking (`noCheck`) while its DOM and repository interfaces are migrated.

Install TypeScript once if it is not already available:

```sh
npm install --global typescript
```

Compile the application and service worker from this directory:

```sh
tsc --project tsconfig.json
```

The same compile also emits the browser test module at `test/test.js`. Then serve this directory over HTTP, for example:

```sh
python -m http.server
```

Open `http://localhost:8000/`. To run the browser test harness, open `http://localhost:8000/test/test.html`.
