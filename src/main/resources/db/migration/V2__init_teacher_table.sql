CREATE TABLE IF NOT EXISTS teacher (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    age     INT,
    clazz   VARCHAR(50),
    phone   VARCHAR(20)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;