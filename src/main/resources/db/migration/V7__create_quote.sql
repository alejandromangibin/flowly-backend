-- V5__create_quote.sql

-- Enum type for quote status
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'quote_status') THEN
        CREATE TYPE quote_status AS ENUM ('DRAFT', 'APPROVED', 'SENT', 'SIGNED', 'EXPIRED');
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS quote (
    quote_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    opportunity_id UUID NOT NULL,
    quote_number TEXT NOT NULL UNIQUE,
    status TEXT NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL DEFAULT 0,
    tax DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total DECIMAL(12, 2) NOT NULL,
    expiry_date DATE,
    terms TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_by UUID,

    CONSTRAINT fk_quote_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    CONSTRAINT fk_quote_opportunity FOREIGN KEY (opportunity_id) REFERENCES opportunity(opportunity_id) ON DELETE CASCADE,
    CONSTRAINT fk_quote_approved_by FOREIGN KEY (approved_by) REFERENCES app_user(user_id)
);
