package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CreateInvoiceController {

    @FXML
    private Button add_btn;

    @FXML
    private Text balance_due_btn;

    @FXML
    private Text create_invoice_btn;

    @FXML
    private Text dashboard_btn;

    @FXML
    private DatePicker date_picker;

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
    private TextField property_text_field;

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
    private TextField unit_text_field;

    @FXML
    private TextField water_text_field;

    @FXML
    private TextField wifi_text_field;

    @FXML
    void add_btn_clicked(ActionEvent event) {

    }

    @FXML
    void balance_due_btn_clicked(MouseEvent event) {
        try {
            // Load the Balance Due FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/BalanceDueView.fxml"));
            Parent paymentHistoryRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(paymentHistoryRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void create_invoice_btn(MouseEvent event) {
        try {
            // Load the Helps & FAQs FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/CreateInvoiceView.fxml"));
            Parent Create_InvoiceRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(Create_InvoiceRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void dashboard_btn_clicked(MouseEvent event) {
        try {
            // Load the Helps & FAQs FXML file
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
            // Load the Helps & FAQs FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/EditInvoiceView.fxml"));
            Parent Edit_InvoiceRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(Edit_InvoiceRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void help_btn_clicked(MouseEvent event) {
        try {
            // Load the Helps & FAQs FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/HelpsFAQsView.fxml"));
            Parent Helps_FaqsRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(Helps_FaqsRoot);
            stage.setScene(scene);
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
            Parent paymentHistoryRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(paymentHistoryRoot);
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
