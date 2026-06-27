# NasanBus Account Sync

## Overview

AWS Cognito authenticates users.

NasanBus stores application-specific account data in PostgreSQL.

Cognito is responsible for login, password management, and token issuance.
PostgreSQL is responsible for local account profile, account status, and
application roles.

## Source of Truth

Authentication:

- AWS Cognito

Application profile:

- `users.accounts`

Application roles:

- `users.roles`
- `users.account_roles`

## Sync Endpoint

```txt
POST /api/v1/accounts/sync
```

## Required Header

```txt
Authorization: Bearer <id-token>
```

Use the Cognito ID token for account sync because the backend needs identity
and profile claims such as `sub`, `email`, `given_name`, and `family_name`.

## Sync Rules

1. Backend reads Cognito `sub` from the authenticated JWT.
2. Backend finds the local account by `users.accounts.cognito_sub`.
3. If the account exists, backend returns the local account profile.
4. If the account does not exist, backend checks whether the email already
   belongs to another local account.
5. If the email is already linked to a different Cognito `sub`, backend returns
   `409 Conflict`.
6. If the email is not linked, backend creates a local account from Cognito
   claims.
7. Backend does not accept `cognitoSub`, `email`, `status`, or `roles` from the
   frontend request body.
8. Backend does not store passwords.

## Roles

New synced accounts do not receive privileged operational roles by default.

Privileged roles such as `ADMIN`, `DRIVER`, and `CONDUCTOR` must be assigned by
a backend-controlled admin flow.

The frontend must not assign roles during account sync.

## Conflict Behavior

Duplicate email with a different Cognito `sub` is rejected to avoid accidental
account takeover or corrupted identity linking.

Expected response:

```txt
409 Conflict
```

Expected detail:

```txt
Account email is already linked to another Cognito user
```

## Security Notes

Do not commit:

- User passwords
- Access tokens
- ID tokens
- Refresh tokens
- Client secrets
- AWS secret keys

The local account record links to Cognito using `cognito_sub`, not password
credentials.
