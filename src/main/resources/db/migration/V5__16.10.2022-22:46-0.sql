UPDATE users AS u SET version = 0 WHERE version IS NULL;
UPDATE persons SET version = 0 WHERE version IS NULL;
UPDATE addresses SET version = 0 WHERE version IS NULL;
UPDATE users SET created_date_time = now() WHERE created_date_time IS NULL;
UPDATE persons SET created_date_time = now() WHERE created_date_time IS NULL;
UPDATE addresses SET created_date_time = now() WHERE created_date_time IS NULL;
UPDATE users SET last_modified_date_time = now() WHERE last_modified_date_time IS NULL;
UPDATE persons SET last_modified_date_time = now() WHERE last_modified_date_time IS NULL;
UPDATE addresses SET last_modified_date_time = now() WHERE last_modified_date_time IS NULL;
