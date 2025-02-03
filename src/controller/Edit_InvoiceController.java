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

        property_cmb_box.setOnMouseClicked(e -> loadProperties());
        unit_cmb_box.setOnMouseClicked(e -> loadUnits());

        property_cmb_box.setOnAction(e -> loadPropertyDetails());
        unit_cmb_box.setOnAction(e -> loadUnitDetails());
        bill_type_cmb_box.setOnAction(e -> enableBillTypeField());
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
        if ( status_pending_chk_box.isSelected()) return "Pending Payment";
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
        String query = "SELECT DISTINCT property FROM payment_history";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            ObservableList<String> properties = FXCollections.observableArrayList();
            while (resultSet.next()) {
                properties.add(resultSet.getString("property"));
            }
            property_cmb_box.setItems(properties);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load properties: " + e.getMessage());
        }
    }

    private void loadUnits() {
        String query = "SELECT DISTINCT unit FROM payment_history WHERE property = ?";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, property_cmb_box.getValue());
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
    
    private void loadPropertyDetails() {
        String query = "SELECT * FROM payment_history WHERE property = ?";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, property_cmb_box.getValue());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
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
                        total_amount_text_field.setText(String.valueOf(resultSet.getDouble("amount")));
                        total_amount_text_field.setDisable(false);
                        break;
                    case "Pending Payment":
                        status_pending_chk_box.setSelected(true);
                        break;
                    case "Overdue Payment":
                        status_overdue_chk_box.setSelected(true);
                        break;
                }

                enableMiscellaneousAndStatus();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load property details: " + e.getMessage());
        }
    }
    
    private void loadUnitDetails() {
        // Enable the Miscellaneous and Status components
        enableMiscellaneousAndStatus();

        // Fetch the bill type details for the selected property and unit
        String query = "SELECT * FROM payment_history WHERE property = ? AND unit = ?";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, property_cmb_box.getValue());
            statement.setString(2, unit_cmb_box.getValue());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load unit details: " + e.getMessage());
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
        total_amount_text_field.clear();
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

    private void enableMiscellaneousAndStatus() {
        monthly_deposit_text.setDisable(false);
        monthly_advance_text.setDisable(false);
        note_text.setDisable(false);
        total_amount_text_field.setDisable(false);
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
    	try {
            // Load the Balance Due FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/BalanceDueView.fxml"));
            Parent BalanceDueRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(BalanceDueRoot);
            stage.setScene(scene);
            stage.setTitle("RentEase: Balance Due");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void bill_type_cmb_box_selected(ActionEvent event) {
        enableBillTypeField();
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
            stage.setTitle("RentEase: Create Invoice");
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
            stage.setTitle("RentEase: Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        String deleteQuery = "DELETE FROM payment_history WHERE property = ? AND unit = ? AND bill_type = ?";

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            statement.setString(1, property);
            statement.setString(2, unit);
            statement.setString(3, billType);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Record deleted successfully.");
                clearFields();
            } else {
                showAlert("Error", "Record not found or could not be deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
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
        int depositMonths = monthly_deposit_chk_box.isSelected() ? Integer.parseInt(monthly_deposit_text.getText()) : 0;
        int advanceMonths = monthly_advance_chk_box.isSelected() ? Integer.parseInt(monthly_advance_text.getText()) : 0;
        String note = note_text.getText();

        if (property == null || unit == null || billType == null || status == null || date == null) {
            showAlert("Error", "Please fill all required fields and select a status.");
            return;
        }

        String updateQuery = "UPDATE payment_history SET date = ?, amount = ?, deposit = ?, advanced = ?, status = ?, note = ? WHERE property = ? AND unit = ? AND bill_type = ?";

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setString(1, date);
            statement.setDouble(2, amount);
            statement.setInt(3, depositMonths);
            statement.setInt(4, advanceMonths);
            statement.setString(5, status);
            statement.setString(6, note);
            statement.setString(7, property);
            statement.setString(8, unit);
            statement.setString(9, billType);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Record updated successfully.");
            } else {
                showAlert("Error", "Record not found or could not be updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
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
