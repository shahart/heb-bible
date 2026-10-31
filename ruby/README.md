# Heb Bible Ruby API

Ruby web API for counting verses using the matching rules in `pasuk_service.rb`.
`GET /psukim` returns the total verse count. The app also supports
`GET /psukim?name=...` and `GET /psukim/:name` for name searches. Add
`containsName=true` to enable substring matching. The response is JSON, such as
`{"count":25}`. A missing `name` query parameter returns HTTP 400.
The dependencies support Ruby 2.6 and newer.

The app reads `../bible.txt.gz` from the repository root. Set `BIBLE_GZIP_PATH`
to use another gzip file.

## Run

```sh
cd ruby
bundle install --path vendor/bundle
bundle exec ruby app.rb
```

Then request `http://localhost:9010/psukim?name=%D7%A9%D7%97%D7%A8`.

## Tests

```sh
ruby test/pasuk_service_test.rb
```
