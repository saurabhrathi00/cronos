CREATE TABLE IF NOT EXISTS role_entity (
  name VARCHAR(100) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS user_entity (
  id VARCHAR(255) PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id VARCHAR(255) NOT NULL,
  role_name VARCHAR(100) NOT NULL,
  CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_name),
  CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id)
    REFERENCES user_entity(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role
    FOREIGN KEY (role_name)
    REFERENCES role_entity(name)
);
