ALTER TABLE accounts
    ADD COLUMN IF NOT EXISTS operation VARCHAR(255),
    ADD COLUMN IF NOT EXISTS timestamp BIGINT,
    ADD COLUMN IF NOT EXISTS created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
    ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);

ALTER TABLE investment
    ADD COLUMN IF NOT EXISTS operation VARCHAR(255),
    ADD COLUMN IF NOT EXISTS timestamp BIGINT,
    ADD COLUMN IF NOT EXISTS created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
    ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);

UPDATE accounts
SET
    operation = COALESCE(operation, 'UNKNOWN'),
    timestamp = COALESCE(timestamp, EXTRACT(EPOCH FROM NOW()) * 1000),
    created_date = COALESCE(created_date, NOW()),
    created_by = COALESCE(created_by, 'system');

UPDATE investment
SET
    operation = COALESCE(operation, 'UNKNOWN'),
    timestamp = COALESCE(timestamp, EXTRACT(EPOCH FROM NOW()) * 1000),
    created_date = COALESCE(created_date, NOW()),
    created_by = COALESCE(created_by, 'system');
