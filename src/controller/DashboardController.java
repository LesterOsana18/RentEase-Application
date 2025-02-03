package controller;

import java.io.IOException;
import java.net.URL;
import java.time.Year;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javafx.application.Platform;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DashboardController implements Initializable {

    @FXML
    private TableColumn<BalanceDueInvoice, Double> b_amount;

    @FXML
    private TableColumn<BalanceDueInvoice, String> b_bill_type;

    @FXML
    private TableColumn<BalanceDueInvoice, String> b_date;

    @FXML
    private TableColumn<BalanceDueInvoice, String> b_property;

    @FXML
    private TableColumn<BalanceDueInvoice, String> b_unit;

    @FXML
    private Text balance_due_btn;

    @FXML
    private Button balance_due_filter_btn;

    @FXML
    private ComboBox<String> balance_due_month_cmb_box;

    @FXML
    private Button balance_due_see_all_btn;

    @FXML
    private TableView<BalanceDueInvoice> balance_due_table_view;

    @FXML
    private ComboBox<Integer> balance_due_year_cmb_box;

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
    private TableColumn<PaymentHistoryInvoice, Double> p_amount;

    @FXML
    private TableColumn<PaymentHistoryInvoice, String> p_bill_type;

    @FXML
    private TableColumn<PaymentHistoryInvoice, String> p_date;

    @FXML
    private TableColumn<PaymentHistoryInvoice, String> p_property;

    @FXML
    private TableColumn<PaymentHistoryInvoice, String> p_unit;

    @FXML
    private Text payment_history_btn;

    @FXML
    private Button payment_history_filter_btn;

    @FXML
    private ComboBox<String> payment_history_month_cmb_box;

    @FXML
    private Button payment_history_see_all_btn;

    @FXML
    private TableView<PaymentHistoryInvoice> payment_history_table_view;

    @FXML
    private ComboBox<Integer> payment_history_year_cmb_box;

    @FXML
    private Text total_properties_text;

    @FXML
    private Text total_revenue_text;

    @FXML
    private Text total_units_text;

    private ObservableList<BalanceDueInvoice> allBalanceDueInvoices;
    private ObservableList<PaymentHistoryInvoice> allPaymentHistoryInvoices;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize month ComboBox with months of the year
        balance_due_month_cmb_box.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));
        payment_history_month_cmb_box.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));

        // Initialize year ComboBox with last 5 years and 1 year ahead
        int currentYear = Year.now().getValue();
        List<Integer> years = Stream.concat(
                IntStream.rangeClosed(currentYear - 5, currentYear).boxed(),
                Stream.of(currentYear + 1)
        ).collect(Collectors.toList());
        balance_due_year_cmb_box.setItems(FXCollections.observableArrayList(years));
        payment_history_year_cmb_box.setItems(FXCollections.observableArrayList(years));

        // Initialize columns for balance_due_table_view
        b_property.setCellValueFactory(new PropertyValueFactory<>("property"));
        b_unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        b_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        b_bill_type.setCellValueFactory(new PropertyValueFactory<>("billType"));
        b_amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Initialize columns for payment_history_table_view
        p_property.setCellValueFactory(new PropertyValueFactory<>("property"));
        p_unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        p_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        p_bill_type.setCellValueFactory(new PropertyValueFactory<>("billType"));
        p_amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Use Platform.runLater to initialize data after the scene is set
        Platform.runLater(() -> {
            Stage stage = (Stage) balance_due_table_view.getScene().getWindow();
            allBalanceDueInvoices = fetchDashboardBalanceDueData(stage);
            balance_due_table_view.setItems(allBalanceDueInvoices);

            allPaymentHistoryInvoices = fetchDashboardPaymentHistoryData(stage);
            payment_history_table_view.setItems(allPaymentHistoryInvoices);

            // Set total properties and units text
            int totalProperties = DBConfig.getTotalDistinctProperties(stage);
            int totalUnits = DBConfig.getTotalDistinctUnits(stage);
            total_properties_text.setText(String.valueOf(totalProperties));
            total_units_text.setText(String.valueOf(totalUnits));

            // Set total revenue text
            double totalRevenue = DBConfig.getTotalRevenue(stage);
            total_revenue_text.setText(String.format("%.2f", totalRevenue));
        });
    }

    private ObservableList<BalanceDueInvoice> fetchDashboardBalanceDueData(Stage stage) {
        List<BalanceDueInvoice> invoices = DBConfig.getDashboardBalanceDueInvoices(stage);
        return FXCollections.observableArrayList(invoices);
    }

    private ObservableList<PaymentHistoryInvoice> fetchDashboardPaymentHistoryData(Stage stage) {
        List<PaymentHistoryInvoice> invoices = DBConfig.getDashboardPaymentHistoryInvoices(stage);
        return FXCollections.observableArrayList(invoices);
    }

    private void filterBalanceDueInvoices() {
        String selectedMonth = balance_due_month_cmb_box.getValue();
        Integer selectedYear = balance_due_year_cmb_box.getValue();

        if (selectedMonth == null || selectedYear == null) {
            // Show error message if either month or year is not selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Filter Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select both month and year to filter.");
            alert.showAndWait();
            return;
        }

        Stage stage = (Stage) balance_due_table_view.getScene().getWindow();
        List<BalanceDueInvoice> filteredInvoices = DBConfig.getFilteredBalanceDueInvoices(stage, selectedMonth, selectedYear);
        balance_due_table_view.setItems(FXCollections.observableArrayList(filteredInvoices));
        balance_due_filter_btn.setText("Remove");
    }

    private void filterPaymentHistoryInvoices() {
        String selectedMonth = payment_history_month_cmb_box.getValue();
        Integer selectedYear = payment_history_year_cmb_box.getValue();

        if (selectedMonth == null || selectedYear == null) {
            // Show error message if either month or year is not selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Filter Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select both month and year to filter.");
            alert.showAndWait();
            return;
        }

        Stage stage = (Stage) payment_history_table_view.getScene().getWindow();
        List<PaymentHistoryInvoice> filteredInvoices = DBConfig.getFilteredPaymentHistoryInvoices(stage, selectedMonth, selectedYear);
        payment_history_table_view.setItems(FXCollections.observableArrayList(filteredInvoices));
        payment_history_filter_btn.setText("Remove");
    }

    private void reloadBalanceDueTable() {
        Stage stage = (Stage) balance_due_table_view.getScene().getWindow();
        allBalanceDueInvoices = fetchDashboardBalanceDueData(stage);
        balance_due_table_view.setItems(allBalanceDueInvoices);
        balance_due_filter_btn.setText("Filter");
    }

    private void reloadPaymentHistoryTable() {
        Stage stage = (Stage) payment_history_table_view.getScene().getWindow();
        allPaymentHistoryInvoices = fetchDashboardPaymentHistoryData(stage);
        payment_history_table_view.setItems(allPaymentHistoryInvoices);
        payment_history_filter_btn.setText("Filter");
    }

    @FXML
    void balance_due_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/BalanceDueView.fxml");
    }

    @FXML
    void balance_due_history_filter_btn_clicked(ActionEvent event) {
        if (balance_due_filter_btn.getText().equals("Filter")) {
            filterBalanceDueInvoices();
        } else {
            reloadBalanceDueTable();
        }
    }

    @FXML
    void balance_due_month_cmb_box_clicked(ActionEvent event) {
        // Implement logic for balance_due_month_cmb_box click if needed
    }

    @FXML
    void balance_due_see_all_btn_clicked(ActionEvent event) {
        loadScene(event, "/controller/BalanceDueView.fxml");
    }

    @FXML
    void balance_due_year_cmb_box_clicked(ActionEvent event) {
        // Implement logic for balance_due_year_cmb_box click if needed
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
        // Implement logic for logout_btn click if needed
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
        if (payment_history_filter_btn.getText().equals("Filter")) {
            filterPaymentHistoryInvoices();
        } else {
            reloadPaymentHistoryTable();
        }
    }

    @FXML
    void payment_history_month_cmb_box_clicked(ActionEvent event) {
        // Implement logic for payment_history_month_cmb_box click if needed
    }

    @FXML
    void payment_history_see_all_btn_clicked(ActionEvent event) {
        loadScene(event, "/controller/PaymentHistoryView.fxml");
    }

    @FXML
    void payment_history_year_cmb_box_clicked(ActionEvent event) {
        // Implement logic for payment_history_year_cmb_box click if needed
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

    private void loadScene(ActionEvent event, String fxmlPath) {
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