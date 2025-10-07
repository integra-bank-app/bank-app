ALTER TABLE users
    ADD COLUMN email VARCHAR(255),
    ADD COLUMN password VARCHAR(255),
    ADD COLUMN role VARCHAR(32) DEFAULT 'USER';

UPDATE users
SET email = CONCAT(LOWER(first_name), '.', LOWER(last_name), '@gmail.com'),
    password = 'password123', role = 'USER'
WHERE email IS NULL OR password IS NULL OR role IS NULL;

ALTER TABLE users
    ALTER COLUMN email SET NOT NULL,
    ALTER COLUMN password SET NOT NULL,
    ALTER COLUMN role SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email),
    ADD CONSTRAINT check_email_length CHECK (LENGTH(email) > 3),
    ADD CONSTRAINT check_password_length CHECK (LENGTH(password) >= 6),
    ADD CONSTRAINT check_first_name_length CHECK (LENGTH(first_name) >= 3),
    ADD CONSTRAINT check_last_name_length CHECK (LENGTH(last_name) >= 3);