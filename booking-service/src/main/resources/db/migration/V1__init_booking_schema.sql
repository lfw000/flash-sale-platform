CREATE TABLE events
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    total_tickets INTEGER NOT NULL,
    available_tickets INTEGER NOT NULL,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE bookings
(
    id VARCHAR(36) PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bookings_event_id ON bookings(event_id);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);

INSERT INTO events (name, total_tickets, available_tickets, version)
VALUES ('Taylor Swift - Eras Tour', 100, 100, 0);
