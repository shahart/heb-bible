FROM mcr.microsoft.com/dotnet/sdk:8.0-alpine AS build
WORKDIR /src

COPY c-sharp/HebBible.Api.csproj c-sharp/
RUN dotnet restore c-sharp/HebBible.Api.csproj

COPY c-sharp/ c-sharp/
COPY bible.txt.gz .
RUN dotnet publish c-sharp/HebBible.Api.csproj \
    --configuration Release \
    --output /app/publish \
    --no-restore

FROM mcr.microsoft.com/dotnet/aspnet:8.0-alpine AS final
WORKDIR /app
ENV ASPNETCORE_HTTP_PORTS=8080
EXPOSE 8080

COPY --from=build /app/publish .

ENTRYPOINT ["dotnet", "HebBible.Api.dll"]
