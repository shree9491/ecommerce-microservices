CREATE TABLE orders (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        user_id UUID NOT NULL,
                        status VARCHAR(30) DEFAULT 'PENDING',
                        total_amount DECIMAL(10,2) NOT NULL,
                        created_at TIMESTAMP DEFAULT NOW(),
                        updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE order_items (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
                             product_id UUID NOT NULL,
                             product_name VARCHAR(200) NOT NULL,
                             quantity INT NOT NULL,
                             unit_price DECIMAL(10,2) NOT NULL,
                             total_price DECIMAL(10,2) NOT NULL
);