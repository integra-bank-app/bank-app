CREATE TABLE investment_history (
    id UUID PRIMARY KEY,
    balance NUMERIC,
    date TIMESTAMP,
    investment_id UUID REFERENCES investment(id)
);