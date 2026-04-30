-- MAKAUT Mate Database Schema

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    semester INTEGER DEFAULT 1,
    department VARCHAR(100),
    college VARCHAR(150),
    role VARCHAR(20) DEFAULT 'student',
    is_premium BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Resources Table (Notes, PYQs, Books, Organizers)
CREATE TABLE IF NOT EXISTS resources (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100),
    semester VARCHAR(20),
    subject VARCHAR(100),
    course VARCHAR(100),
    type VARCHAR(50), -- 'Notes', 'Books', 'Organizers', 'PYQs'
    file_url TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending', -- 'pending', 'approved', 'rejected'
    uploader_id UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Academic Results Table
CREATE TABLE IF NOT EXISTS results (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    semester INTEGER NOT NULL,
    sgpa DECIMAL(4, 2),
    total_credits INTEGER,
    results_json JSONB, -- Detailed subject-wise marks
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Syllabus Table
CREATE TABLE IF NOT EXISTS syllabus (
    id SERIAL PRIMARY KEY,
    course VARCHAR(100),
    department VARCHAR(100),
    semester INTEGER,
    subject VARCHAR(100),
    module_details JSONB,
    file_url TEXT
);

-- CA Marks Table
CREATE TABLE IF NOT EXISTS ca_marks (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    subject VARCHAR(100),
    ca_type VARCHAR(10), -- 'CA1', 'CA2', etc.
    marks DECIMAL(5, 2),
    max_marks DECIMAL(5, 2) DEFAULT 25.0,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    message TEXT,
    type VARCHAR(50),
    target_semester INTEGER,
    target_dept VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
