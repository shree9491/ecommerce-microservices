CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(50),
                       last_name VARCHAR(50),
                       phone VARCHAR(20),
                       role VARCHAR(20) DEFAULT 'CUSTOMER',
                       active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                                token VARCHAR(500) UNIQUE NOT NULL,
                                expires_at TIMESTAMP NOT NULL,
                                revoked BOOLEAN DEFAULT FALSE,
                                created_at TIMESTAMP DEFAULT NOW()
);