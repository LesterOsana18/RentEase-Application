package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class DBConfig {

    private static final String URL = "jdbc:mysql://localhost:3306/rentease_application";
    private static final String USER = "root";
    private static String PASSWORD = "";

    static {
        int user = 2; // Set the user value here

        if (user == 1) {
            PASSWORD = "lesterosana";
        } else if (user == 2) {
            PASSWORD = "1123";
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
        Parent root = null;

        if (username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBConfig.class.getResource(fxmlFile));
                root = loader.load();
                // Dashboard dashboard = loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(DBConfig.class.getResource(fxmlFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1200, 780));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()) {
                System.out.println("User already exists!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username.");
                alert.show();
            } else {
                psInsert = connection.prepareStatement("INSERT INTO users (username) VALUES (?)");
                psInsert.setString(1, username);
                psInsert.executeUpdate();

                changeScene(event, "/controller/LoginView.fxml", "RentEase: Dashboard", null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psCheckUserExists != null) {
                try {
                    psCheckUserExists.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void logInUser(ActionEvent event, String username) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT user_id, username FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("User not found.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect!");
                alert.show();
            } else {
                while (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String retrievedUsername = resultSet.getString("username");

                    if (retrievedUsername.equals(username)) {
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setUserData(userId); // Store user ID in the stage
                        changeScene(event, "/controller/DashboardView.fxml", "RentEase: Dashboard", null);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int getUserId(Stage stage) {
        return (int) stage.getUserData();
    }

    public static List<BalanceDueInvoice> getBalanceDueInvoices(Stage stage) {
        List<BalanceDueInvoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM balance_due WHERE b_user_id = ?");
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                invoices.add(new BalanceDueInvoice(
                        resultSet.getString("property"),
                        resultSet.getString("unit"),
                        resultSet.getString("date"),
                        resultSet.getString("bill_type"),
                        resultSet.getDouble("amount"),
                        resultSet.getInt("deposit"),
                        resultSet.getInt("advanced"),
                        resultSet.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return invoices;
    }

    public static List<BalanceDueInvoice> getFilteredBalanceDueInvoices(Stage stage, String month, int year) {
        List<BalanceDueInvoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "SELECT * FROM balance_due WHERE b_user_id = ? AND YEAR(date) = ? AND MONTHNAME(date) = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, year);
            preparedStatement.setString(3, month);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                invoices.add(new BalanceDueInvoice(
                        resultSet.getString("property"),
                        resultSet.getString("unit"),
                        resultSet.getString("date"),
                        resultSet.getString("bill_type"),
                        resultSet.getDouble("amount"),
                        resultSet.getInt("deposit"),
                        resultSet.getInt("advanced"),
                        resultSet.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return invoices;
    }

    public static List<PaymentHistoryInvoice> getPaymentHistoryInvoices(Stage stage) {
        List<PaymentHistoryInvoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM payment_history WHERE p_user_id = ?");
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                invoices.add(new PaymentHistoryInvoice(
                        resultSet.getString("property"),
                        resultSet.getString("unit"),
                        resultSet.getString("date"),
                        resultSet.getString("bill_type"),
                        resultSet.getDouble("amount"),
                        resultSet.getInt("deposit"),
                        resultSet.getInt("advanced"),
                        resultSet.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return invoices;
    }

    public static List<PaymentHistoryInvoice> getFilteredPaymentHistoryInvoices(Stage stage, String month, int year) {
        List<PaymentHistoryInvoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "SELECT * FROM payment_history WHERE p_user_id = ? AND YEAR(date) = ? AND MONTHNAME(date) = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, year);
            preparedStatement.setString(3, month);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                invoices.add(new PaymentHistoryInvoice(
                        resultSet.getString("property"),
                        resultSet.getString("unit"),
                        resultSet.getString("date"),
                        resultSet.getString("bill_type"),
                        resultSet.getDouble("amount"),
                        resultSet.getInt("deposit"),
                        resultSet.getInt("advanced"),
                        resultSet.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return invoices;
    }

    public static int getTotalDistinctProperties(Stage stage) {
        int totalProperties = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "SELECT COUNT(DISTINCT property) AS total FROM ("
                         + "SELECT property FROM payment_history WHERE p_user_id = ? "
                         + "UNION "
                         + "SELECT property FROM balance_due WHERE b_user_id = ?"
                         + ") AS combined";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                totalProperties = resultSet.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return totalProperties;
    }

    public static int getTotalDistinctUnits(Stage stage) {
        int totalUnits = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "SELECT COUNT(DISTINCT unit) AS total FROM ("
                         + "SELECT unit FROM payment_history WHERE p_user_id = ? "
                         + "UNION "
                         + "SELECT unit FROM balance_due WHERE b_user_id = ?"
                         + ") AS combined";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                totalUnits = resultSet.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return totalUnits;
    }

    public static double getTotalRevenue(Stage stage) {
        double totalRevenue = 0.0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "SELECT SUM(amount) AS total FROM payment_history WHERE p_user_id = ? AND bill_type = 'Rent'";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                totalRevenue = resultSet.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return totalRevenue;
    }

    public static List<BalanceDueInvoice> getDashboardBalanceDueInvoices(Stage stage) {
        List<BalanceDueInvoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT property, unit, date, bill_type, amount FROM balance_due WHERE b_user_id = ?");
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                invoices.add(new BalanceDueInvoice(
                        resultSet.getString("property"),
                        resultSet.getString("unit"),
                        resultSet.getString("date"),
                        resultSet.getString("bill_type"),
                        resultSet.getDouble("amount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return invoices;
    }

    public static List<PaymentHistoryInvoice> getDashboardPaymentHistoryInvoices(Stage stage) {
        List<PaymentHistoryInvoice> invoices = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT property, unit, date, bill_type, amount FROM payment_history WHERE p_user_id = ?");
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                invoices.add(new PaymentHistoryInvoice(
                        resultSet.getString("property"),
                        resultSet.getString("unit"),
                        resultSet.getString("date"),
                        resultSet.getString("bill_type"),
                        resultSet.getDouble("amount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return invoices;
    }
    
    public static String getUsername(Stage stage) {
        String username = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT username FROM users WHERE user_id = ?");
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                username = resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return username;
    }
    
    public static void updateUsername(Stage stage, String newUsername) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            preparedStatement = connection.prepareStatement("UPDATE users SET username = ? WHERE user_id = ?");
            preparedStatement.setString(1, newUsername);
            preparedStatement.setInt(2, userId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Username updated successfully.");
            } else {
                System.out.println("Failed to update username.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteUser(Stage stage) {
        Connection connection = null;
        PreparedStatement deleteUserStmt = null;
        PreparedStatement deletePaymentHistoryStmt = null;
        PreparedStatement deleteBalanceDueStmt = null;

        try {
            int userId = getUserId(stage);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Delete from payment_history
            deletePaymentHistoryStmt = connection.prepareStatement("DELETE FROM payment_history WHERE p_user_id = ?");
            deletePaymentHistoryStmt.setInt(1, userId);
            deletePaymentHistoryStmt.executeUpdate();

            // Delete from balance_due
            deleteBalanceDueStmt = connection.prepareStatement("DELETE FROM balance_due WHERE b_user_id = ?");
            deleteBalanceDueStmt.setInt(1, userId);
            deleteBalanceDueStmt.executeUpdate();

            // Delete from users
            deleteUserStmt = connection.prepareStatement("DELETE FROM users WHERE user_id = ?");
            deleteUserStmt.setInt(1, userId);
            deleteUserStmt.executeUpdate();

            System.out.println("User and related data deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (deleteUserStmt != null) {
                try {
                    deleteUserStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (deletePaymentHistoryStmt != null) {
                try {
                    deletePaymentHistoryStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (deleteBalanceDueStmt != null) {
                try {
                    deleteBalanceDueStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static int getCurrentUserId(Stage stage) {
        return (int) stage.getUserData();
    }
    
    public static void handleInvoiceUpdate(String tableName, String operation, String property, String unit, String billType, String date, double amount, int depositMonths, int advanceMonths, String status, String note, int userId) throws SQLException {
        String query;
        switch (operation) {
            case "checkExists":
                query = String.format("SELECT 1 FROM %s WHERE property = ? AND unit = ? AND bill_type = ?", tableName);
                break;
            case "insert":
                query = String.format("INSERT INTO %s (property, unit, bill_type, date, amount, deposit, advanced, status, note, b_user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", tableName);
                break;
            case "update":
                query = String.format("UPDATE %s SET date = ?, amount = ?, deposit = ?, advanced = ?, status = ?, note = ? WHERE property = ? AND unit = ? AND bill_type = ?", tableName);
                break;
            case "delete":
                query = String.format("DELETE FROM %s WHERE property = ? AND unit = ? AND bill_type = ?", tableName);
                break;
            default:
                throw new IllegalArgumentException("Invalid operation: " + operation);
        }

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            switch (operation) {
                case "checkExists":
                    statement.setString(1, property);
                    statement.setString(2, unit);
                    statement.setString(3, billType);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        resultSet.next();
                    }
                    break;
                case "insert":
                    statement.setString(1, property);
                    statement.setString(2, unit);
                    statement.setString(3, billType);
                    statement.setString(4, date);
                    statement.setDouble(5, amount);
                    statement.setInt(6, depositMonths);
                    statement.setInt(7, advanceMonths);
                    statement.setString(8, status);
                    statement.setString(9, note);
                    statement.setInt(10, userId);
                    statement.executeUpdate();
                    break;
                case "update":
                    statement.setString(1, date);
                    statement.setDouble(2, amount);
                    statement.setInt(3, depositMonths);
                    statement.setInt(4, advanceMonths);
                    statement.setString(5, status);
                    statement.setString(6, note);
                    statement.setString(7, property);
                    statement.setString(8, unit);
                    statement.setString(9, billType);
                    statement.executeUpdate();
                    break;
                case "delete":
                    statement.setString(1, property);
                    statement.setString(2, unit);
                    statement.setString(3, billType);
                    statement.executeUpdate();
                    break;
            }
        }
    }
    
    public static boolean checkRecordExists(String tableName, String property, String unit, String billType) throws SQLException {
        String query = String.format("SELECT 1 FROM %s WHERE property = ? AND unit = ? AND bill_type = ?", tableName);
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, property);
            statement.setString(2, unit);
            statement.setString(3, billType);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public static boolean insertRecord(String tableName, String property, String unit, String billType, String date, double amount, int deposit, int advanced, String status, String note, int userId) throws SQLException {
        String query = String.format("INSERT INTO %s (property, unit, bill_type, date, amount, deposit, advanced, status, note, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", tableName, tableName.equals("balance_due") ? "b_user_id" : "p_user_id");
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, property);
            statement.setString(2, unit);
            statement.setString(3, billType);
            statement.setString(4, date);
            statement.setDouble(5, amount);
            statement.setInt(6, deposit);
            statement.setInt(7, advanced);
            statement.setString(8, status);
            statement.setString(9, note);
            statement.setInt(10, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static boolean updateRecord(String tableName, String property, String unit, String billType, String date, double amount, int deposit, int advanced, String status, String note) throws SQLException {
        String query = String.format("UPDATE %s SET date = ?, amount = ?, deposit = ?, advanced = ?, status = ?, note = ? WHERE property = ? AND unit = ? AND bill_type = ?", tableName);
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, date);
            statement.setDouble(2, amount);
            statement.setInt(3, deposit);
            statement.setInt(4, advanced);
            statement.setString(5, status);
            statement.setString(6, note);
            statement.setString(7, property);
            statement.setString(8, unit);
            statement.setString(9, billType);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static boolean deleteRecord(String tableName, String property, String unit, String billType) throws SQLException {
        String query = String.format("DELETE FROM %s WHERE property = ? AND unit = ? AND bill_type = ?", tableName);
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, property);
            statement.setString(2, unit);
            statement.setString(3, billType);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    
}