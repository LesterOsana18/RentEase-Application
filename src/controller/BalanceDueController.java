package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.Year;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    private Button balance_due_filter_btn;

    @FXML
    private ComboBox<String> balance_due_month_cmb_box;

    @FXML
    private ComboBox<Integer> balance_due_year_cmb_box;

    @FXML
    private TableView<BalanceDueInvoice> balance_due_table_view;

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
    private TableColumn<BalanceDueInvoice, Void> receipt;

    @FXML
    private TableColumn<BalanceDueInvoice, String> property;

    @FXML
    private TableColumn<BalanceDueInvoice, String> status;

    @FXML
    private TableColumn<BalanceDueInvoice, String> unit;

    private ObservableList<BalanceDueInvoice> allInvoices;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize month ComboBox with months of the year
        balance_due_month_cmb_box.setItems(FXCollections.observableArrayList(
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

        // Initialize any other necessary data here
        property.setCellValueFactory(new PropertyValueFactory<>("property"));
        unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        bill_type.setCellValueFactory(new PropertyValueFactory<>("billType"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        deposit.setCellValueFactory(new PropertyValueFactory<>("deposit"));
        advance.setCellValueFactory(new PropertyValueFactory<>("advance"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        addReceiptButtonToTable();

        // Use Platform.runLater to initialize data after the scene is set
        Platform.runLater(() -> {
            Stage stage = (Stage) balance_due_table_view.getScene().getWindow();
            allInvoices = fetchBalanceDueData(stage);
            balance_due_table_view.setItems(allInvoices);

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

    private ObservableList<BalanceDueInvoice> fetchBalanceDueData(Stage stage) {
        List<BalanceDueInvoice> invoices = DBConfig.getBalanceDueInvoices(stage);
        return FXCollections.observableArrayList(invoices);
    }

    private void addReceiptButtonToTable() {
        Callback<TableColumn<BalanceDueInvoice, Void>, TableCell<BalanceDueInvoice, Void>> cellFactory = new Callback<TableColumn<BalanceDueInvoice, Void>, TableCell<BalanceDueInvoice, Void>>() {
            @Override
            public TableCell<BalanceDueInvoice, Void> call(final TableColumn<BalanceDueInvoice, Void> param) {
                final TableCell<BalanceDueInvoice, Void> cell = new TableCell<BalanceDueInvoice, Void>() {
                    private final Button btn = new Button("Save Receipt");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            BalanceDueInvoice invoice = getTableView().getItems().get(getIndex());
                            saveReceiptToTextFile(invoice);
                        });
                        // Apply the CSS class to the button
                        btn.getStyleClass().add("receipt-button");
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        receipt.setCellFactory(cellFactory);
    }

    private void saveReceiptToTextFile(BalanceDueInvoice invoice) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Receipt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(balance_due_table_view.getScene().getWindow());

        if (file != null) {
            new Thread(() -> {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("Receipt for Property: " + invoice.getProperty() + "\n");
                    writer.write("Unit: " + invoice.getUnit() + "\n");
                    writer.write("Date: " + invoice.getDate() + "\n");
                    writer.write("Bill Type: " + invoice.getBillType() + "\n");
                    writer.write("Amount: " + invoice.getAmount() + "\n");
                    writer.write("Deposit: " + invoice.getDeposit() + "\n");
                    writer.write("Advance: " + invoice.getAdvance() + "\n");
                    writer.write("Status: " + invoice.getStatus() + "\n");
                    System.out.println("Text file created and saved successfully.");
                } catch (IOException e) {
                    System.err.println("Error creating text file: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        } else {
            System.err.println("File chooser returned null, text file not created.");
        }
    }

    @FXML
    void balance_due_btn_clicked(MouseEvent event) {
        // Implement the action for balance_due_btn click
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

    // Utility method to show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void my_profile_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/MyProfileView.fxml", "RentEase: My Profile");
    }

    @FXML
    void payment_history_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/PaymentHistoryView.fxml", "RentEase: Payment History");
    }

    @FXML
    void balance_due_filter_btn_clicked(ActionEvent event) {
        if (balance_due_filter_btn.getText().equals("Filter")) {
            filterInvoices();
        } else {
            reloadScene();
        }
    }

    @FXML
    void balance_due_month_cmb_box_clicked(ActionEvent event) {
        // Implement the action for balance_due_month_cmb_box click if needed
    }

    @FXML
    void balance_due_year_cmb_box_clicked(ActionEvent event) {
        // Implement the action for balance_due_year_cmb_box click if needed
    }

    private void filterInvoices() {
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
        balance_due_filter_btn.setText("Remove Filter");
    }

    private void reloadScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/BalanceDueView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) balance_due_filter_btn.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}