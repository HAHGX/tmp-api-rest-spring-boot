CREATE TABLE IF NOT EXISTS api_call_history (
    id SERIAL PRIMARY KEY,
    endpoint VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    request_params TEXT,
    response_body TEXT,
    status_code INTEGER,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_call_history_created_at ON api_call_history(created_at);
CREATE INDEX idx_api_call_history_endpoint ON api_call_history(endpoint);