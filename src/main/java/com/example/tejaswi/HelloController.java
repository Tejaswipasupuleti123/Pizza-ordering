package com.example.tejaswi;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.Optional;

public class HelloController {

    public TableColumn id;
    @FXML private TextField customerNameField;
    @FXML private TextField mobileNumberField;
    @FXML private CheckBox xlCheckbox;
    @FXML private TextField toppingsField;
    @FXML private TableView<PizzaOrder> pizzaOrdersTable;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/pizza";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "";

    @FXML
    public void initialize() {
        initializeTableColumns();
        loadPizzaOrdersFromDatabase();
    }
    private void initializeTableColumns() {
        TableColumn<PizzaOrder, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<PizzaOrder, String> customerColumn = new TableColumn<>("Customer");
        customerColumn.setCellValueFactory(cellData -> cellData.getValue().customerProperty());

        TableColumn<PizzaOrder, String> mobileColumn = new TableColumn<>("Mobile");
        mobileColumn.setCellValueFactory(cellData -> cellData.getValue().mobileProperty());

        TableColumn<PizzaOrder, String> sizeColumn = new TableColumn<>("Size");
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());

        TableColumn<PizzaOrder, Integer> toppingsColumn = new TableColumn<>("Toppings");
        toppingsColumn.setCellValueFactory(cellData -> cellData.getValue().toppingsProperty().asObject());

        TableColumn<PizzaOrder, Double> totalBillColumn = new TableColumn<>("Total Bill");
        totalBillColumn.setCellValueFactory(cellData -> cellData.getValue().totalBillProperty().asObject());

        pizzaOrdersTable.getColumns().addAll(idColumn, customerColumn, mobileColumn, sizeColumn, toppingsColumn, totalBillColumn);
    }
    @FXML
    private void addPizzaOrder() {

        String customer = customerNameField.getText().trim();
        String mobile = mobileNumberField.getText().trim();
        String size = determinePizzaSize();
        int toppings = Integer.parseInt(toppingsField.getText().trim());
        double totalBill = calculateTotalBill(size, toppings);

        PizzaOrder newPizzaOrder = new PizzaOrder(0, customer, mobile, size, toppings, totalBill);
        savePizzaOrderToDatabase(newPizzaOrder);

        clearFields();
        loadPizzaOrdersFromDatabase();
    }
    @FXML
    private void updatePizzaOrder() {
        PizzaOrder selectedOrder = pizzaOrdersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            selectedOrder.setCustomer(customerNameField.getText().trim());
            selectedOrder.setMobile(mobileNumberField.getText().trim());
            selectedOrder.setSize(determinePizzaSize());
            selectedOrder.setToppings(Integer.parseInt(toppingsField.getText().trim()));
            selectedOrder.setTotalBill(calculateTotalBill(selectedOrder.getSize(), selectedOrder.getToppings()));

            updatePizzaOrderInDatabase(selectedOrder);

            clearFields();
            loadPizzaOrdersFromDatabase();
        }
    }
    private void deletePizzaOrder() {
        PizzaOrder selectedOrder = pizzaOrdersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Pizza Order");
            alert.setHeaderText("Are you sure you want to delete this pizza order?");
            alert.setContentText("This action cannot be undone.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deletePizzaOrderFromDatabase(selectedOrder.getId());

                clearFields();
                loadPizzaOrdersFromDatabase();
            }
        }
    }
    private void clearFields() {
        customerNameField.clear();
        mobileNumberField.clear();
        toppingsField.clear();
        xlCheckbox.setSelected(false);
        // Clear other size checkboxes if applicable
        pizzaOrdersTable.getSelectionModel().clearSelection();
    }
    private void loadPizzaOrdersFromDatabase() {
        pizzaOrdersTable.getItems().clear();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT * FROM customer_details";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String customer = resultSet.getString("customer");
                String mobile = resultSet.getString("mobile");
                String size = resultSet.getString("size");
                int toppings = resultSet.getInt("toppings");
                double totalBill = resultSet.getDouble("total bill");

                PizzaOrder pizzaOrder = new PizzaOrder(id, customer, mobile, size, toppings, totalBill);
                pizzaOrdersTable.getItems().add(pizzaOrder);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void savePizzaOrderToDatabase(PizzaOrder pizzaOrder) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "INSERT INTO customer_details (customer, mobile, size, toppings, total bill) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, pizzaOrder.getCustomer());
            statement.setString(2, pizzaOrder.getMobile());
            statement.setString(3, pizzaOrder.getSize());
            statement.setInt(4, pizzaOrder.getToppings());
            statement.setDouble(5, pizzaOrder.getTotalBill());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating pizza order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pizzaOrder.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating pizza order failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     private void updatePizzaOrderInDatabase(PizzaOrder pizzaOrder) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "UPDATE customer_details SET customer=?, mobile=?, size=?, toppings=?, totalbill=? WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, pizzaOrder.getCustomer());
            statement.setString(2, pizzaOrder.getMobile());
            statement.setString(3, pizzaOrder.getSize());
            statement.setInt(4, pizzaOrder.getToppings());
            statement.setDouble(5, pizzaOrder.getTotalBill());
            statement.setInt(6, pizzaOrder.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deletePizzaOrderFromDatabase(int pizzaOrderId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "DELETE FROM customer_details WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, pizzaOrderId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private String determinePizzaSize() {
        if (xlCheckbox.isSelected()) {
            return "XL";
        } else {
            // Handle other size checkboxes if needed
            return "S";
        }
    }
    private double calculateTotalBill(String size, int toppings) {
        double basePrice = 10.0; // Example base price
        // Implement your logic to calculate total bill based on size and toppings
        return basePrice + (toppings * 1.5); // Example calculation
    }
}
