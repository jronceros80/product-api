-- Ensure the user has all necessary privileges
GRANT ALL PRIVILEGES ON DATABASE products_db TO products_user;

-- Connect to the products_db database to set up schema permissions
\c products_db;

-- Grant schema permissions
GRANT ALL ON SCHEMA public TO products_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO products_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO products_user;

-- Grant future privileges
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO products_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO products_user;

-- Elimina la tabla si existe
DROP TABLE IF EXISTS products;

-- Crea la tabla con definición PostgreSQL compatible
CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  price NUMERIC(10, 2) NOT NULL,
  category VARCHAR(20) NOT NULL,
  active BOOLEAN DEFAULT TRUE
);