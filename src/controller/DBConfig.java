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

                changeScene(event, "/controller/LoginView.fxml", "Dashboard", null);
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
                        changeScene(event, "/controller/DashboardView.fxml", "Dashboard", null);
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

    private static int getUserId(Stage stage) {
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
            String query = "SELECT SUM(amount) AS total FROM payment_history WHERE p_user_id = ?";
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
}