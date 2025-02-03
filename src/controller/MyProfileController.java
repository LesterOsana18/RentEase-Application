package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MyProfileController {

    @FXML
    private Text balance_due_btn;

    @FXML
    private Text create_invoice_btn;

    @FXML
    private Text dashboard_btn;
    
    @FXML
    private Button delete_btn;

    @FXML
    private Text edit_invoice_btn;

    @FXML
    private Text help_btn;

    @FXML
    private Button logout_btn;

    @FXML
    private Text my_profile_btn;

    @FXML
    private Text payment_history_btn;

    @FXML
    private TextField rent_bill_text_field;

    @FXML
    private TextField rent_bill_text_field2;

    @FXML
    private Button save_btn;
    
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
    void create_invoice_btn(MouseEvent event) {
        try {
            // Load the Helps & FAQs FXML file
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
            // Load the Helps & FAQs FXML file
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
    void delete_btn(ActionEvent event) {

    }

    @FXML
    void edit_invoice_btn_clicked(MouseEvent event) {
        try {
            // Load the Helps & FAQs FXML file
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
            // Load the Helps & FAQs FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/HelpsFAQsView.fxml"));
            Parent Helps_FaqsRoot = loader.load();

            // Get the current stage (window) from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(Helps_FaqsRoot);
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
    void my_profile_btn_clicked(MouseEvent event) {

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
    void save_btn_clicked(ActionEvent event) {

    }

}
