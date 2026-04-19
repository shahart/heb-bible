# Repository Guidelines

## Project Structure & Module Organization
`sb3/` is the primary Spring Boot application and the best place to start for backend changes. Java cloud adapters such as `aws-lambda/`, `azure-functions/`, `gcp-cloud-function/`, `spring-cloud-function/`, `oracle-cloud-fn/`, and `tencent/` mirror the same core behavior for different runtimes. `docs/` contains the static web client, `python/`, `go/`, `rust/`, `c++/`, and `pascal/` hold alternate implementations, and top-level `bible*.txt` / `bible.txt.gz` files are the shared source data. Tests live beside each module, for example `sb3/src/test/java/` and `python/tests/`.

## Build, Test, and Development Commands
Use module-local commands; there is no single root build.

- `mvn -f sb3/pom.xml spring-boot:run` runs the main API locally on port 8080.
- `mvn -f sb3/pom.xml test` runs unit and integration tests for the primary Java app.
- `mvn -f sb3/pom.xml clean package` builds the Spring Boot artifact.
- `cd python; python -m pip install -r requirements.txt` installs Python dependencies.
- `cd python; python app.py` starts the Flask sample API on port 9000.
- `cd python; pytest tests` runs Python tests.
- `cd go; go test ./...` and `cd rust; cargo test` cover the alternate language ports.

## Coding Style & Naming Conventions
Follow the style already used in each language rather than forcing a repo-wide format. Java code uses 4-space indentation, `PascalCase` for classes, `camelCase` for methods/fields, and package paths under `edu.hebbible`. Python follows PEP 8-style snake_case for functions and test names such as `test_psukim_by_name`. Keep generated artifacts out of commits; `target/`, `build/`, `out/`, and local virtualenvs are ignored.

## Testing Guidelines
Prefer focused tests near the module you changed. Java tests use JUnit via Maven Surefire/Failsafe in `src/test/java`; Python uses `pytest` in `python/tests/test_*.py`. For cross-cutting logic changes, update `sb3/` first, then verify any affected adapter module. No formal coverage gate is defined, but new behavior should ship with at least one automated test.

## Commit & Pull Request Guidelines
Recent commit subjects are short, imperative, and feature-focused, for example `Small Haftarah fix` or `Dependabot: pytest has vulnerable tmpdir handling`. Keep commits scoped to one logical change. PRs should describe the affected module(s), summarize behavior changes, list the commands you ran, and include screenshots only for `docs/` or UI-facing updates.

## Agent-Specific Notes
Do not edit generated files such as `dependency-reduced-pom.xml`. When the same fix spans several runtimes, implement and validate it in `sb3/` first, then port the minimal necessary changes to the other modules.
