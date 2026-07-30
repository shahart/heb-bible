# Heb Bible React frontend

This Vite + React client uses the Spring Boot API in `../sb3`.

## Run locally

Start the backend:

```bash
mvn -f sb3/pom.xml spring-boot:run
```

Then start the frontend:

```bash
cd react-sb
npm install
npm run dev
```

Vite proxies authentication, user, and psukim requests to
`http://localhost:8080`. Copy `.env.example` to `.env.local` to point it at a
different backend.

The Google button uses Spring Security's
`/oauth2/authorization/google` route. Email registration and sign-in use
`/auth/signup` and `/auth/login`; the returned JWT is sent with subsequent
`POST /psukim` requests.
