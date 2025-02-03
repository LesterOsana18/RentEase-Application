package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Edit_InvoiceController {

    @FXML
    private Text balance_due_btn;

    @FXML
    private ComboBox<String> bill_type_cmb_box;

    @FXML
    private Text create_invoice_btn;

    @FXML
    private Text dashboard_btn;

    @FXML
    private DatePicker date_picker;

    @FXML
    private Button delete_btn;

    @FXML
    private Button edit_btn;

    @FXML
    private Text edit_invoice_btn;

    @FXML
    private TextField electricity_text_field;

    @FXML
    private Button find_btn;

    @FXML
    private Text help_btn;

    @FXML
    private Button logout_btn;

    @FXML
    private CheckBox monthly_advance_chk_box;

    @FXML
    private TextField monthly_advance_text;

    @FXML
    private CheckBox monthly_deposit_chk_box;

    @FXML
    private TextField monthly_deposit_text;

    @FXML
    private Text my_profile_btn;

    @FXML
    private TextField note_text;

    @FXML
    private TextField paid_amount;

    @FXML
    private Text payment_history_btn;

    @FXML
    private ComboBox<String> property_cmb_box;

    @FXML
    private TextField rent_bill_text_field;

    @FXML
    private CheckBox repeat_monthly_chk_box;

    @FXML
    private CheckBox status_overdue_chk_box;

    @FXML
    private CheckBox status_paid_chk_box;

    @FXML
    private CheckBox status_partially_chk_box;

    @FXML
    private CheckBox status_pending_chk_box;

    @FXML
    private ComboBox<String> unit_cmb_box;

    @FXML
    private TextField water_text_field;

    @FXML
    private TextField wifi_text_field;

    @FXML
    public void initialize() {
        // Initialize the bill_type_cmb_box with predefined values
        bill_type_cmb_box.setItems(FXCollections.observableArrayList("Rent", "Electricity", "Water", "Wi-Fi"));

        // Load data for property_cmb_box and unit_cmb_box from the database
        property_cmb_box.setItems(DBConfig.getProperties());
        unit_cmb_box.setItems(DBConfig.getUnits());
    }

    @FXML
    void balance_due_btn_clicked(MouseEvent event) {
        // Handle balance due button click
    }

    @FXML
    void bill_type_cmb_box_selected(ActionEvent event) {
        // Handle bill type combo box selection
    }

    @FXML
    void create_invoice_btn(MouseEvent event) {
        // Handle create invoice button click
    }

    @FXML
    void dashboard_btn_clicked(MouseEvent event) {
        // Handle dashboard button click
    }

    @FXML
    void delete_btn_clicked(ActionEvent event) {
        // Handle delete button click
    }

    @FXML
    void edit_btn_clicked(ActionEvent event) {
        // Handle edit button click
    }

    @FXML
    void edit_invoice_btn_clicked(MouseEvent event) {
        // Handle edit invoice button click
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

        ResultSet resultSet = DBConfig.getInvoiceData(property, unit, billType, date);

        try {
            if (resultSet != null && resultSet.next()) {
                enableFields(billType);

                String amount = resultSet.getString("amount");
                switch (billType) {
                    case "Rent":
                        rent_bill_text_field.setText(amount);
                        break;
                    case "Electricity":
                        electricity_text_field.setText(amount);
                        break;
                    case "Water":
                        water_text_field.setText(amount);
                        break;
                    case "Wi-Fi":
                        wifi_text_field.setText(amount);
                        break;
                }

                int deposit = resultSet.getInt("deposit");
                if (deposit > 0) {
                    monthly_deposit_chk_box.setSelected(true);
                    monthly_deposit_text.setText(String.valueOf(deposit));
                    monthly_deposit_text.setDisable(deposit == 1 ? false : true);
                } else {
                    monthly_deposit_chk_box.setSelected(false);
                    monthly_deposit_text.clear();
                    monthly_deposit_text.setDisable(true);
                }

                int advance = resultSet.getInt("advanced");
                if (advance > 0) {
                    monthly_advance_chk_box.setSelected(true);
                    monthly_advance_text.setText(String.valueOf(advance));
                    monthly_advance_text.setDisable(advance == 1 ? false : true);
                } else {
                    monthly_advance_chk_box.setSelected(false);
                    monthly_advance_text.clear();
                    monthly_advance_text.setDisable(true);
                }

                note_text.setText(resultSet.getString("note"));
            } else {
                showAlert("Error", "No record found matching the selected inputs.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void enableFields(String billType) {
        // Disable all fields first
        rent_bill_text_field.setDisable(true);
        electricity_text_field.setDisable(true);
        water_text_field.setDisable(true);
        wifi_text_field.setDisable(true);

        // Enable only the field corresponding to the selected bill type
        switch (billType) {
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

        monthly_deposit_chk_box.setDisable(false);
        monthly_advance_chk_box.setDisable(false);
        repeat_monthly_chk_box.setDisable(false);
        status_paid_chk_box.setDisable(false);
        status_pending_chk_box.setDisable(false);
        status_overdue_chk_box.setDisable(false);
        note_text.setDisable(false);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void help_btn_clicked(MouseEvent event) {
        // Handle help button click
    }

    @FXML
    void logout_btn_clicked(MouseEvent event) {
        // Handle logout button click
    }

    @FXML
    void monthly_advance_chk_box_clicked(ActionEvent event) {
        monthly_advance_text.setDisable(!monthly_advance_chk_box.isSelected());
    }

    @FXML
    void monthly_deposit_chk_box_clicked(ActionEvent event) {
        monthly_deposit_text.setDisable(!monthly_deposit_chk_box.isSelected());
    }

    @FXML
    void my_profile_btn_clicked(MouseEvent event) {
        // Handle my profile button click
    }

    @FXML
    void payment_history_btn_clicked(MouseEvent event) {
        // Handle payment history button click
    }

    @FXML
    void repeat_monthly_chk_box_clicked(ActionEvent event) {
        // Handle repeat monthly checkbox click
    }

    @FXML
    void status_overdue_chk_box_clicked(ActionEvent event) {
        // Handle status overdue checkbox click
    }

    @FXML
    void status_paid_chk_box_clicked(ActionEvent event) {
        // Handle status paid checkbox click
    }

    @FXML
    void status_partially_chk_box_clicked(ActionEvent event) {
        // Handle status partially checkbox click
    }

    @FXML
    void status_pending_chk_box_clicked(ActionEvent event) {
        // Handle status pending checkbox click
    }

}