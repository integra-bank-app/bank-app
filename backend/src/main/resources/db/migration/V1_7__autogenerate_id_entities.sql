CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER TABLE accounts ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE accounts ALTER COLUMN user_id SET DEFAULT gen_random_uuid();

ALTER TABLE branch ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE deposits ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE deposits ALTER COLUMN user_id SET DEFAULT gen_random_uuid();

ALTER TABLE fee_tax_transaction ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE fee_tax_transaction ALTER COLUMN user_id SET DEFAULT gen_random_uuid();

ALTER TABLE investment ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE investment ALTER COLUMN user_id SET DEFAULT gen_random_uuid();

ALTER TABLE notification ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE notification ALTER COLUMN user_id SET DEFAULT gen_random_uuid();

ALTER TABLE transactions ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE transactions ALTER COLUMN user_id SET DEFAULT gen_random_uuid();

ALTER TABLE users ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE users ALTER COLUMN branch_id SET DEFAULT gen_random_uuid();