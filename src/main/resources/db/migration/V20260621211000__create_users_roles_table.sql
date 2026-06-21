CREATE TABLE users.roles (
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       code VARCHAR(50) NOT NULL UNIQUE,
       name VARCHAR(100) NOT NULL,
       description VARCHAR(255),
       added_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       added_by TEXT NOT NULL,
       updated_by TEXT NOT NULL,
       updated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       deleted_by TEXT,
       deleted_on TIMESTAMP
);

INSERT INTO users.roles (code, name, description, added_by, updated_by)
VALUES
    ('COMMUTER', 'Commuter', 'Can view minibus information and later request or wait for rides.', 'SYSTEM', 'SYSTEM'),
    ('DRIVER', 'Driver', 'Can update assigned minibus trip and location status.', 'SYSTEM', 'SYSTEM'),
    ('CONDUCTOR', 'Conductor', 'Can assist with passenger and trip-related status updates.', 'SYSTEM', 'SYSTEM'),
    ('ADMIN', 'Administrator', 'Can manage users, minibuses, routes, and operations.', 'SYSTEM', 'SYSTEM');
