package test.bd;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

public class DatabaseTest {

    // Динамически определяем хост: если тесты запущены в Docker, используем имя сервиса, иначе — localhost
    private static final String DB_HOST = System.getenv("DOCKER_ENV") != null ? "postgres" : "localhost";

    // Параметры подключения из docker-compose.yml
    private static final String URL = "jdbc:postgresql://" + DB_HOST + ":5432/test_db";
    private static final String USER = "test_user";
    private static final String PASSWORD = "test_password";

    private Connection connection;
    private UserOrderDao dao;

    @BeforeEach
    public void setUp() {
        try {
            DriverManager.setLoginTimeout(5);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(true);

            dao = new UserOrderDao(connection);

            DatabaseInitializer.initialize(connection);

        } catch (Exception e) {
            Assertions.fail("Критическая ошибка: Не удалось настроить подключение или подготовить данные: " + e.getMessage());
        }
    }

    @AfterEach
    public void cleanUpData() {
        if (dao != null) {
            try {
                dao.deleteUsersByEmailPattern("%@test.com");
            } catch (Exception e) {
                System.err.println("Предупреждение: Не удалось очистить данные после теста: " + e.getMessage());
            }
        }

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("--- Соединение успешно закрыто и освобождено ---");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Проверка поиска пользователя по части email (должен быть ровно один)")
    public void testFindUserByEmailPart() {
        int count = executeSafe(() -> dao.getUserCountByEmailPart("ivan"));
        Assertions.assertEquals(1, count, "Количество пользователей не соответствует ожидаемому!");
    }

    @Test
    @DisplayName("Проверка вывода активных пользователей, созданных за последнюю неделю")
    public void testGetRecentActiveUsers() {
        List<String> activeUsers = executeSafe(() -> dao.getRecentActiveUsers());

        Assertions.assertNotNull(activeUsers, "Список пользователей не должен быть null");
        Assertions.assertTrue(activeUsers.contains("alex@example.com"), "Список не содержит ожидаемого пользователя");
    }

    @Test
    @DisplayName("Проверка выбора пользователей с суммой заказа больше 1000")
    public void testGetUsersWithLargeOrders() {
        List<String> names = executeSafe(() -> dao.getUsersWithOrdersAmountGreaterThan(1000.0));

        Assertions.assertFalse(names.isEmpty(), "Список пользователей с крупными заказами пуст");
        Assertions.assertTrue(names.contains("Иван Иванов"), "Иван Иванов должен быть в списке крупных заказчиков");
    }

    @Test
    @DisplayName("Проверка атомарной вставки пользователя и заказа в рамках одной транзакции")
    public void testInsertUserAndOrderInTransaction() {
        boolean success = executeSafe(() -> dao.insertUserWithOrder("Новый Тест", "transaction@test.com", 3000.50));
        Assertions.assertTrue(success, "Транзакция завершилась ошибкой");
    }

    @Test
    @DisplayName("Проверка обновления статуса заказа по email пользователя")
    public void testUpdateOrderStatusByEmail() {
        int updatedRows = executeSafe(() -> dao.updateOrderStatusByEmail("alex@example.com", "processing"));
        Assertions.assertTrue(updatedRows > 0, "Ни одна строка статуса заказа не была обновлена");
    }

    /**
     * Утилитарный метод для исключения boilerplate try-catch из тестовых методов.
     */
    private <T> T executeSafe(Callable<T> action) {
        try {
            return action.call();
        } catch (Exception e) {
            Assertions.fail("Тест провален из-за непредвиденного исключения SQL: " + e.getMessage());
            return null;
        }
    }
}
