CREATE TABLE IF NOT EXISTS offices (
                                       id VARCHAR(36) PRIMARY KEY,
    address TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
    );


CREATE TABLE IF NOT EXISTS administrators (
                                              id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    email_verified BOOLEAN DEFAULT FALSE,
    email_verified_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    is_hr BOOLEAN DEFAULT FALSE,
    office_id VARCHAR(36),
    version BIGINT DEFAULT 0,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_administrator_office FOREIGN KEY (office_id) REFERENCES offices(id)
    );


CREATE TABLE IF NOT EXISTS clients (
                                       id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    email_verified BOOLEAN DEFAULT FALSE,
    email_verified_at TIMESTAMP,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );


CREATE TABLE IF NOT EXISTS devices (
    id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('WORKING', 'DAMAGED', 'IN_REPAIR', 'WRITTEN_OFF')),
    day_rate DECIMAL(10, 2) NOT NULL,
    night_rate DECIMAL(10, 2) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_device_office FOREIGN KEY (office_id) REFERENCES offices(id)
    );


CREATE TABLE IF NOT EXISTS games (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );


CREATE TABLE IF NOT EXISTS device_games (
    id VARCHAR(36) NOT NULL,
    game_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (device_id, game_id),
    CONSTRAINT fk_device_games_device FOREIGN KEY (device_id) REFERENCES devices(id),
    CONSTRAINT fk_device_games_game FOREIGN KEY (game_id) REFERENCES games(id)
    );


CREATE TABLE IF NOT EXISTS rents (
    id VARCHAR(36) PRIMARY KEY,
    client_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'ACTIVE', 'COMPLETED', 'OVERDUE', 'CANCELLED')),
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    promo_id VARCHAR(36),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rent_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_rent_device FOREIGN KEY (device_id) REFERENCES devices(id)
    );


CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(36) PRIMARY KEY,
    contents TEXT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_rent FOREIGN KEY (rent_id) REFERENCES rents(id)
    );


CREATE TABLE IF NOT EXISTS promos (
    id VARCHAR(36) PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    coefficient DECIMAL(3, 2) NOT NULL DEFAULT 1.00,
    description TEXT,
    image_url VARCHAR(255),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );


CREATE TABLE IF NOT EXISTS logs (
    id VARCHAR(36) PRIMARY KEY,
    administrator_id VARCHAR(36) NOT NULL,
    contents TEXT NOT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_administrator FOREIGN KEY (administrator_id) REFERENCES administrators(id)
    );


CREATE TABLE IF NOT EXISTS payments (
    id VARCHAR(36) PRIMARY KEY,
    rent_id VARCHAR(36) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    provider VARCHAR(20) NOT NULL DEFAULT 'MOCK',
    external_transaction_id VARCHAR(100),
    paid_at TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_rent FOREIGN KEY (rent_id) REFERENCES rents(id)
    );


CREATE TABLE IF NOT EXISTS outbox_events (
    id VARCHAR(36) PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id VARCHAR(36) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('NEW', 'SENT')) DEFAULT 'NEW',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );



CREATE INDEX idx_rent_device_status_period ON rents(device_id, status, start_date_time, end_date_time);

CREATE INDEX idx_client_email ON clients(email);
CREATE INDEX idx_admin_email ON administrators(email);


CREATE INDEX idx_outbox_status_created ON outbox_events(status, created_at);


CREATE INDEX idx_payment_status ON payments(status);
CREATE INDEX idx_payment_rent ON payments(rent_id);