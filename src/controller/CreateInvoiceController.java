package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
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
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CreateInvoiceController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> months = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        monthly_deposit_text.setDisable(true);
        monthly_advance_text.setDisable(true);
        paid_amount_text_field.setDisable(true); 

        monthly_deposit_chk_box.setOnAction(e -> monthly_deposit_text.setDisable(!monthly_deposit_chk_box.isSelected()));
        monthly_advance_chk_box.setOnAction(e -> monthly_advance_text.setDisable(!monthly_advance_chk_box.isSelected()));

        status_paid_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_paid_chk_box));
        status_partially_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_partially_chk_box));
        status_pending_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_pending_chk_box));
        status_overdue_chk_box.setOnAction(e -> handleStatusCheckboxSelection(status_overdue_chk_box));

        rent_bill_text_field.setOnKeyReleased(this::rent_bill_text_field_listener);
        electricity_text_field.setOnKeyReleased(this::electricity_text_field_listener);
        water_text_field.setOnKeyReleased(this::water_text_field_listener);
        wifi_text_field.setOnKeyReleased(this::wifi_text_field_listener);

    }

    private void handleStatusCheckboxSelection(CheckBox selectedCheckBox) {
        if (selectedCheckBox.isSelected()) {
            if (selectedCheckBox != status_paid_chk_box) status_paid_chk_box.setSelected(false);
            if (selectedCheckBox != status_partially_chk_box) status_partially_chk_box.setSelected(false);
            if (selectedCheckBox != status_pending_chk_box) status_pending_chk_box.setSelected(false);
            if (selectedCheckBox != status_overdue_chk_box) status_overdue_chk_box.setSelected(false);

            if (selectedCheckBox == status_partially_chk_box) {
                paid_amount_text_field.setDisable(false);
            } else {
                paid_amount_text_field.setDisable(true); 
            }
        } else {
            if (selectedCheckBox == status_partially_chk_box) {
                paid_amount_text_field.setDisable(true); 
            }
        }
    }

    private void resetPartiallyPaidState() {
        status_partially_chk_box.setSelected(false);
        paid_amount_text_field.setDisable(true);
        paid_amount_text_field.clear();
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

    @FXML
    private void rent_bill_text_field_listener(KeyEvent event) {
        updatePartiallyPaidCheckboxState();
    }

    @FXML
    private void electricity_text_field_listener(KeyEvent event) {
        updatePartiallyPaidCheckboxState();
    }

    @FXML
    private void water_text_field_listener(KeyEvent event) {
        updatePartiallyPaidCheckboxState();
    }

    @FXML
    private void wifi_text_field_listener(KeyEvent event) {
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

    @FXML private Button add_btn, logout_btn, clear_btn;
    @FXML private Text balance_due_btn, create_invoice_btn, dashboard_btn;
    @FXML private DatePicker date_picker;
    @FXML private Text edit_invoice_btn, help_btn, my_profile_btn, payment_history_btn;
    @FXML private CheckBox monthly_advance_chk_box, monthly_deposit_chk_box, status_overdue_chk_box, status_paid_chk_box, status_partially_chk_box, status_pending_chk_box;
    @FXML private TextField monthly_advance_text, monthly_deposit_text, note_text, property_text_field, paid_amount_text_field, unit_text_field, rent_bill_text_field, electricity_text_field, water_text_field, wifi_text_field;

    private void insertBill(PreparedStatement statement, int userId, String property, String unit, String date, String billType, double amount, int depositMonths, int advanceMonths, String status, String note) throws SQLException {
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

        // Calculate deposit and advance amounts for Rent
        double depositAmount = rentBill * depositMonths;
        double advanceAmount = rentBill * advanceMonths;
        double adjustedRentBill = rentBill + depositAmount + advanceAmount;

        // Calculate initial payment amount if status is "Partially Paid".
        double initialPayment = parseDoubleOrZero(paid_amount_text_field.getText());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        int userId = DBConfig.getCurrentUserId(stage);

        try (Connection connection = DBConfig.getConnection()) {
            // Insert into the appropriate table based on status
            if ("Paid".equals(status)) {
                // Insert into payment_history for each bill type
                if (rentBill > 0) {
                    DBConfig.insertRecord("payment_history", property, unit, "Rent", date, adjustedRentBill, depositMonths, advanceMonths, status, note, userId);
                }
                if (electricityBill > 0) {
                    DBConfig.insertRecord("payment_history", property, unit, "Electricity", date, electricityBill, 0, 0, status, note, userId);
                }
                if (waterBill > 0) {
                    DBConfig.insertRecord("payment_history", property, unit, "Water", date, waterBill, 0, 0, status, note, userId);
                }
                if (wifiBill > 0) {
                    DBConfig.insertRecord("payment_history", property, unit, "Wi-Fi", date, wifiBill, 0, 0, status, note, userId);
                }
            } else if ("Partially Paid".equals(status)) {
                // Ensure only one bill type is provided for partially paid status
                if ((rentBill > 0 && electricityBill == 0 && waterBill == 0 && wifiBill == 0) ||
                    (rentBill == 0 && electricityBill > 0 && waterBill == 0 && wifiBill == 0) ||
                    (rentBill == 0 && electricityBill == 0 && waterBill > 0 && wifiBill == 0) ||
                    (rentBill == 0 && electricityBill == 0 && waterBill == 0 && wifiBill > 0)) {

                    // Insert into payment_history for initial payment
                    if (initialPayment > 0) {
                        if (rentBill > 0) {
                            DBConfig.insertRecord("payment_history", property, unit, "Rent", date, initialPayment, depositMonths, advanceMonths, status, note, userId);
                        } else if (electricityBill > 0) {
                            DBConfig.insertRecord("payment_history", property, unit, "Electricity", date, initialPayment, 0, 0, status, note, userId);
                        } else if (waterBill > 0) {
                            DBConfig.insertRecord("payment_history", property, unit, "Water", date, initialPayment, 0, 0, status, note, userId);
                        } else if (wifiBill > 0) {
                            DBConfig.insertRecord("payment_history", property, unit, "Wi-Fi", date, initialPayment, 0, 0, status, note, userId);
                        }
                    }

                    // Insert remaining balance into balance_due
                    double remainingBalance = (rentBill + depositAmount + advanceAmount) - initialPayment;

                    if (remainingBalance > 0) {
                        if (rentBill > 0) {
                            DBConfig.insertRecord("balance_due", property, unit, "Rent", date, remainingBalance, depositMonths, advanceMonths, "Pending", note, userId);
                        } else if (electricityBill > 0) {
                            DBConfig.insertRecord("balance_due", property, unit, "Electricity", date, electricityBill - initialPayment, 0, 0, "Pending", note, userId);
                        } else if (waterBill > 0) {
                            DBConfig.insertRecord("balance_due", property, unit, "Water", date, waterBill - initialPayment, 0, 0, "Pending", note, userId);
                        } else if (wifiBill > 0) {
                            DBConfig.insertRecord("balance_due", property, unit, "Wi-Fi", date, wifiBill - initialPayment, 0, 0, "Pending", note, userId);
                        }
                    }
                } else {
                    showAlert("Error", "Partial payment can only be applied to one bill type at a time.");
                    return;
                }
            } else if ("Pending".equals(status) || "Overdue".equals(status)) {
                // Insert into balance_due for each bill type
                if (rentBill > 0) {
                    DBConfig.insertRecord("balance_due", property, unit, "Rent", date, adjustedRentBill, depositMonths, advanceMonths, status, note, userId);
                }
                if (electricityBill > 0) {
                    DBConfig.insertRecord("balance_due", property, unit, "Electricity", date, electricityBill, 0, 0, status, note, userId);
                }
                if (waterBill > 0) {
                    DBConfig.insertRecord("balance_due", property, unit, "Water", date, waterBill, 0, 0, status, note, userId);
                }
                if (wifiBill > 0) {
                    DBConfig.insertRecord("balance_due", property, unit, "Wi-Fi", date, wifiBill, 0, 0, status, note, userId);
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
        loadScene(event, "/controller/BalanceDueView.fxml", "RentEase: Balance Due");
    }

    @FXML
    void create_invoice_btn(MouseEvent event) {
        loadScene(event, "/controller/CreateInvoiceView.fxml", "RentEase: Create Invoice");
    }

    @FXML
    void dashboard_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/DashboardView.fxml", "RentEase: Dashboard");
    }

    @FXML
    void edit_invoice_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/EditInvoiceView.fxml", "RentEase: Edit Invoice");
    }

    @FXML
    void help_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/HelpsFAQsView.fxml", "RentEase: Help & FAQs");
    }

    @FXML
    void my_profile_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/MyProfileView.fxml", "RentEase: My Profile");
    }

    @FXML
    void payment_history_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/PaymentHistoryView.fxml", "RentEase: Payment History");
    }

    private void loadScene(MouseEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void logout_btn_clicked(MouseEvent event) {
        // Create a confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Logout Confirmation");
        alert.setContentText("Are you sure you want to logout?");

        // Add Yes and No buttons to the alert
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show the alert and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            // If the user clicked Yes, load the Login view
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/LoginView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("RentEase: Login");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Unable to load view: " + e.getMessage());
            }
        }
    }

    @FXML
    void clear_btn_clicked(ActionEvent event) {
        // Reload the current scene
        Stage stage = (Stage) clear_btn.getScene().getWindow();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/controller/CreateInvoiceView.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
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