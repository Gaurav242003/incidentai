CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    available_quantity INTEGER NOT NULL,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT chk_available_quantity_non_negative
        CHECK (available_quantity >= 0),

    CONSTRAINT chk_reserved_quantity_non_negative
        CHECK (reserved_quantity >= 0)
);

INSERT INTO inventory (
    product_id,
    available_quantity,
    reserved_quantity
)
VALUES
    (1, 50, 0),
    (2, 20, 0),
    (3, 100, 0);