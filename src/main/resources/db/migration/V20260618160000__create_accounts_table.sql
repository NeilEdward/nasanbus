CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE users.accounts (
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       cognito_sub VARCHAR(100) NOT NULL UNIQUE,
       email VARCHAR(255) NOT NULL UNIQUE,
       first_name VARCHAR(100),
       last_name VARCHAR(100),
       phone_number VARCHAR(30),
       status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
       added_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       added_by TEXT NOT NULL,
       updated_by TEXT NOT NULL,
       updated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       deleted_by TEXT,
       deleted_on TIMESTAMP
);
