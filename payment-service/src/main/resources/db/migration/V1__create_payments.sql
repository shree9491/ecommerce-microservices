CREATE TABLE payments (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          order_id UUID NOT NULL,
                          user_id UUID NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          status VARCHAR(30) DEFAULT 'PENDING',
                          payment_method VARCHAR(50) DEFAULT 'MOCK_CARD',
                          transaction_id VARCHAR(100),
                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW()
);