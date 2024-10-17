CREATE TABLE failed_transaction (
                                    id BIGSERIAL PRIMARY KEY,
                                    original_transaction_id BIGINT NOT NULL,
                                    account_id BIGINT NOT NULL
);