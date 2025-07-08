-- V6__create_ticket.sql

-- Create enums
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'ticket_priority') THEN
        CREATE TYPE ticket_priority AS ENUM ('LOW', 'MEDIUM', 'HIGH');
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'ticket_status') THEN
        CREATE TYPE ticket_status AS ENUM ('OPEN', 'IN-PROGRESS', 'RESOLVED');
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS ticket (
    ticket_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    agent_id UUID NOT NULL,
    priority TEXT NOT NULL,
    status TEXT NOT NULL,

    CONSTRAINT fk_ticket_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_agent FOREIGN KEY (agent_id) REFERENCES agent(agent_id) ON DELETE CASCADE
);
