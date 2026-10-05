-- ==============================================================================
-- SCENTIVA DATABASE SCHEMA MIGRATION (V3__cms_and_audit.sql)
-- Editorial Stories CMS, Promotional Banners, System Audit Logs
-- ==============================================================================

-- 34. EDITORIAL STORIES (Journal & Olfactory Narratives)
CREATE TABLE IF NOT EXISTS editorial_stories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    subtitle VARCHAR(255),
    excerpt TEXT,
    content_html TEXT NOT NULL,
    cover_image_url VARCHAR(500),
    author_name VARCHAR(150) NOT NULL DEFAULT 'Scentiva Editorial Guild',
    reading_time_minutes INT NOT NULL DEFAULT 4,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMP WITHOUT TIME ZONE,
    tags VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_editorial_stories_slug ON editorial_stories(slug);
CREATE INDEX IF NOT EXISTS idx_editorial_stories_published ON editorial_stories(published_at);

-- Drop old pre-V3 schema for banners if created without placement column
DROP TABLE IF EXISTS banners CASCADE;

-- 35. BANNERS (Hero & Promotional Showcase)
CREATE TABLE banners (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    cta_text VARCHAR(100),
    cta_link VARCHAR(255),
    image_url VARCHAR(500) NOT NULL,
    mobile_image_url VARCHAR(500),
    placement VARCHAR(50) NOT NULL DEFAULT 'HERO_CAROUSEL',
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    starts_at TIMESTAMP WITHOUT TIME ZONE,
    ends_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_banners_placement ON banners(placement);

-- Drop old pre-V3 schema for audit_logs if created without user_email
DROP TABLE IF EXISTS audit_logs CASCADE;

-- 36. AUDIT LOGS (Security & Administrative Action Ledgers)
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    ip_address VARCHAR(100),
    details TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_email);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created ON audit_logs(created_at);

