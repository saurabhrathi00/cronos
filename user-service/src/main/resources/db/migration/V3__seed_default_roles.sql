INSERT INTO role_entity (name)
VALUES ('ROLE_USER')
ON CONFLICT DO NOTHING;

INSERT INTO role_scopes (role_name, scope)
VALUES ('ROLE_USER', 'jobs.create')
ON CONFLICT DO NOTHING;
