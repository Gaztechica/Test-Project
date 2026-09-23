package test.bd;

import org.junit.jupiter.api.Order;

import java.util.ArrayList;
import java.util.List;

public record User(
        int id,
        String name,
        String email,
        String status,
        List<Order> orders
) {
    public User(int id, String name, String email, String status) {
        this(id, name, email, status, new ArrayList<>());
    }
}
