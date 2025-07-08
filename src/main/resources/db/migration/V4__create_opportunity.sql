-- V4__create_opportunity.sql

-- Create enum type for opportunity stage
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'opportunity_stage') THEN
        CREATE TYPE opportunity_stage AS ENUM ('PROPOSAL', 'NEGOTIATION', 'CLOSED');
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS opportunity (
    opportunity_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    stage TEXT NOT NULL,
    expected_value DECIMAL(12, 2) NOT NULL,
    close_date DATE,
    CONSTRAINT fk_opportunity_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE
);
