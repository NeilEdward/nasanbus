# NasanBus Cognito Setup

## Overview

NasanBus uses AWS Cognito for authentication.

AWS Cognito is responsible for:

- User login
- User password management
- Email verification
- Token issuance

NasanBus PostgreSQL is responsible for:

- Local account profile
- Account status
- Application roles
- Account-role assignment

Passwords must not be stored in the NasanBus database.

---

## User Pool

| Field | Value |
|---|---|
| User Pool Name | `nasanbus-user-pool` |
| AWS Region | `us-east-1` |
| User Pool ID | `us-east-1_sEDxoPqSt` |

---

## Cognito Domain

| Field | Value |
|---|---|
| Domain Type | Cognito domain |
| Branding Version | Managed login |
| Domain URL | `https://us-east-1sedxopqst.auth.us-east-1.amazoncognito.com` |

---

## App Client

| Field | Value |
|---|---|
| App Client Name | `nasanbus-web-client` |
| Client Secret | None |
| Client Type | Public client |
| Access Token Expiration | 60 minutes |
| ID Token Expiration | 60 minutes |
| Refresh Token Expiration | 5 days |
| Authentication Flow Session Duration | 3 minutes |

> Do not use a client secret for frontend, mobile, or PWA clients.

---

## App Client ID

```txt
<replace-with-app-client-id>
```

Keep this as configuration.

Recommended frontend environment variable name:

```txt
VITE_COGNITO_CLIENT_ID=<replace-with-app-client-id>
```

Do not hardcode this directly inside frontend source files if environment variables are available.

---

## Authentication Flows

Enabled flows:

- Choice-based sign-in: `ALLOW_USER_AUTH`
- Secure Remote Password authentication: `ALLOW_USER_SRP_AUTH`
- Refresh token authentication: `ALLOW_REFRESH_TOKEN_AUTH`

---

## Managed Login URLs

### Allowed Callback URLs

```txt
http://localhost:5173/auth/callback
```

### Default Redirect URL

```txt
http://localhost:5173/auth/callback
```

### Allowed Sign-out URLs

```txt
http://localhost:5173/login
```

---

## Identity Provider

Enabled identity provider:

```txt
Cognito user pool
```

---

## OAuth 2.0 Grant Types

Enabled grant type:

```txt
Authorization code grant
```

For frontend/PWA usage, this should be used with PKCE.

---

## OpenID Connect Scopes

Enabled scopes:

```txt
openid
email
profile
phone
```

Minimum required scopes for NasanBus:

```txt
openid
email
profile
```

`phone` is optional, but can be kept if phone number will be collected during registration.

---

## Backend Issuer URI

The Spring Boot backend uses this value to validate Cognito JWT tokens:

```txt
https://cognito-idp.us-east-1.amazonaws.com/us-east-1_sEDxoPqSt
```

In `application.yml`, this can be configured as:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://cognito-idp.us-east-1.amazonaws.com/us-east-1_sEDxoPqSt
```

Recommended environment variable version:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${COGNITO_ISSUER_URI}
```

Recommended local environment value:

```txt
COGNITO_ISSUER_URI=https://cognito-idp.us-east-1.amazonaws.com/us-east-1_sEDxoPqSt
```

---

## Token Signing Key URL

Cognito exposes the JSON Web Key Set here:

```txt
https://cognito-idp.us-east-1.amazonaws.com/us-east-1_sEDxoPqSt/.well-known/jwks.json
```

Spring Security uses the issuer URI to discover this automatically.

---

## Frontend Environment Variables

For the future NasanBus frontend, use environment variables like:

```txt
VITE_COGNITO_REGION=us-east-1
VITE_COGNITO_USER_POOL_ID=us-east-1_sEDxoPqSt
VITE_COGNITO_CLIENT_ID=<replace-with-app-client-id>
VITE_COGNITO_DOMAIN=https://us-east-1sedxopqst.auth.us-east-1.amazoncognito.com
VITE_COGNITO_REDIRECT_URI=http://localhost:5173/auth/callback
VITE_COGNITO_LOGOUT_URI=http://localhost:5173/login
```

---

## Backend Environment Variables

For the NasanBus backend, use:

```txt
COGNITO_ISSUER_URI=https://cognito-idp.us-east-1.amazonaws.com/us-east-1_sEDxoPqSt
```

Optional backend variables:

```txt
COGNITO_REGION=us-east-1
COGNITO_USER_POOL_ID=us-east-1_sEDxoPqSt
```

---

## Auth Architecture Notes

Authentication source of truth:

```txt
AWS Cognito
```

Application profile source of truth:

```txt
users.accounts
```

Application role source of truth:

```txt
users.roles
users.account_roles
```

Cognito should only authenticate the user.

NasanBus backend should decide what the user is allowed to do using local database roles.

---

## Security Notes

Do not commit:

- User passwords
- Access tokens
- ID tokens
- Refresh tokens
- Client secrets
- AWS secret keys

The current app client is a public client and has no client secret.

The App Client ID is not a password, but it should still be treated as configuration.

---

## Local Login Test Checklist

- Cognito User Pool exists.
- App Client exists.
- App Client has no client secret.
- Cognito domain exists.
- Managed login is enabled.
- Callback URL is configured.
- Sign-out URL is configured.
- Authorization code grant is enabled.
- Scopes include `openid`, `email`, and `profile`.
- Test user can open the Cognito login page.
- After login, Cognito redirects to `http://localhost:5173/auth/callback`.

---

## Related Sprint Stories

- US3: AWS Cognito Setup
- US4: JWT Security Integration
- US5: Cognito User Sync
- US6: Current User Endpoint
