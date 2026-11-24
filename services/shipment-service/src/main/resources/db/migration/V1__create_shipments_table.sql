CREATE TABLE shipments
(
    id                  UUID PRIMARY KEY,
    tracking_number     VARCHAR(20)  NOT NULL UNIQUE,
    sender              VARCHAR(255) NOT NULL,
    recipient           VARCHAR(255) NOT NULL,
    destination_address VARCHAR(500) NOT NULL,
    status              VARCHAR(30)  NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL,
    updated_at          TIMESTAMPTZ  NOT NULL,
    version             BIGINT       NOT NULL
);
