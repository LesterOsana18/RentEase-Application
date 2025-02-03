package controller;

import java.io.IOException;

import java.net.URL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CreateInvoiceController implements Initializable  {
	
    @Override
    public void initialize(URL location, ResourceBundle resources) {
		ObservableList<Integer> months = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        monthly_deposit_text.setDisable(true);
        monthly_advance_text.setDisable(true);

        monthly_deposit_chk_box.setOnAction(e -> monthly_deposit_text.setDisable(!monthly_deposit_chk_box.isSelected()));
        monthly_advance_chk_box.setOnAction(e -> monthly_advance_text.setDisable(!monthly_advance_chk_box.isSelected()));
        
        status_paid_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_paid_chk_box));
        status_partially_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_partially_chk_box));
        status_pending_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_pending_chk_box));
        status_overdue_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_overdue_chk_box));
    }
    
    private void handleStatusCheckboxSelection(CheckBox selectedCheckBox) {
        if (selectedCheckBox.isSelected()) {
            if (selectedCheckBox != status_paid_chk_box) status_paid_chk_box.setSelected(false);
            if (selectedCheckBox != status_partially_chk_box) status_partially_chk_box.setSelected(false);
            if (selectedCheckBox != status_pending_chk_box) status_pending_chk_box.setSelected(false);
            if (selectedCheckBox != status_overdue_chk_box) status_overdue_chk_box.setSelected(false);
            
            if (selectedCheckBox == status_partially_chk_box) {
            	total_amount_text_field.setDisable(false);
            } else {
            	total_amount_text_field.setDisable(true);
            }
        }
    }
	
    private String getSelectedStatus() {
        if (status_paid_chk_box.isSelected()) return "Paid";
        if (status_partially_chk_box.isSelected()) return "Partially Paid";
        if (status_pending_chk_box.isSelected()) return "Pending Payment";
        if (status_overdue_chk_box.isSelected()) return "Overdue Payment";
        return null;
    }

    private double parseDoubleOrZero(String text) {
        try { return Double.parseDouble(text); } catch (NumberFormatException e) { return 0; }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
	
    @FXML private Button add_btn, logout_btn;
    @FXML private Text balance_due_btn, create_invoice_btn, dashboard_btn;
    @FXML private DatePicker date_picker;
    @FXML private Text edit_invoice_btn, help_btn, my_profile_btn, payment_history_btn;
    @FXML private CheckBox monthly_advance_chk_box, monthly_deposit_chk_box, repeat_monthly_chk_box, status_overdue_chk_box, status_paid_chk_box, status_partially_chk_box, status_pending_chk_box;
    @FXML private TextField monthly_advance_text, monthly_deposit_text, note_text, property_text_field, total_amount_text_field, unit_text_field, rent_bill_text_field, electricity_text_field, water_text_field, wifi_text_field;
    
    private void insertBill(PreparedStatement statement, Connection connection, int userId, String property, String unit, String date, String billType, double amount, int depositMonths, int advanceMonths, String status, String note) throws SQLException {
        statement.setInt(1, userId); // Replace with actual user ID
        statement.setString(2, property);
        statement.setString(3, unit);
        statement.setString(4, date);
        statement.setString(5, billType);
        statement.setDouble(6, amount);
        statement.setInt(7, depositMonths);
        statement.setInt(8, advanceMonths);
        statement.setString(9, status);
        statement.setString(10, note);
        statement.executeUpdate();
    }
    
    @FXML
    void add_btn_clicked(ActionEvent event) {
    	String property = property_text_field.getText();
        String unit = unit_text_field.getText();
        String date = (date_picker.getValue() != null) ? date_picker.getValue().toString() : null;
        double rentBill = parseDoubleOrZero(rent_bill_text_field.getText());
        double electricityBill = parseDoubleOrZero(electricity_text_field.getText());
        double waterBill = parseDoubleOrZero(water_text_field.getText());
        double wifiBill = parseDoubleOrZero(wifi_text_field.getText());
        int depositMonths = monthly_deposit_chk_box.isSelected() ? Integer.parseInt(monthly_deposit_text.getText()) : 0;
        int advanceMonths = monthly_advance_chk_box.isSelected() ? Integer.parseInt(monthly_advance_text.getText()) : 0;
        String status = getSelectedStatus();
        String note = note_text.getText();

        if (property.isEmpty() || unit.isEmpty() || date == null || status == null) {
            showAlert("Error", "Please fill all required fields and select a status.");
            return;
        }

        // Calculate initial payment amount if status is "Partially Paid".
        double initialPayment = parseDoubleOrZero(total_amount_text_field.getText());

        String paymentQuery = "INSERT INTO payment_history (p_user_id, property, unit, date, bill_type, amount, deposit, advanced, status, note)"
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String balanceDueQuery = "INSERT INTO balance_due (b_user_id, property, unit, date, bill_type, amount, deposit, advanced, status, note)"
                               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement paymentStatement = connection.prepareStatement(paymentQuery);
             PreparedStatement balanceDueStatement = connection.prepareStatement(balanceDueQuery)) {
            
            // Insert into payment_history for each bill type
            if (rentBill > 0) {
                insertBill(paymentStatement, connection, 1, property, unit, date, "Rent", rentBill, depositMonths, advanceMonths, status, note);
            }
            if (electricityBill > 0) {
                insertBill(paymentStatement, connection, 1, property, unit, date, "Electricity", electricityBill, depositMonths, advanceMonths, status, note);
            }
            if (waterBill > 0) {
                insertBill(paymentStatement, connection, 1, property, unit, date, "Water", waterBill, depositMonths, advanceMonths, status, note);
            }
            if (wifiBill > 0) {
                insertBill(paymentStatement, connection, 1, property, unit, date, "Wi-Fi", wifiBill, depositMonths, advanceMonths, status, note);
            }
            
            // If partially paid, insert the remaining balance into balance_due
            if ("Partially Paid".equals(status)) {
                double remainingBalance = rentBill + electricityBill + waterBill + wifiBill - initialPayment;
                if (remainingBalance > 0) {
                    balanceDueStatement.setInt(1, 1); // Replace with actual user ID
                    balanceDueStatement.setString(2, property);
                    balanceDueStatement.setString(3, unit);
                    balanceDueStatement.setString(4, date);
                    balanceDueStatement.setString(5, "Rent, Electricity, Water, Wi-Fi");
                    balanceDueStatement.setDouble(6, remainingBalance);
                    balanceDueStatement.setInt(7, depositMonths);
                    balanceDueStatement.setInt(8, advanceMonths);
                    balanceDueStatement.setString(9, "Pending Payment");
                    balanceDueStatement.setString(10, note);
                    balanceDueStatement.executeUpdate();
                }
            }

            showAlert("Success", "Invoice added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }
    
    @FXML
    void balance_due_btn_clicked(MouseEvent event) {
    	try {
            // Load the Balance Due FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/BalanceDueView.fxml"));
            Parent BalanceDueRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(BalanceDueRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void create_invoice_btn(MouseEvent event) {
    	try {
            // Load the Create Invoice FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/CreateInvoiceView.fxml"));
            Parent CreateInvoiceRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(CreateInvoiceRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void dashboard_btn_clicked(MouseEvent event) {
    	try {
            // Load the Dashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/DashboardView.fxml"));
            Parent DashboardRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(DashboardRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void edit_invoice_btn_clicked(MouseEvent event) {
    	try {
            // Load the Edit Invoice FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/EditInvoiceView.fxml"));
            Parent EditInvoiceRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(EditInvoiceRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void help_btn_clicked(MouseEvent event) {
    	try {
            // Load the Help & FAQs FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/HelpsFAQsView.fxml"));
            Parent HelpsFAQsRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(HelpsFAQsRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void logout_btn_clicked(MouseEvent event) {
    	try {
            // Load the Login FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/LoginView.fxml"));
            Parent LoginRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(LoginRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void monthly_advance_chk_box_clicked(ActionEvent event) {

    }

    @FXML
    void monthly_deposit_chk_box_clicked(ActionEvent event) {

    }

    @FXML
    void my_profile_btn_clicked(MouseEvent event) {
    	try {
            // Load the My Profile FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/MyProfileView.fxml"));
            Parent MyProfileRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(MyProfileRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void payment_history_btn_clicked(MouseEvent event) {
    	try {
            // Load the Payment History FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/PaymentHistoryView.fxml"));
            Parent PaymentHistoryRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(PaymentHistoryRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void repeat_monthly_chk_box_clicked(ActionEvent event) {

    }

    @FXML
    void status_overdue_chk_box_clicked(ActionEvent event) {

    }

    @FXML
    void status_paid_chk_box_clicked(ActionEvent event) {

    }

    @FXML
    void status_partially_chk_box_clicked(ActionEvent event) {

    }

    @FXML
    void status_pending_chk_box_clicked(ActionEvent event) {

    }

}
