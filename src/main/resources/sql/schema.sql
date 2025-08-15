CREATE TABLE IF NOT EXISTS blog_post (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(250)                          NOT NULL,
    content     TEXT                                  NOT NULL,
    author      VARCHAR(50)                           NOT NULL,
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP   DEFAULT NULL,
    updated_by  VARCHAR(50) DEFAULT NULL,
    image_url   VARCHAR(500)
);