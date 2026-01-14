-- 1. Таблица постов
CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    tags VARCHAR(255)[],
    likes_count INTEGER NOT NULL DEFAULT 0,
    comments_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Таблица комментариев
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT fk_post
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
        ON DELETE CASCADE
);

-- 5. Индексы для ускорения запросов
CREATE INDEX IF NOT EXISTS idx_posts_title ON posts (title);
CREATE INDEX IF NOT EXISTS idx_posts_tags ON posts (tags);
CREATE INDEX IF NOT EXISTS idx_posts_created ON posts (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments (post_id);
