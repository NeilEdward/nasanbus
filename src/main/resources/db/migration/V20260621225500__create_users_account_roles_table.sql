CREATE TABLE users.account_roles (
    account_id UUID NOT NULL,
    role_id UUID NOT NULL,
    added_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_account_roles
        PRIMARY KEY (account_id, role_id),

    CONSTRAINT fk_account_roles_account
        FOREIGN KEY (account_id)
        REFERENCES users.accounts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_account_roles_role
        FOREIGN KEY (role_id)
        REFERENCES users.roles(id)
        ON DELETE RESTRICT
);
