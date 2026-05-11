-- =========================
-- REMOVE USER FOREIGN KEYS FROM CMS TABLES
-- =========================
-- Reason:
-- User identity now lives in auth-service.
-- CMS only stores auth user UUID in created_by / updated_by.
-- CMS must not enforce FK to local users table.

ALTER TABLE IF EXISTS articles
DROP CONSTRAINT IF EXISTS articles_created_by_fkey;

ALTER TABLE IF EXISTS articles
DROP CONSTRAINT IF EXISTS articles_updated_by_fkey;


ALTER TABLE IF EXISTS domains
DROP CONSTRAINT IF EXISTS domains_created_by_fkey;

ALTER TABLE IF EXISTS domains
DROP CONSTRAINT IF EXISTS domains_updated_by_fkey;


ALTER TABLE IF EXISTS authors
DROP CONSTRAINT IF EXISTS authors_created_by_fkey;

ALTER TABLE IF EXISTS authors
DROP CONSTRAINT IF EXISTS authors_updated_by_fkey;


ALTER TABLE IF EXISTS categories
DROP CONSTRAINT IF EXISTS categories_created_by_fkey;

ALTER TABLE IF EXISTS categories
DROP CONSTRAINT IF EXISTS categories_updated_by_fkey;


ALTER TABLE IF EXISTS media
DROP CONSTRAINT IF EXISTS media_created_by_fkey;

ALTER TABLE IF EXISTS media
DROP CONSTRAINT IF EXISTS media_updated_by_fkey;