CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    sent BOOLEAN NOT NULL DEFAULT FALSE,
    error_message VARCHAR(1000),
    related_entity_type VARCHAR(50),
    related_entity_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_notif_recipient ON notifications(recipient);
CREATE INDEX idx_notif_type ON notifications(notification_type);
CREATE INDEX idx_notif_sent ON notifications(sent);
