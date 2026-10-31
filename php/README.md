# Heb Bible PHP API

PHP port of the `typescript/` API. It exposes `GET /psukim?name=...`; adding
`containsName=true` enables substring matching. Responses are JSON, for example
`{"count":25}`. A missing `name` returns HTTP 400.

The Bible text is bundled in `data/bible.txt.gz`, so requests do not need network
access. Set `BIBLE_GZIP_PATH` to use a different compressed Bible file. PHP's
zlib extension decompresses it.

## Run

```sh
php -S 127.0.0.1:8080 router.php
```

Then request `http://127.0.0.1:8080/psukim?name=%D7%A9%D7%97%D7%A8`.

Requires PHP 8.1+ with the mbstring and zlib extensions.

## Tests

Run the dependency-free test suite with `php tests/run.php`.
