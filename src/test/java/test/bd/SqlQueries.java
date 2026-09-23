package test.bd;

public final class SqlQueries {
    private SqlQueries() {} // Запрещаем создание экземпляров

    public static final String CREATE_USERS_TABLE = """
    CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        email VARCHAR(100) UNIQUE NOT NULL,
        status VARCHAR(20) NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );
    """;

    public static final String CREATE_ORDERS_TABLE = """
        CREATE TABLE IF NOT EXISTS orders (
            id SERIAL PRIMARY KEY,
            user_id INT REFERENCES users(id) ON DELETE CASCADE,
            amount NUMERIC(10, 2) NOT NULL,
            status VARCHAR(20) NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT NOW()
        );
        """;

    public static final String INSERT_TEST_USERS = """
    INSERT INTO users (id, name, email, status, created_at) VALUES
    (1, 'Иван Иванов', 'ivan@test.com', 'active', NOW() - INTERVAL '3 days'),
    (2, 'Пётр Петров', 'petr@example.com', 'inactive', NOW() - INTERVAL '10 days'),
    (3, 'Алексей Иванов', 'alex@example.com', 'active', NOW() - INTERVAL '2 days')
    ON CONFLICT (id) DO NOTHING;
    """;

    public static final String INSERT_TEST_ORDERS = """
        INSERT INTO orders (id, user_id, amount, status, created_at) VALUES
        (1, 1, 1500.00, 'new', NOW()),
        (2, 3, 500.00, 'completed', NOW())
        ON CONFLICT (id) DO NOTHING;
        """;

    public static final String FIND_USER_BY_EMAIL_PART = """
        SELECT * FROM users WHERE email LIKE ?
        """;

    public static final String GET_RECENT_ACTIVE_USERS = """
        SELECT * FROM users
        WHERE status = 'active'
          AND created_at >= NOW() - INTERVAL '7 days'
        """;

    public static final String GET_USERS_WITH_LARGE_ORDERS = """
        SELECT DISTINCT u.* FROM users u
        JOIN orders o ON u.id = o.user_id
        WHERE o.amount > ?
        """;

    public static final String INSERT_NEW_USER = """
        INSERT INTO users (name, email, status, created_at)
        VALUES (?, ?, 'active', NOW()) RETURNING id
        """;

    public static final String INSERT_NEW_ORDER = """
        INSERT INTO orders (user_id, amount, status, created_at)
        VALUES (?, ?, 'new', NOW())
        """;

    public static final String UPDATE_ORDER_STATUS_BY_EMAIL = """
        UPDATE orders
        SET status = ?
        WHERE user_id IN (SELECT id FROM users WHERE email = ?)
        """;

    public static final String DELETE_USERS_BY_EMAIL_PATTERN = """
        DELETE FROM users WHERE email LIKE ?
        """;
}

