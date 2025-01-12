CREATE TABLE todo (
    id SERIAL PRIMARY KEY,         -- Auto-incrementing primary key
    title VARCHAR(255) NOT NULL,   -- Title is required
    description TEXT,              -- Description is optional
    is_done BOOLEAN DEFAULT FALSE  -- Default value is false
);
