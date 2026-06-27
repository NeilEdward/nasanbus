# Cognito Token and Postman Testing

## Purpose

Use this guide to get a Cognito token with AWS CLI and test authenticated
backend endpoints in Postman.

Current backend sync endpoint:

```txt
POST http://localhost:8080/api/v1/accounts/sync
```

Use the Cognito ID token for this endpoint because account sync needs identity
and profile claims such as `sub`, `email`, `given_name`, and `family_name`.

Do not use the refresh token for backend API calls.

## Prerequisites

- AWS CLI is installed.
- AWS CLI credentials are configured for the correct AWS account.
- Cognito app client allows `USER_PASSWORD_AUTH`.
- Backend is configured with the Cognito issuer URI.

Example issuer:

```powershell
$env:COGNITO_ISSUER_URI="https://cognito-idp.us-east-1.amazonaws.com/us-east-1_sEDxoPqSt"
```

Start the backend:

```powershell
.\gradlew.bat bootRun
```

## Get Tokens with AWS CLI

Run:

```powershell
aws cognito-idp initiate-auth `
  --region us-east-1 `
  --auth-flow USER_PASSWORD_AUTH `
  --client-id "<cognito-app-client-id>" `
  --auth-parameters USERNAME="<email>",PASSWORD="<password>"
```

If authentication succeeds, Cognito returns:

```json
{
  "AuthenticationResult": {
    "AccessToken": "...",
    "IdToken": "...",
    "RefreshToken": "..."
  }
}
```

Copy the `IdToken`.

## Complete New Password Challenge

If Cognito returns:

```txt
NEW_PASSWORD_REQUIRED
```

copy the returned `Session` value and run:

```powershell
aws cognito-idp respond-to-auth-challenge `
  --region us-east-1 `
  --client-id "<cognito-app-client-id>" `
  --challenge-name NEW_PASSWORD_REQUIRED `
  --session "<session-from-initiate-auth>" `
  --challenge-responses USERNAME="<email>",NEW_PASSWORD="<new-password>",userAttributes.given_name="<first-name>",userAttributes.family_name="<last-name>"
```

If successful, Cognito returns `AuthenticationResult`. Copy the `IdToken`.

## Test in Postman

Create a request:

```txt
POST http://localhost:8080/api/v1/accounts/sync
```

In the Authorization tab:

```txt
Type: Bearer Token
Token: <IdToken>
```

In the Body tab:

```txt
none
```

Do not send `cognitoSub`, `email`, `status`, or `roles` in the request body.
The backend reads identity values from the authenticated JWT and controls local
status and roles.

Expected response:

```json
{
  "id": "local-account-uuid",
  "cognitoSub": "cognito-user-sub",
  "email": "user@example.com",
  "firstName": "Admin",
  "lastName": "Nasan",
  "phoneNumber": null,
  "status": "ACTIVE",
  "roles": []
}
```

## Token Choice

Use:

```txt
IdToken
```

for account sync.

The ID token usually contains:

```txt
sub
email
given_name
family_name
phone_number
```

The access token may not include `email` or profile claims.

Do not use:

```txt
RefreshToken
```

for API calls. It is only used to request new tokens.

## Troubleshooting

If Postman returns `401 Unauthorized`:

- Confirm the backend is running.
- Confirm `COGNITO_ISSUER_URI` matches the token issuer.
- Confirm the token is not expired.
- Confirm the token came from the same Cognito user pool.

If the backend reports that the email claim is missing:

- Use the `IdToken` instead of the access token.
- Confirm Cognito scopes include `openid`, `email`, and `profile`.

If AWS CLI says the app client does not exist:

- Confirm the AWS region.
- Confirm the app client ID.
- Confirm AWS CLI credentials point to the correct AWS account.
