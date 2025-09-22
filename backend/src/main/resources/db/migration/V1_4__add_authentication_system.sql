CREATE TABLE auth_users_details
(
    id       UUID         NOT NULL,
    username VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(255),
    user_id  UUID         NOT NULL,
    CONSTRAINT pk_auth_users_details PRIMARY KEY (id)
);

ALTER TABLE auth_users_details
    ADD CONSTRAINT uc_auth_users_details_email UNIQUE (email);

ALTER TABLE auth_users_details
    ADD CONSTRAINT uc_auth_users_details_username UNIQUE (username);