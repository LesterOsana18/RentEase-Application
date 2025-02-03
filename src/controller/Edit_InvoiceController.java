package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Edit_InvoiceController implements Initializable {

    // Variable to store the state of repeat_monthly_chk_box
    private static boolean isRepeatMonthlyChecked = false;
    private static final int CURRENT_USER_ID = 1; // Update this to get the actual current user ID

    @FXML private Text create_invoice_btn, dashboard_btn, edit_invoice_btn, help_btn, my_profile_btn, payment_history_btn;
    @FXML private ComboBox<String> bill_type_cmb_box, property_cmb_box, unit_cmb_box;
    @FXML private DatePicker date_picker;
    @FXML private Button delete_btn, edit_btn, logout_btn, clear_btn, find_btn;
    @FXML private TextField electricity_text_field, monthly_advance_text, monthly_deposit_text, note_text, rent_bill_text_field, total_amount_text_field, water_text_field, wifi_text_field;
    @FXML private CheckBox monthly_advance_chk_box, monthly_deposit_chk_box, repeat_monthly_chk_box, status_overdue_chk_box, status_paid_chk_box, status_partially_chk_box, status_pending_chk_box;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bill_type_cmb_box.setItems(FXCollections.observableArrayList("Rent", "Electricity", "Water", "Wi-Fi"));

        disableAllBillTypeFields();
        disableMiscellaneousAndStatus();

        monthly_deposit_chk_box.setOnAction(e -> monthly_deposit_text.setDisable(!monthly_deposit_chk_box.isSelected()));
        monthly_advance_chk_box.setOnAction(e -> monthly_advance_text.setDisable(!monthly_advance_chk_box.isSelected()));

        status_paid_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_paid_chk_box));
        status_partially_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_partially_chk_box));
        status_pending_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_pending_chk_box));
        status_overdue_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_overdue_chk_box));

        property_cmb_box.setOnMouseClicked(e -> loadData(property_cmb_box, "property", "SELECT DISTINCT property FROM payment_history WHERE p_user_id = ? UNION SELECT DISTINCT property FROM balance_due WHERE b_user_id = ?"));
        unit_cmb_box.setOnMouseClicked(e -> loadUnits());

        // Add listeners to bill amount text fields to enable/disable the partially paid checkbox
        rent_bill_text_field.setOnKeyTyped(this::bill_text_field_listener);
        electricity_text_field.setOnKeyTyped(this::bill_text_field_listener);
        water_text_field.setOnKeyTyped(this::bill_text_field_listener);
        wifi_text_field.setOnKeyTyped(this::bill_text_field_listener);

        // Store the state of repeat_monthly_chk_box when it is clicked
        repeat_monthly_chk_box.setOnAction(e -> isRepeatMonthlyChecked = repeat_monthly_chk_box.isSelected());
    }

    private void handleStatusCheckboxSelection(CheckBox selectedCheckBox) {
        if (selectedCheckBox.isSelected()) {
            if (selectedCheckBox != status_paid_chk_box) status_paid_chk_box.setSelected(false);
            if (selectedCheckBox != status_partially_chk_box) status_partially_chk_box.setSelected(false);
            if (selectedCheckBox != status_pending_chk_box) status_pending_chk_box.setSelected(false);
            if (selectedCheckBox != status_overdue_chk_box) status_overdue_chk_box.setSelected(false);

            if (selectedCheckBox == status_partially_chk_box) {
                total_amount_text_field.setDisable(false); // Enable the total amount text field
            } else {
                total_amount_text_field.setDisable(true); // Disable the total amount text field otherwise
            }
        } else {
            if (selectedCheckBox == status_partially_chk_box) {
                total_amount_text_field.setDisable(true); // Disable the total amount text field if partially paid checkbox is deselected
            }
        }
    }

    private void resetPartiallyPaidState() {
        status_partially_chk_box.setSelected(false);
        total_amount_text_field.setDisable(true);
        total_amount_text_field.clear();
    }

    private void updatePartiallyPaidCheckboxState() {
        double rentBill = parseDoubleOrZero(rent_bill_text_field.getText());
        double electricityBill = parseDoubleOrZero(electricity_text_field.getText());
        double waterBill = parseDoubleOrZero(water_text_field.getText());
        double wifiBill = parseDoubleOrZero(wifi_text_field.getText());

        // Check if exactly one of the text fields has a value greater than zero
        boolean hasSingleBill = (rentBill > 0 && electricityBill == 0 && waterBill == 0 && wifiBill == 0) ||
                                (rentBill == 0 && electricityBill > 0 && waterBill == 0 && wifiBill == 0) ||
                                (rentBill == 0 && electricityBill == 0 && waterBill > 0 && wifiBill == 0) ||
                                (rentBill == 0 && electricityBill == 0 && waterBill == 0 && wifiBill > 0);

        status_partially_chk_box.setDisable(!hasSingleBill);

        // Reset partially paid state if another bill is modified
        if (status_partially_chk_box.isSelected() && !hasSingleBill) {
            resetPartiallyPaidState();
        }
    }

    // Event handler for bill text fields
    @FXML
    private void bill_text_field_listener(KeyEvent event) {
        updatePartiallyPaidCheckboxState();
    }

    private String getSelectedStatus() {
        if (status_paid_chk_box.isSelected()) return "Paid";
        if (status_partially_chk_box.isSelected()) return "Partially Paid";
        if (status_pending_chk_box.isSelected()) return "Pending";
        if (status_overdue_chk_box.isSelected()) return "Overdue";
        return null;
    }

    private double parseDoubleOrZero(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadData(ComboBox<String> comboBox, String columnName, String query) {
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, CURRENT_USER_ID);
            statement.setInt(2, CURRENT_USER_ID);
            ResultSet resultSet = statement.executeQuery();
            ObservableList<String> items = FXCollections.observableArrayList();
            while (resultSet.next()) {
                items.add(resultSet.getString(columnName));
            }
            comboBox.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load data: " + e.getMessage());
        }
    }

    private void loadUnits() {
        String query = "SELECT DISTINCT unit FROM payment_history WHERE property = ? AND p_user_id = ? UNION SELECT DISTINCT unit FROM balance_due WHERE property = ? AND b_user_id = ?";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, property_cmb_box.getValue());
            statement.setInt(2, CURRENT_USER_ID);
            statement.setString(3, property_cmb_box.getValue());
            statement.setInt(4, CURRENT_USER_ID);
            ResultSet resultSet = statement.executeQuery();
            ObservableList<String> units = FXCollections.observableArrayList();
            while (resultSet.next()) {
                units.add(resultSet.getString("unit"));
            }
            unit_cmb_box.setItems(units);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load units: " + e.getMessage());
        }
    }

    @FXML
    void find_btn_clicked(ActionEvent event) {
        String property = property_cmb_box.getValue();
        String unit = unit_cmb_box.getValue();
        String billType = bill_type_cmb_box.getValue();
        String date = (date_picker.getValue() != null) ? date_picker.getValue().toString() : null;

        if (property == null || unit == null || billType == null || date == null) {
            showAlert("Error", "Please select a property, unit, bill type, and date to find.");
            return;
        }

        String query = "SELECT * FROM payment_history WHERE property = ? AND unit = ? AND bill_type = ? AND date = ? AND p_user_id = ? UNION SELECT * FROM balance_due WHERE property = ? AND unit = ? AND bill_type = ? AND date = ? AND b_user_id = ?";

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, property);
            statement.setString(2, unit);
            statement.setString(3, billType);
            statement.setString(4, date);
            statement.setInt(5, CURRENT_USER_ID);
            statement.setString(6, property);
            statement.setString(7, unit);
            statement.setString(8, billType);
            statement.setString(9, date);
            statement.setInt(10, CURRENT_USER_ID);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Enable all text fields and checkboxes
                enableAllFields();

                // Set the text fields with the retrieved data
                rent_bill_text_field.setText(resultSet.getString("rent"));
                electricity_text_field.setText(resultSet.getString("electricity"));
                water_text_field.setText(resultSet.getString("water"));
                wifi_text_field.setText(resultSet.getString("wifi"));
                note_text.setText(resultSet.getString("note"));

                // Set the checkboxes for deposit and advance
                int deposit = resultSet.getInt("deposit");
                int advance = resultSet.getInt("advanced");

                if (deposit > 0) {
                    monthly_deposit_chk_box.setSelected(true);
                    monthly_deposit_text.setText(String.valueOf(deposit));
                } else {
                    monthly_deposit_chk_box.setSelected(false);
                    monthly_deposit_text.clear();
                }

                if (advance > 0) {
                    monthly_advance_chk_box.setSelected(true);
                    monthly_advance_text.setText(String.valueOf(advance));
                } else {
                    monthly_advance_chk_box.setSelected(false);
                    monthly_advance_text.clear();
                }

                // Set the checkboxes for status
                String status = resultSet.getString("status");
                switch (status) {
                    case "Paid":
                        status_paid_chk_box.setSelected(true);
                        break;
                    case "Partially Paid":
                        status_partially_chk_box.setSelected(true);
                        break;
                    case "Pending":
                        status_pending_chk_box.setSelected(true);
                        break;
                    case "Overdue":
                        status_overdue_chk_box.setSelected(true);
                        break;
                }
            } else {
                showAlert("Error", "No record found matching the selected inputs.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void enableAllFields() {
        rent_bill_text_field.setDisable(false);
        electricity_text_field.setDisable(false);
        water_text_field.setDisable(false);
        wifi_text_field.setDisable(false);
        note_text.setDisable(false);
        monthly_deposit_chk_box.setDisable(false);
        monthly_advance_chk_box.setDisable(false);
        status_paid_chk_box.setDisable(false);
        status_partially_chk_box.setDisable(false);
        status_pending_chk_box.setDisable(false);
        status_overdue_chk_box.setDisable(false);
    }

    @FXML
    void edit_btn_clicked(ActionEvent event) {
        String property = property_cmb_box.getValue();
        String unit = unit_cmb_box.getValue();
        String billType = bill_type_cmb_box.getValue();
        String status = getSelectedStatus();
        String date = (date_picker.getValue() != null) ? date_picker.getValue().toString() : null;
        double amount = parseDoubleOrZero(getBillTypeAmount(billType));
        int depositMonths = monthly_deposit_chk_box.isSelected() ? Integer.parseInt(monthly_deposit_text.getText()) : 0;
        int advanceMonths = monthly_advance_chk_box.isSelected() ? Integer.parseInt(monthly_advance_text.getText()) : 0;
        String note = note_text.getText();

        if (property == null || unit == null || billType == null || status == null || date == null) {
            showAlert("Error", "Please fill all required fields and select a status.");
            return;
        }

        try (Connection connection = DBConfig.getConnection()) {
            connection.setAutoCommit(false);

            // Determine the current status of the record
            String currentStatusQuery = "SELECT status FROM balance_due WHERE property = ? AND unit = ? AND bill_type = ? AND b_user_id = ? UNION SELECT status FROM payment_history WHERE property = ? AND unit = ? AND bill_type = ? AND p_user_id = ?";
            try (PreparedStatement currentStatusStmt = connection.prepareStatement(currentStatusQuery)) {
                currentStatusStmt.setString(1, property);
                currentStatusStmt.setString(2, unit);
                currentStatusStmt.setString(3, billType);
                currentStatusStmt.setInt(4, CURRENT_USER_ID);
                currentStatusStmt.setString(5, property);
                currentStatusStmt.setString(6, unit);
                currentStatusStmt.setString(7, billType);
                currentStatusStmt.setInt(8, CURRENT_USER_ID);

                ResultSet rs = currentStatusStmt.executeQuery();
                if (rs.next()) {
                    String currentStatus = rs.getString("status");

                    // Check status change and move the record if necessary
                    if ("Pending".equals(currentStatus) && "Paid".equals(status)) {
                        moveRecord(connection, "balance_due", "payment_history", property, unit, billType, date, amount, depositMonths, advanceMonths, status, note);
                    } else if ("Paid".equals(currentStatus) && "Pending".equals(status)) {
                        moveRecord(connection, "payment_history", "balance_due", property, unit, billType, date, amount, depositMonths, advanceMonths, status, note);
                    } else {
                        // Update the record in the current table
                        updateRecord(connection, property, unit, billType, date, amount, depositMonths, advanceMonths, status, note, currentStatus);
                    }
                } else {
                    showAlert("Error", "Record not found.");
                    connection.rollback();
                    return;
                }
            }

            connection.commit();
            showAlert("Success", "Record updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void moveRecord(Connection connection, String fromTable, String toTable, String property, String unit, String billType, String date, double amount, int depositMonths, int advanceMonths, String status, String note) throws SQLException {
        String insertQuery = "INSERT INTO " + toTable + " (property, unit, bill_type, date, amount, deposit, advanced, status, note, " + (toTable.equals("payment_history") ? "p_user_id" : "b_user_id") + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setString(1, property);
            insertStmt.setString(2, unit);
            insertStmt.setString(3, billType);
            insertStmt.setString(4, date);
            insertStmt.setDouble(5, amount);
            insertStmt.setInt(6, depositMonths);
            insertStmt.setInt(7, advanceMonths);
            insertStmt.setString(8, status);
            insertStmt.setString(9, note);
            insertStmt.setInt(10, CURRENT_USER_ID);
            insertStmt.executeUpdate();
        }

        String deleteQuery = "DELETE FROM " + fromTable + " WHERE property = ? AND unit = ? AND bill_type = ? AND " + (fromTable.equals("payment_history") ? "p_user_id" : "b_user_id") + " = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
            deleteStmt.setString(1, property);
            deleteStmt.setString(2, unit);
            deleteStmt.setString(3, billType);
            deleteStmt.setInt(4, CURRENT_USER_ID);
            deleteStmt.executeUpdate();
        }
    }

    private void updateRecord(Connection connection, String property, String unit, String billType, String date, double amount, int depositMonths, int advanceMonths, String status, String note, String currentStatus) throws SQLException {
        String updateQuery;
        if ("Pending".equals(currentStatus)) {
            updateQuery = "UPDATE balance_due SET date = ?, amount = ?, deposit = ?, advanced = ?, status = ?, note = ? WHERE property = ? AND unit = ? AND bill_type = ? AND b_user_id = ?";
        } else {
            updateQuery = "UPDATE payment_history SET date = ?, amount = ?, deposit = ?, advanced = ?, status = ?, note = ? WHERE property = ? AND unit = ? AND bill_type = ? AND p_user_id = ?";
        }
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setString(1, date);
            updateStmt.setDouble(2, amount);
            updateStmt.setInt(3, depositMonths);
            updateStmt.setInt(4, advanceMonths);
            updateStmt.setString(5, status);
            updateStmt.setString(6, note);
            updateStmt.setString(7, property);
            updateStmt.setString(8, unit);
            updateStmt.setString(9, billType);
            updateStmt.setInt(10, CURRENT_USER_ID);
            updateStmt.executeUpdate();
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
            stage.setTitle("RentEase: Dashboard");
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
            stage.setTitle("RentEase: Edit Invoice");
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
            stage.setTitle("RentEase: Help & FAQs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void logout_btn_clicked(MouseEvent event) {
        // Implement logout logic here
    }

    @FXML
    void monthly_advance_chk_box_clicked(ActionEvent event) {
        // Handle action
    }

    @FXML
    void monthly_deposit_chk_box_clicked(ActionEvent event) {
        // Handle action
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
            stage.setTitle("RentEase: My Profile");
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
            stage.setTitle("RentEase: Payment History");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void repeat_monthly_chk_box_clicked(ActionEvent event) {
        // Store the state of repeat_monthly_chk_box
        isRepeatMonthlyChecked = repeat_monthly_chk_box.isSelected();
    }

    @FXML
    void status_overdue_chk_box_clicked(ActionEvent event) {
        // Implement logic if needed
    }

    @FXML
    void status_paid_chk_box_clicked(ActionEvent event) {
        // Implement logic if needed
    }

    @FXML
    void status_partially_chk_box_clicked(ActionEvent event) {
        // Implement logic if needed
    }

    @FXML
    void status_pending_chk_box_clicked(ActionEvent event) {
        // Implement logic if needed
    }

    private void disableMiscellaneousAndStatus() {
        monthly_deposit_text.setDisable(true);
        monthly_advance_text.setDisable(true);
        note_text.setDisable(true);
        total_amount_text_field.setDisable(true);
        status_paid_chk_box.setDisable(true);
        status_partially_chk_box.setDisable(true);
        status_pending_chk_box.setDisable(true);
        status_overdue_chk_box.setDisable(true);
        monthly_deposit_chk_box.setDisable(true);
        monthly_advance_chk_box.setDisable(true);
        repeat_monthly_chk_box.setDisable(true);
    }

    private void disableAllBillTypeFields() {
        rent_bill_text_field.setDisable(true);
        electricity_text_field.setDisable(true);
        water_text_field.setDisable(true);
        wifi_text_field.setDisable(true);
    }

    private String getBillTypeAmount(String billType) {
        switch (billType) {
            case "Rent":
                return rent_bill_text_field.getText();
            case "Electricity":
                return electricity_text_field.getText();
            case "Water":
                return water_text_field.getText();
            case "Wi-Fi":
                return wifi_text_field.getText();
            default:
                return "0";
        }
    }}