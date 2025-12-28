CREATE TABLE IF NOT EXISTS role_scopes (
  role_name VARCHAR(100) NOT NULL,
  scope VARCHAR(100) NOT NULL,
  CONSTRAINT pk_role_scopes PRIMARY KEY (role_name, scope),
  CONSTRAINT fk_role_scopes_role
    FOREIGN KEY (role_name)
    REFERENCES role_entity(name)
    ON DELETE CASCADE
);
