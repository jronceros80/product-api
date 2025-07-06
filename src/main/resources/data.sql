-- Script de inicialización para desarrollo local
-- Se ejecuta automáticamente al iniciar la aplicación

-- Crear tabla de productos
CREATE TABLE IF NOT EXISTS products (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  price NUMERIC(10, 2) NOT NULL,
  category VARCHAR(20) NOT NULL,
  active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Crear índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_active ON products(active);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);

-- Insertar datos de ejemplo para desarrollo
INSERT INTO products (name, price, category, active) VALUES
('Laptop Dell XPS 13', 1299.99, 'ELECTRONICS', true),
('Book: Clean Code', 45.99, 'BOOKS', true),
('Nike Air Max', 149.99, 'CLOTHING', true),
('Samsung Galaxy S23', 799.99, 'ELECTRONICS', true),
('Java Programming Book', 39.99, 'BOOKS', true),
('Gaming Mouse', 89.99, 'ELECTRONICS', true),
('Mechanical Keyboard', 159.99, 'ELECTRONICS', true),
('Wireless Headphones', 249.99, 'ELECTRONICS', true),
('Running Shoes', 129.99, 'CLOTHING', true),
('Winter Jacket', 199.99, 'CLOTHING', true)
ON CONFLICT DO NOTHING; 