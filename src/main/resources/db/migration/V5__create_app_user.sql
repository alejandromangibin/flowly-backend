CREATE TABLE IF NOT EXISTS app_user (
	user_id uuid NOT NULL,
	created_at timestamp(6) NULL,
	department varchar(255) NULL,
	email varchar(255) NULL,
	first_name varchar(255) NULL,
	last_login_at timestamp(6) NULL,
	last_name varchar(255) NULL,
	"password" varchar(255) NULL,
	"role" varchar(255) NULL,
	username varchar(255) NULL,
	CONSTRAINT app_user_pkey PRIMARY KEY (user_id)
);