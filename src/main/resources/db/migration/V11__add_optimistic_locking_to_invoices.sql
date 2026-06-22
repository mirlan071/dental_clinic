-- V9: Add optimistic locking version column to invoices
ALTER TABLE invoices ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
