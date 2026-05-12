# Docs Folder Guidelines

## Scope
These instructions apply only to `docs/` and its subdirectories. Treat this folder as a standalone static web client, even though it shares data files and logic conventions with the larger repository.

## Structure
- `index.html` is the main browser entry point for the Hebrew Bible UI.
- `script.js`, `Read.js`, `Pasuk.js`, `Find.js`, `Gematria.js`, `Dilug.js`, `No2gim.js`, `Repo.js`, and `RepoInit.js` contain the client logic.
- `sw.js` is the service worker and caches a small set of static assets.
- `mystyle.css` contains shared styling.
- `test/test.html` is the browser test harness, and `test/test.js` plus `test/no2gim.test.js` contain Mocha/Chai tests.
- `bible.txt.gz` and `bible-niqqud.txt` are local data files; `RepoInit.js` can also fetch remote copies from GitHub.

## Run And Test
- There is no package-based build step in `docs/`; treat it as plain static HTML/CSS/JS.
- Serve `docs/` over HTTP for normal manual testing. A simple option is `python -m http.server` from `docs/`, then open `http://localhost:8000/index.html`.
- Open `test/test.html` through the same local server to run the browser tests.
- Service worker changes should be verified in a real browser with cache cleared or the service worker updated, because `sw.js` uses an explicit cache version.

## Editing Conventions
- Prefer small, direct edits that preserve the current plain-JS structure.
- Keep filenames and exported class names stable; the test harness imports modules by relative path.
- Avoid introducing a bundler, framework, or npm dependency unless explicitly requested.
- Be careful with network-dependent code paths in `index.html` and `RepoInit.js`; the current app relies on CDN scripts and may fetch Bible data from GitHub when local cache is empty.

## Validation
- For logic changes, run the browser tests in `test/test.html`.
- For UI changes, verify `index.html` manually in desktop and mobile-width layouts.
- If you change caching behavior, validate a fresh load, a repeat load, and offline or failed-network fallback behavior.

## Do Not Touch
- Do not modify unrelated modules outside `docs/` when the task is scoped here.
- Do not delete or overwrite user-created untracked files in this folder; the worktree may be intentionally dirty.
