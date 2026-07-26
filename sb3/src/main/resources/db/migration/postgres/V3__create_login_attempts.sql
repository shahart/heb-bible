CREATE TABLE IF NOT EXISTS login_attempts (
    account_key TEXT NOT NULL,
    attempt_id UUID NOT NULL,
    attempted_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (account_key, attempt_id)
);

CREATE INDEX IF NOT EXISTS login_attempts_attempted_at_idx
    ON login_attempts(attempted_at);
