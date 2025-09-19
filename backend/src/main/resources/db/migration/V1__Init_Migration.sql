CREATE TABLE accounts
(
    id      UUID             NOT NULL,
    balance DOUBLE PRECISION NOT NULL,
    user_id UUID             NOT NULL,
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

CREATE TABLE branch
(
    id UUID NOT NULL,
    CONSTRAINT pk_branch PRIMARY KEY (id)
);

CREATE TABLE deposits
(
    id            UUID NOT NULL,
    interest_rate DOUBLE PRECISION,
    amount        DOUBLE PRECISION,
    user_id       UUID NOT NULL,
    CONSTRAINT pk_deposits PRIMARY KEY (id)
);

CREATE TABLE fee_tax_transaction
(
    id         UUID             NOT NULL,
    amount     DOUBLE PRECISION NOT NULL,
    user_id    UUID             NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_feetaxtransaction PRIMARY KEY (id)
);

CREATE TABLE investment
(
    id      UUID             NOT NULL,
    risk    INTEGER          NOT NULL,
    balance DOUBLE PRECISION NOT NULL,
    user_id UUID             NOT NULL,
    CONSTRAINT pk_investment PRIMARY KEY (id)
);

CREATE TABLE transactions
(
    id          UUID             NOT NULL,
    amount      DOUBLE PRECISION NOT NULL,
    timestamp   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id     UUID             NOT NULL,
    type        VARCHAR(255)     NOT NULL,
    description VARCHAR(255)     NOT NULL,
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

CREATE TABLE users
(
    id          UUID         NOT NULL,
    first_name  VARCHAR(255) NOT NULL,
    middle_name VARCHAR(255),
    last_name   VARCHAR(255) NOT NULL,
    branch_id   UUID         NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE accounts
    ADD CONSTRAINT FK_ACCOUNTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE deposits
    ADD CONSTRAINT FK_DEPOSITS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE fee_tax_transaction
    ADD CONSTRAINT FK_FEETAXTRANSACTION_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE investment
    ADD CONSTRAINT FK_INVESTMENT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch (id);