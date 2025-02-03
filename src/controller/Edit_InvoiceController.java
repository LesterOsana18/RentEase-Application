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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Edit_InvoiceController implements Initializable {

    @FXML private Text balance_due_btn, create_invoice_btn, dashboard_btn, edit_invoice_btn, help_btn, my_profile_btn, payment_history_btn;
    @FXML private ComboBox<String> bill_type_cmb_box, property_cmb_box, unit_cmb_box;
    @FXML private DatePicker date_picker;
    @FXML private Button delete_btn, edit_btn, logout_btn;
    @FXML private TextField electricity_text_field, monthly_advance_text, monthly_deposit_text, note_text, rent_bill_text_field, paid_amount_text_field, water_text_field, wifi_text_field;
    @FXML private CheckBox monthly_advance_chk_box, monthly_deposit_chk_box, repeat_monthly_chk_box, status_overdue_chk_box, status_paid_chk_box, status_partially_chk_box, status_pending_chk_box;

    private final String PAYMENT_HISTORY_QUERY = "SELECT DISTINCT property FROM payment_history WHERE p_user_id = ? UNION SELECT DISTINCT property FROM balance_due WHERE b_user_id = ?";
    private final String UNITS_QUERY = "SELECT DISTINCT unit FROM payment_history WHERE property = ? AND p_user_id = ? UNION SELECT DISTINCT unit FROM balance_due WHERE property = ? AND b_user_id = ?";
    private final String PROPERTY_DETAILS_QUERY = "SELECT * FROM payment_history WHERE property = ? AND p_user_id = ? UNION SELECT * FROM balance_due WHERE property = ? AND b_user_id = ?";
    private final String UNIT_DETAILS_QUERY = "SELECT * FROM payment_history WHERE property = ? AND unit = ? AND p_user_id = ? UNION SELECT * FROM balance_due WHERE property = ? AND unit = ? AND b_user_id = ?";
    private final String UPDATE_QUERY_TEMPLATE = "UPDATE %s SET date = ?, amount = ?, deposit = ?, advanced = ?, status = ?, note = ? WHERE property = ? AND unit = ? AND bill_type = ? AND %s = ?";
    private final String DELETE_QUERY_TEMPLATE = "DELETE FROM %s WHERE property = ? AND unit = ? AND bill_type = ? AND %s = ?";
    private final String INSERT_QUERY_TEMPLATE = "INSERT INTO %s (property, unit, bill_type, date, amount, deposit, advanced, status, note, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String PAYMENT_HISTORY_TABLE = "payment_history";
    private final String BALANCE_DUE_TABLE = "balance_due";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bill_type_cmb_box.setItems(FXCollections.observableArrayList("Rent", "Electricity", "Water", "Wi-Fi"));
        disableAllBillTypeFields();
        disableMiscellaneousAndStatus();
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        monthly_deposit_chk_box.setOnAction(e -> monthly_deposit_text.setDisable(!monthly_deposit_chk_box.isSelected()));
        monthly_advance_chk_box.setOnAction(e -> monthly_advance_text.setDisable(!monthly_advance_chk_box.isSelected()));
        status_paid_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_paid_chk_box));
        status_partially_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_partially_chk_box));
        status_pending_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_pending_chk_box));
        status_overdue_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_overdue_chk_box));
        property_cmb_box.setOnMouseClicked(e -> loadProperties());
        unit_cmb_box.setOnMouseClicked(e -> loadUnits());
        unit_cmb_box.setOnAction(e -> loadUnitDetails());
        bill_type_cmb_box.setOnAction(e -> {
            enableBillTypeField();
            loadBillTypeDetails();
        });
    }

    private void handleStatusCheckboxSelection(CheckBox selectedCheckBox) {
        if (selectedCheckBox.isSelected()) {
            if (selectedCheckBox != status_paid_chk_box) status_paid_chk_box.setSelected(false);
            if (selectedCheckBox != status_partially_chk_box) status_partially_chk_box.setSelected(false);
            if (selectedCheckBox != status_pending_chk_box) status_pending_chk_box.setSelected(false);
            if (selectedCheckBox != status_overdue_chk_box) status_overdue_chk_box.setSelected(false);
            paid_amount_text_field.setDisable(selectedCheckBox != status_partially_chk_box);
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

    private void loadProperties() {
        Stage stage = (Stage) property_cmb_box.getScene().getWindow();
        int userId = DBConfig.getCurrentUserId(stage);
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(PAYMENT_HISTORY_QUERY)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                ObservableList<String> properties = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    properties.add(resultSet.getString("property"));
                }
                property_cmb_box.setItems(properties);
            }
        } catch (SQLException e) {
            handleDatabaseError("Unable to load properties", e);
        }
    }

    private void loadUnits() {
        Stage stage = (Stage) unit_cmb_box.getScene().getWindow();
        int userId = DBConfig.getCurrentUserId(stage);
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UNITS_QUERY)) {
            statement.setString(1, property_cmb_box.getValue());
            statement.setInt(2, userId);
            statement.setString(3, property_cmb_box.getValue());
            statement.setInt(4, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                ObservableList<String> units = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    units.add(resultSet.getString("unit"));
                }
                unit_cmb_box.setItems(units);
            }
        } catch (SQLException e) {
            handleDatabaseError("Unable to load units", e);
        }
    }

    private void loadPropertyDetails() {
        Stage stage = (Stage) property_cmb_box.getScene().getWindow();
        int userId = DBConfig.getCurrentUserId(stage);
        String selectedProperty = property_cmb_box.getValue();
        String selectedUnit = unit_cmb_box.getValue();
        if (selectedProperty == null || selectedUnit == null) {
            return;
        }

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(PROPERTY_DETAILS_QUERY)) {
            statement.setString(1, selectedProperty);
            statement.setInt(2, userId);
            statement.setString(3, selectedProperty);
            statement.setInt(4, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    populatePropertyDetails(resultSet);
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Unable to load property details", e);
        }
    }

    private void populatePropertyDetails(ResultSet resultSet) throws SQLException {
        unit_cmb_box.setValue(resultSet.getString("unit"));
        date_picker.setValue(resultSet.getDate("date").toLocalDate());
        monthly_deposit_text.setText(String.valueOf(resultSet.getInt("deposit")));
        monthly_advance_text.setText(String.valueOf(resultSet.getInt("advanced")));
        note_text.setText(resultSet.getString("note"));

        String status = resultSet.getString("status");
        switch (status) {
            case "Paid":
                status_paid_chk_box.setSelected(true);
                break;
            case "Partially Paid":
                status_partially_chk_box.setSelected(true);
                paid_amount_text_field.setText(String.valueOf(resultSet.getDouble("amount")));
                paid_amount_text_field.setDisable(false);
                break;
            case "Pending Payment":
                status_pending_chk_box.setSelected(true);
                break;
            case "Overdue Payment":
                status_overdue_chk_box.setSelected(true);
                break;
        }

        if (resultSet.getInt("deposit") > 0) {
            monthly_deposit_chk_box.setSelected(true);
            monthly_deposit_text.setDisable(false);
        } else {
            monthly_deposit_chk_box.setSelected(false);
            monthly_deposit_text.setDisable(true);
        }

        if (resultSet.getInt("advanced") > 0) {
            monthly_advance_chk_box.setSelected(true);
            monthly_advance_text.setDisable(false);
        } else {
            monthly_advance_chk_box.setSelected(false);
            monthly_advance_text.setDisable(true);
        }

        enableMiscellaneousAndStatus();
    }

    private void loadUnitDetails() {
        disableAllBillTypeFields();
        clearDepositAdvanceAndNoteFields();
        enableStatusCheckboxes();

        Stage stage = (Stage) unit_cmb_box.getScene().getWindow();
        int userId = DBConfig.getCurrentUserId(stage);
        String selectedProperty = property_cmb_box.getValue();
        String selectedUnit = unit_cmb_box.getValue();
        if (selectedProperty == null || selectedUnit == null) {
            return;
        }

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UNIT_DETAILS_QUERY)) {
            statement.setString(1, selectedProperty);
            statement.setString(2, selectedUnit);
            statement.setInt(3, userId);
            statement.setString(4, selectedProperty);
            statement.setString(5, selectedUnit);
            statement.setInt(6, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    populateUnitDetails(resultSet);
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Unable to load unit details", e);
        }
    }

    private void populateUnitDetails(ResultSet resultSet) throws SQLException {
        String billType = resultSet.getString("bill_type");
        double amount = resultSet.getDouble("amount");

        switch (billType) {
            case "Rent":
                rent_bill_text_field.setText(String.valueOf(amount));
                break;
            case "Electricity":
                electricity_text_field.setText(String.valueOf(amount));
                break;
            case "Water":
                water_text_field.setText(String.valueOf(amount));
                break;
            case "Wi-Fi":
                wifi_text_field.setText(String.valueOf(amount));
                break;
        }

        String status = resultSet.getString("status");
        switch (status) {
            case "Paid":
                status_paid_chk_box.setSelected(true);
                break;
            case "Partially Paid":
                status_partially_chk_box.setSelected(true);
                paid_amount_text_field.setText(String.valueOf(amount));
                paid_amount_text_field.setDisable(false);
                break;
            case "Pending Payment":
                status_pending_chk_box.setSelected(true);
                break;
            case "Overdue Payment":
                status_overdue_chk_box.setSelected(true);
                break;
        }
    }
    
    private void clearDepositAdvanceAndNoteFields() {
        monthly_deposit_chk_box.setSelected(false);
        monthly_deposit_text.clear();
        monthly_deposit_text.setDisable(true);

        monthly_advance_chk_box.setSelected(false);
        monthly_advance_text.clear();
        monthly_advance_text.setDisable(true);

        note_text.clear();
        note_text.setDisable(true);
    }

    private void enableStatusCheckboxes() {
        status_paid_chk_box.setDisable(false);
        status_partially_chk_box.setDisable(false);
        status_pending_chk_box.setDisable(false);
        status_overdue_chk_box.setDisable(false);
    }
    
    private void loadBillTypeDetails() {
        Stage stage = (Stage) bill_type_cmb_box.getScene().getWindow();
        int userId = DBConfig.getCurrentUserId(stage);
        String selectedProperty = property_cmb_box.getValue();
        String selectedUnit = unit_cmb_box.getValue();
        String selectedBillType = bill_type_cmb_box.getValue();
        if (selectedProperty == null || selectedUnit == null || selectedBillType == null) {
            return;
        }

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UNIT_DETAILS_QUERY)) {
            statement.setString(1, selectedProperty);
            statement.setString(2, selectedUnit);
            statement.setInt(3, userId);
            statement.setString(4, selectedProperty);
            statement.setString(5, selectedUnit);
            statement.setInt(6, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String billType = resultSet.getString("bill_type");
                    if (billType.equals(selectedBillType)) {
                        populateBillTypeDetails(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Unable to load bill type details", e);
        }
    }

    private void populateBillTypeDetails(ResultSet resultSet) throws SQLException {
        int deposit = resultSet.getInt("deposit");
        int advanced = resultSet.getInt("advanced");
        String note = resultSet.getString("note");
        java.sql.Date date = resultSet.getDate("date");

        if (deposit > 0) {
            monthly_deposit_chk_box.setSelected(true);
            monthly_deposit_text.setText(String.valueOf(deposit));
        } else {
            monthly_deposit_chk_box.setSelected(false);
            monthly_deposit_text.clear();
        }
        monthly_deposit_text.setDisable(false);
        monthly_deposit_chk_box.setDisable(false);

        if (advanced > 0) {
            monthly_advance_chk_box.setSelected(true);
            monthly_advance_text.setText(String.valueOf(advanced));
        } else {
            monthly_advance_chk_box.setSelected(false);
            monthly_advance_text.clear();
        }
        monthly_advance_text.setDisable(false);
        monthly_advance_chk_box.setDisable(false);

        note_text.setText(note);
        note_text.setDisable(false);

        if (date != null) {
            date_picker.setValue(date.toLocalDate());
        }
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
    }

    private void enableBillTypeField() {
        disableAllBillTypeFields();
        String selectedBillType = bill_type_cmb_box.getValue();
        if (selectedBillType == null) {
            return;
        }
        switch (selectedBillType) {
            case "Rent":
                rent_bill_text_field.setDisable(false);
                break;
            case "Electricity":
                electricity_text_field.setDisable(false);
                break;
            case "Water":
                water_text_field.setDisable(false);
                break;
            case "Wi-Fi":
                wifi_text_field.setDisable(false);
                break;
        }
    }

    private void disableAllBillTypeFields() {
        rent_bill_text_field.setDisable(true);
        electricity_text_field.setDisable(true);
        water_text_field.setDisable(true);
        wifi_text_field.setDisable(true);
    }

    private void clearFields() {
        property_cmb_box.getSelectionModel().clearSelection();
        unit_cmb_box.getSelectionModel().clearSelection();
        bill_type_cmb_box.getSelectionModel().clearSelection();
        date_picker.setValue(null);
        rent_bill_text_field.clear();
        electricity_text_field.clear();
        water_text_field.clear();
        wifi_text_field.clear();
        monthly_deposit_text.clear();
        monthly_advance_text.clear();
        note_text.clear();
        status_paid_chk_box.setSelected(false);
        status_partially_chk_box.setSelected(false);
        status_pending_chk_box.setSelected(false);
        status_overdue_chk_box.setSelected(false);
        paid_amount_text_field.clear();
        disableAllBillTypeFields();
    }

    private void disableMiscellaneousAndStatus() {
        monthly_deposit_text.setDisable(true);
        monthly_advance_text.setDisable(true);
        note_text.setDisable(true);
        paid_amount_text_field.setDisable(true);
        status_paid_chk_box.setDisable(true);
        status_partially_chk_box.setDisable(true);
        status_pending_chk_box.setDisable(true);
        status_overdue_chk_box.setDisable(true);
        monthly_deposit_chk_box.setDisable(true);
        monthly_advance_chk_box.setDisable(true);
        repeat_monthly_chk_box.setDisable(true);
    }

    private void enableMiscellaneousAndStatus() {
        monthly_deposit_text.setDisable(false);
        monthly_advance_text.setDisable(false);
        note_text.setDisable(false);
        paid_amount_text_field.setDisable(false);
        status_paid_chk_box.setDisable(false);
        status_partially_chk_box.setDisable(false);
        status_pending_chk_box.setDisable(false);
        status_overdue_chk_box.setDisable(false);
        monthly_deposit_chk_box.setDisable(false);
        monthly_advance_chk_box.setDisable(false);
        repeat_monthly_chk_box.setDisable(false);
    }

    @FXML
    void balance_due_btn_clicked(MouseEvent event) {
        loadFXMLView("/controller/BalanceDueView.fxml", "RentEase: Balance Due", event);
    }

    @FXML
    void bill_type_cmb_box_selected(ActionEvent event) {
        enableBillTypeField();
    }

    @FXML
    void create_invoice_btn(MouseEvent event) {
        loadFXMLView("/controller/CreateInvoiceView.fxml", "RentEase: Create Invoice", event);
    }

    @FXML
    void dashboard_btn_clicked(MouseEvent event) {
        loadFXMLView("/controller/DashboardView.fxml", "RentEase: Dashboard", event);
    }

    @FXML
    void delete_btn_clicked(ActionEvent event) {
        String property = property_cmb_box.getValue();
        String unit = unit_cmb_box.getValue();
        String billType = bill_type_cmb_box.getValue();

        if (property == null || unit == null || billType == null) {
            showAlert("Error", "Please select a property, unit, and bill type to delete.");
            return;
        }

        Stage stage = (Stage) delete_btn.getScene().getWindow();
        int userId = DBConfig.getCurrentUserId(stage);

        boolean deletedFromPaymentHistory = deleteRecordFromTable(PAYMENT_HISTORY_TABLE, property, unit, billType, userId);
        boolean deletedFromBalanceDue = deleteRecordFromTable(BALANCE_DUE_TABLE, property, unit, billType, userId);

        if (deletedFromPaymentHistory || deletedFromBalanceDue) {
            showAlert("Success", "Record deleted successfully.");
            clearFields();
        } else {
            showAlert("Error", "Record not found or could not be deleted.");
        }
    }

    private boolean deleteRecordFromTable(String tableName, String property, String unit, String billType, int userId) {
        String userIdColumn = tableName.equals("balance_due") ? "b_user_id" : "p_user_id";
        String deleteQuery = String.format(DELETE_QUERY_TEMPLATE, tableName, userIdColumn);
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setString(1, property);
            statement.setString(2, unit);
            statement.setString(3, billType);
            statement.setInt(4, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            handleDatabaseError("Database error: Unable to delete record from " + tableName, e);
            return false;
        }
    }

    @FXML
    void edit_btn_clicked(ActionEvent event) {
        String property = property_cmb_box.getValue();
        String unit = unit_cmb_box.getValue();
        String billType = bill_type_cmb_box.getValue();
        String status = getSelectedStatus();
        String date = (date_picker.getValue() != null) ? date_picker.getValue().toString() : null;
        double amount = parseDoubleOrZero(getBillTypeAmount(billType));
        int depositMonths = monthly_deposit_chk_box.isSelected() ? Integer.parseInt(monthly_deposit_text.getText().trim()) : 0;
        int advanceMonths = monthly_advance_chk_box.isSelected() ? Integer.parseInt(monthly_advance_text.getText().trim()) : 0;
        String note = note_text.getText().trim();

        if (property == null || unit == null || billType == null || status == null || date == null) {
            showAlert("Error", "Please fill all required fields and select a status.");
            return;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        int userId = DBConfig.getCurrentUserId(stage);

        // Calculate the new amount if the monthly deposit or advance check boxes are checked and their values are greater than or equal to 1
        if (monthly_deposit_chk_box.isSelected() && depositMonths >= 1 || monthly_advance_chk_box.isSelected() && advanceMonths >= 1) {
            amount = amount + (amount * (depositMonths + advanceMonths));
        }

        try {
            boolean recordExistsInPaymentHistory = checkRecordExists(PAYMENT_HISTORY_TABLE, property, unit, billType, userId);
            boolean recordExistsInBalanceDue = checkRecordExists(BALANCE_DUE_TABLE, property, unit, billType, userId);

            if (!recordExistsInPaymentHistory && !recordExistsInBalanceDue) {
                showAlert("Error", "Record not found.");
                return;
            }

            boolean success = false;

            // Handle status change logic
            if (status.equals("Paid")) {
                // If the record is in balance_due, move it to payment_history
                if (recordExistsInBalanceDue) {
                    success = DBConfig.deleteRecord(BALANCE_DUE_TABLE, property, unit, billType) &&
                              DBConfig.insertRecord(PAYMENT_HISTORY_TABLE, property, unit, billType, date, amount, depositMonths, advanceMonths, status, note, userId);
                } else {
                    success = DBConfig.updateRecord(PAYMENT_HISTORY_TABLE, property, unit, billType, date, amount, depositMonths, advanceMonths, status, note);
                }
            } else if (status.equals("Pending Payment") || status.equals("Overdue")) {
                // If the record is in payment_history, move it to balance_due
                if (recordExistsInPaymentHistory) {
                    success = DBConfig.deleteRecord(PAYMENT_HISTORY_TABLE, property, unit, billType) &&
                              DBConfig.insertRecord(BALANCE_DUE_TABLE, property, unit, billType, date, amount, depositMonths, advanceMonths, status, note, userId);
                } else {
                    success = DBConfig.updateRecord(BALANCE_DUE_TABLE, property, unit, billType, date, amount, depositMonths, advanceMonths, status, note);
                }
            } else if (status.equals("Partially Paid")) {
                // Handle partially paid logic
                double paidAmount = parseDoubleOrZero(paid_amount_text_field.getText());
                if (paidAmount > 0) {
                    success = DBConfig.insertRecord(PAYMENT_HISTORY_TABLE, property, unit, billType, date, paidAmount, depositMonths, advanceMonths, "Partially Paid", note, userId) &&
                              DBConfig.insertRecord(BALANCE_DUE_TABLE, property, unit, billType, date, amount - paidAmount, depositMonths, advanceMonths, "Pending", note, userId);
                } else {
                    showAlert("Error", "Please enter a valid paid amount for partial payment.");
                    return;
                }
            }

            if (success) {
                showAlert("Success", "Invoice processed successfully.");
            } else {
                showAlert("Error", "Failed to process invoice.");
            }
        } catch (SQLException e) {
            handleDatabaseError("Database error: Unable to handle invoice update", e);
        }
    }

    private boolean checkRecordExists(String tableName, String property, String unit, String billType, int userId) throws SQLException {
        String userIdColumn = tableName.equals("balance_due") ? "b_user_id" : "p_user_id";
        String selectQuery = String.format("SELECT 1 FROM %s WHERE property = ? AND unit = ? AND bill_type = ? AND %s = ?", tableName, userIdColumn);
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setString(1, property);
            selectStatement.setString(2, unit);
            selectStatement.setString(3, billType);
            selectStatement.setInt(4, userId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void updateRecord(String tableName, String property, String unit, String billType, String date, double amount, int depositMonths, int advanceMonths, String status, String note, int userId) {
        String userIdColumn = tableName.equals("balance_due") ? "b_user_id" : "p_user_id";
        String updateQuery = String.format(UPDATE_QUERY_TEMPLATE, tableName, userIdColumn);
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, date);
            updateStatement.setDouble(2, amount);
            updateStatement.setInt(3, depositMonths);
            updateStatement.setInt(4, advanceMonths);
            updateStatement.setString(5, status);
            updateStatement.setString(6, note);
            updateStatement.setString(7, property);
            updateStatement.setString(8, unit);
            updateStatement.setString(9, billType);
            updateStatement.setInt(10, userId);

            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", String.format("Record updated successfully in %s.", tableName));
            } else {
                showAlert("Error", String.format("Record not found or could not be updated in %s.", tableName));
            }
        } catch (SQLException e) {
            handleDatabaseError("Database error: Unable to update record", e);
        }
    }

    private void insertRecord(String tableName, String property, String unit, String billType, String date, double amount, int depositMonths, int advanceMonths, String status, String note, int userId) {
        String userIdColumn = tableName.equals("balance_due") ? "b_user_id" : "p_user_id";
        String insertQuery = String.format(INSERT_QUERY_TEMPLATE, tableName, userIdColumn);
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setString(1, property);
            insertStatement.setString(2, unit);
            insertStatement.setString(3, billType);
            insertStatement.setString(4, date);
            insertStatement.setDouble(5, amount);
            insertStatement.setInt(6, depositMonths);
            insertStatement.setInt(7, advanceMonths);
            insertStatement.setString(8, status);
            insertStatement.setString(9, note);
            insertStatement.setInt(10, userId);

            insertStatement.executeUpdate();
            showAlert("Success", String.format("Record inserted successfully in %s.", tableName));
        } catch (SQLException e) {
            handleDatabaseError("Database error: Unable to insert record into " + tableName, e);
        }
    }

    @FXML
    void edit_invoice_btn_clicked(MouseEvent event) {
        loadFXMLView("/controller/EditInvoiceView.fxml", "RentEase: Edit Invoice", event);
    }

    @FXML
    void help_btn_clicked(MouseEvent event) {
        loadFXMLView("/controller/HelpsFAQsView.fxml", "RentEase: Help & FAQs", event);
    }

    @FXML
    void monthly_deposit_chk_box_clicked(ActionEvent event) {
        monthly_deposit_text.setDisable(!monthly_deposit_chk_box.isSelected());
    }

    @FXML
    void monthly_advance_chk_box_clicked(ActionEvent event) {
        monthly_advance_text.setDisable(!monthly_advance_chk_box.isSelected());
    }

    @FXML
    void status_overdue_chk_box_clicked(ActionEvent event) {
        handleStatusCheckboxSelection(status_overdue_chk_box);
    }

    @FXML
    void status_paid_chk_box_clicked(ActionEvent event) {
        handleStatusCheckboxSelection(status_paid_chk_box);
    }

    @FXML
    void status_partially_chk_box_clicked(ActionEvent event) {
        handleStatusCheckboxSelection(status_partially_chk_box);
    }

    @FXML
    void status_pending_chk_box_clicked(ActionEvent event) {
        handleStatusCheckboxSelection(status_pending_chk_box);
    }

    @FXML
    void my_profile_btn_clicked(MouseEvent event) {
        loadFXMLView("/controller/MyProfileView.fxml", "RentEase: My Profile", event);
    }

    @FXML
    void payment_history_btn_clicked(MouseEvent event) {
        loadFXMLView("/controller/PaymentHistoryView.fxml", "RentEase: Payment History", event);
    }

    @FXML
    void repeat_monthly_chk_box_clicked(ActionEvent event) {
        // Implement behavior for repeat_monthly_chk_box if needed
    }

    @FXML
    void logout_btn_clicked(MouseEvent event) {
        loadFXMLView("/controller/LoginView.fxml", "RentEase: Login", event);
    }

    // Utility method to load FXML views
    private void loadFXMLView(String fxmlPath, String title, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load view: " + e.getMessage());
        }
    }

    // Utility method to load FXML views
    private void loadFXMLView(String fxmlPath, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load view: " + e.getMessage());
        }
    }

    // Utility method to handle database errors
    private void handleDatabaseError(String message, SQLException e) {
        e.printStackTrace();
        showAlert("Error", message + ": " + e.getMessage());
    }
    }