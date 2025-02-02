package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BalanceDueController implements Initializable {

    @FXML
    private Text balance_due_btn;

    @FXML
    private Text create_invoice_btn;

    @FXML
    private Text dashboard_btn;

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
    private Button payment_history_filter_btn;

    @FXML
    private ComboBox<?> payment_history_month_cmb_box;

    @FXML
    private TableView<BalanceDueInvoice> balance_due_table_view;

    @FXML
    private ComboBox<?> payment_history_year_cmb_box;

    @FXML
    private Text total_properties_text;

    @FXML
    private Text total_revenue_text;

    @FXML
    private Text total_units_text;

    @FXML
    private TableColumn<BalanceDueInvoice, Double> advance;

    @FXML
    private TableColumn<BalanceDueInvoice, Double> amount;

    @FXML
    private TableColumn<BalanceDueInvoice, String> bill_type;

    @FXML
    private TableColumn<BalanceDueInvoice, String> date;

    @FXML
    private TableColumn<BalanceDueInvoice, Double> deposit;

    @FXML
    private TableColumn<BalanceDueInvoice, String> receipt;

    @FXML
    private TableColumn<BalanceDueInvoice, String> property;

    @FXML
    private TableColumn<BalanceDueInvoice, String> status;

    @FXML
    private TableColumn<BalanceDueInvoice, String> unit;
    
    
    ObservableList<BalanceDueInvoice> initialData(){
    	
    	BalanceDueInvoice inv1 = new BalanceDueInvoice("Sunset Apartments", "A101", "2025-02-03", "Electricity", 150.75, 50.50, 5.50, "Paid", "test");
    	return FXCollections.observableArrayList(inv1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize any necessary data here
    	property.setCellValueFactory(new PropertyValueFactory<BalanceDueInvoice, String>("property"));
    	unit.setCellValueFactory(new PropertyValueFactory<BalanceDueInvoice, String>("unit"));
    	date.setCellValueFactory(new PropertyValueFactory<BalanceDueInvoice, String>("date"));
    	bill_type.setCellValueFactory(new PropertyValueFactory<BalanceDueInvoice, String>("billType"));
    	amount.setCellValueFactory(new PropertyValueFactory<BalanceDueInvoice, Double>("amount"));
    	deposit.setCellValueFactory(new PropertyValueFactory<BalanceDueInvoice, Double>("deposit"));
    	advance.setCellValueFactory(new PropertyValueFactory<BalanceDueInvoice, Double>("advance"));
    	status.setCellValueFactory(new PropertyValueFactory<BalanceDueInvoice, String>("status"));
    	receipt.setCellValueFactory(new PropertyValueFactory<BalanceDueInvoice, String>("receipt"));
    	
    	balance_due_table_view.setItems(initialData());
    }

    @FXML
    void balance_due_btn_clicked(MouseEvent event) {
        // Implement the action for balance_due_btn click
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
    void edit_invoice_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/EditInvoiceView.fxml");
    }

    @FXML
    void help_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/HelpsFAQsView.fxml");
    }

    @FXML
    void logout_btn_clicked(MouseEvent event) {
        // Implement the action for logout_btn click
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
    void payment_history_filter_btn_clicked(ActionEvent event) {
        // Implement the action for payment_history_filter_btn click
    }

    @FXML
    void payment_history_month_cmb_box_clicked(ActionEvent event) {
        // Implement the action for payment_history_month_cmb_box click
    }

    @FXML
    void payment_history_year_cmb_box_clicked(ActionEvent event) {
        // Implement the action for payment_history_year_cmb_box click
    }

    void balance_due() {
        // Implement the balance_due method
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