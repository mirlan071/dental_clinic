-- V10: Add unique constraint to work_schedules and fix version column
ALTER TABLE work_schedules ADD CONSTRAINT uk_work_schedule_doctor_day UNIQUE (doctor_id, day_of_week);
