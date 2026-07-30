# Firebase Authentication

The `/auth/signup` and `/auth/login` endpoints can use Firebase Authentication
instead of the JDBC `local_users` table. The public API does not change: after
Firebase validates the credentials, the application returns its existing JWT.

## Firebase setup

1. Create or select a Firebase project.
2. In **Authentication > Sign-in method**, enable **Email/Password**.
3. Copy the project's Web API key from **Project settings > General**.
4. Export the key and start the Firebase profile:

   ```shell
   export FIREBASE_API_KEY=your-web-api-key
   mvn spring-boot:run -Dspring-boot.run.profiles=firebase
   ```

Alternatively, set these properties without using the profile:

```properties
hebbible.auth.user-management.provider=firebase
hebbible.auth.firebase.api-key=${FIREBASE_API_KEY}
```

The Firebase provider uses the Identity Toolkit `accounts:signUp` and
`accounts:signInWithPassword` APIs. A Firebase service-account private key is
not required for these two operations. Keep the API key out of source control
and apply appropriate Google Cloud API restrictions.

JDBC remains the default provider. The login-attempt limiter is configured
separately with `hebbible.auth.rate-limit.storage`; Firebase can therefore be
combined with the existing SQLite, PostgreSQL, or Redis limiter configuration.
