-- 1. Таблица постов
CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    likes_count INTEGER NOT NULL DEFAULT 0,
    comments_count INTEGER NOT NULL DEFAULT 0,
    image BYTEA,  -- Для хранения картинки (byte array)
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

-- 3. Таблица тегов
CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- 4. Связующая таблица пост‑теги (многие‑ко‑многим)
CREATE TABLE IF NOT EXISTS post_tags (
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_tag
        FOREIGN KEY (tag_id)
        REFERENCES tags (id)
        ON DELETE CASCADE
);

-- 5. Индексы для ускорения запросов
CREATE INDEX IF NOT EXISTS idx_posts_title ON posts USING GIN (title gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_posts_created ON posts (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments (post_id);
CREATE INDEX IF NOT EXISTS idx_tags_name ON tags (name);

-- 6. Триггер для обновления updated_at у поста
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_posts_updated_at
    BEFORE UPDATE ON posts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
