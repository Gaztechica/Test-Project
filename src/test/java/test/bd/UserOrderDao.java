package test.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserOrderDao {
    private final Connection connection;

    public UserOrderDao(Connection connection) {
        this.connection = connection;
    }

    public int getUserCountByEmailPart(String emailPart) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SqlQueries.FIND_USER_BY_EMAIL_PART)) {
            stmt.setString(1, "%" + emailPart + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) count++;
                return count;
            }
        }
    }

    public List<String> getRecentActiveUsers() throws SQLException {
        List<String> emails = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SqlQueries.GET_RECENT_ACTIVE_USERS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                emails.add(rs.getString("email"));
            }
        }
        return emails;
    }

    public List<String> getUsersWithOrdersAmountGreaterThan(double amount) throws SQLException {
        List<String> names = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SqlQueries.GET_USERS_WITH_LARGE_ORDERS)) {
            stmt.setDouble(1, amount);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
        }
        return names;
    }

    public boolean insertUserWithOrder(String name, String email, double amount) throws SQLException {
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            int userId = -1;
            try (PreparedStatement userStmt = connection.prepareStatement(SqlQueries.INSERT_NEW_USER)) {
                userStmt.setString(1, name);
                userStmt.setString(2, email);
                try (ResultSet rs = userStmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    }
                }
            }

            if (userId == -1) {
                throw new SQLException("Не удалось получить ID сгенерированного пользователя");
            }

            try (PreparedStatement orderStmt = connection.prepareStatement(SqlQueries.INSERT_NEW_ORDER)) {
                orderStmt.setInt(1, userId);
                orderStmt.setDouble(2, amount);
                orderStmt.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    public int updateOrderStatusByEmail(String email, String newStatus) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SqlQueries.UPDATE_ORDER_STATUS_BY_EMAIL)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, email);
            return stmt.executeUpdate();
        }
    }

    public void deleteUsersByEmailPattern(String pattern) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SqlQueries.DELETE_USERS_BY_EMAIL_PATTERN)) {
            stmt.setString(1, pattern);
            stmt.executeUpdate();
        }
    }
}

