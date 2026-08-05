# C# API

This ASP.NET Core implementation exposes only the Python `psukim_by_name`
endpoint. It reads the repository's shared `bible.txt.gz` file at startup.

Run it from this directory with:

```sh
dotnet run
```

Then request a name:

```sh
curl http://localhost:5000/psukim/שחר
```

The response is the JSON number `25`, matching the Python implementation.

Run the focused test with:

```sh
dotnet test tests/HebBible.Api.Tests.csproj
```

## Docker

Run these commands from the repository root so Docker can include the shared
`bible.txt.gz` data file:

```sh
docker build -f c-sharp/Dockerfile -t heb-bible-csharp .
docker run --rm -d --name heb-bible-csharp -p 8080:8080 heb-bible-csharp
curl 'http://localhost:8080/psukim/שחר'
docker stop heb-bible-csharp
```

The curl response should be `25`.
