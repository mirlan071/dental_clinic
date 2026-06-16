-- V5: Create appointments and appointment_services tables
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    notes VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_appointment_patient ON appointments(patient_id);
CREATE INDEX idx_appointment_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointment_start ON appointments(start_time);
CREATE INDEX idx_appointment_status ON appointments(status);
CREATE INDEX idx_appointment_doctor_time ON appointments(doctor_id, start_time, end_time, status);

CREATE TABLE appointment_services (
    appointment_id BIGINT NOT NULL REFERENCES appointments(id),
    service_id BIGINT NOT NULL REFERENCES dental_services(id),
    PRIMARY KEY (appointment_id, service_id)
);
