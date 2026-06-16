-- V4: Create dental_services table
CREATE TABLE dental_services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    price NUMERIC(10,2) NOT NULL,
    duration_minutes INT NOT NULL,
    category VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_service_category ON dental_services(category);
CREATE INDEX idx_service_name ON dental_services(name);
CREATE INDEX idx_service_active ON dental_services(active);
