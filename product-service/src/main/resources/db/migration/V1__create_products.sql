CREATE TABLE categories (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            name VARCHAR(100) UNIQUE NOT NULL,
                            description TEXT,
                            created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE products (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(200) NOT NULL,
                          description TEXT,
                          price DECIMAL(10,2) NOT NULL,
                          stock_quantity INT DEFAULT 0,
                          category_id UUID REFERENCES categories(id),
                          sku VARCHAR(100) UNIQUE NOT NULL,
                          active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO categories (name, description) VALUES
                                               ('Electronics', 'Electronic devices and accessories'),
                                               ('Clothing', 'Fashion and apparel'),
                                               ('Books', 'Books and literature'),
                                               ('Food', 'Food and beverages');