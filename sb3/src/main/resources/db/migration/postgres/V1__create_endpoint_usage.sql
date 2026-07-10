CREATE TABLE IF NOT EXISTS endpoint_usage (
    user_id TEXT NOT NULL,
    endpoint TEXT NOT NULL,
    invocation_count INTEGER NOT NULL,
    last_used_date TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (user_id, endpoint)
);
