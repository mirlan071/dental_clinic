-- V6: Create medical_records and document_attachments tables
CREATE TABLE medical_records (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    appointment_id BIGINT REFERENCES appointments(id),
    diagnosis VARCHAR(5000),
    treatment VARCHAR(5000),
    recommendations VARCHAR(2000),
    notes VARCHAR(5000),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_medrec_patient ON medical_records(patient_id);
CREATE INDEX idx_medrec_doctor ON medical_records(doctor_id);
CREATE INDEX idx_medrec_appointment ON medical_records(appointment_id);

CREATE TABLE document_attachments (
    id BIGSERIAL PRIMARY KEY,
    medical_record_id BIGINT NOT NULL REFERENCES medical_records(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT,
    description VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_doc_attachment_record ON document_attachments(medical_record_id);
