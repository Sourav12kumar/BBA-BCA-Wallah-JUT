CREATE TABLE subject (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NULL,
    course VARCHAR(50) NOT NULL,
    semester INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uk_subject_name_course_semester UNIQUE (name, course, semester)
);

CREATE TABLE academic_resource (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    course VARCHAR(50) NOT NULL,
    semester INT NOT NULL,
    subject_id BIGINT NULL,
    type VARCHAR(50) NOT NULL,
    session_year VARCHAR(255) NULL,
    file_url VARCHAR(255) NULL,
    description VARCHAR(1500) NULL,
    download_count BIGINT NOT NULL DEFAULT 0,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_academic_resource_subject
        FOREIGN KEY (subject_id) REFERENCES subject(id)
);

CREATE INDEX idx_academic_resource_subject ON academic_resource(subject_id);
CREATE INDEX idx_academic_resource_course_semester ON academic_resource(course, semester);
CREATE INDEX idx_academic_resource_active_featured ON academic_resource(active, featured);

CREATE TABLE notice (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(3000) NULL,
    category VARCHAR(255) NULL,
    external_url VARCHAR(255) NULL,
    notice_date DATE NULL,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_notice_active_pinned ON notice(active, pinned);
CREATE INDEX idx_notice_date ON notice(notice_date);

CREATE TABLE opportunity (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    company VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    location VARCHAR(255) NULL,
    batch VARCHAR(255) NULL,
    eligible_courses VARCHAR(255) NULL,
    eligibility VARCHAR(255) NULL,
    apply_url VARCHAR(255) NULL,
    deadline DATE NULL,
    description VARCHAR(2500) NULL,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_opportunity_type_active ON opportunity(type, active);
CREATE INDEX idx_opportunity_deadline ON opportunity(deadline);

CREATE TABLE download_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    resource_id BIGINT NOT NULL,
    resource_title VARCHAR(255) NOT NULL,
    downloaded_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_download_event_time ON download_events(downloaded_at);
CREATE INDEX idx_download_event_resource ON download_events(resource_id);
