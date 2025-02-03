package controller;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Edit_InvoiceController implements Initializable {

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
    private Text payment_history_btn;

    @FXML
    private ComboBox<?> property_cmb_box;

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
    private TextField total_amount_text_field;

    @FXML
    private ComboBox<?> unit_cmb_box;

    @FXML
    private TextField water_text_field;

    @FXML
    private TextField wifi_text_field;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate the ComboBox with items
        bill_type_cmb_box.setItems(FXCollections.observableArrayList(
            "Rent Bill", "Electricity Bill", "Water Bill", "Wi-Fi Bill"
        ));
    }

    @FXML
    void balance_due_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/BalanceDueView.fxml");
    }

    @FXML
    void bill_type_cmb_box_selected(ActionEvent event) {

    }

    @FXML
    void create_invoice_btn(MouseEvent event) {
        loadScene(event, "/controller/CreateInvoiceView.fxml");
    }

    @FXML
    void dashboard_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/DashboardView.fxml");
    }

    @FXML
    void delete_btn_clicked(ActionEvent event) {
        
    }

    @FXML
    void edit_btn_clicked(ActionEvent event) {
        
    }

    @FXML
    void edit_invoice_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/EditInvoiceView.fxml");
    }

    @FXML
    void help_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/HelpsFAQsView.fxml");
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
        loadScene(event, "/controller/MyProfileView.fxml");
    }

    @FXML
    void payment_history_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/PaymentHistoryView.fxml");
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

    private void loadScene(MouseEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}