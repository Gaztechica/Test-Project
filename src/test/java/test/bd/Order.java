package test.bd;

public record Order(

        int id,
        int userId,
        double amount,
        String status
) {}