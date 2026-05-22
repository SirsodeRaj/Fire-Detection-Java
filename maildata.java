package fire;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class FireDataEntryGUI extends JFrame {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/fire"; // Replace with your actual DB URL
    private static final String DB_USERNAME = "root"; // Replace with your DB username
    private static final String DB_PASSWORD = "raj05"; // Replace with your DB password

    // GUI components
    private JTextField nameField, idField, emailField;
    private JButton submitButton, deleteButton, updateButton;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    public FireDataEntryGUI() {
        setTitle("Fire Data Entry");
        setSize(800, 600); // Adjusted size to accommodate form and table
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Set the background color of the frame
        getContentPane().setBackground(new Color(240, 248, 255)); // Light blue background

        // Initialize GUI components
        JLabel nameLabel = new JLabel("Name:");
        JLabel idLabel = new JLabel("ID:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel fdateLabel = new JLabel("Fire Date:");

        nameField = new JTextField(20);
        idField = new JTextField(20);
        emailField = new JTextField(20);

        submitButton = new JButton("Submit");
        deleteButton = new JButton("Delete");
        updateButton = new JButton("Update");

        // Style components
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        fdateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        updateButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        submitButton.setBackground(new Color(0x4CAF50)); // Green button
        deleteButton.setBackground(new Color(0xFF5722)); // Red button
        updateButton.setBackground(new Color(0x2196F3)); // Blue button

        submitButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);
        updateButton.setForeground(Color.WHITE);

        submitButton.setFocusPainted(false);
        deleteButton.setFocusPainted(false);
        updateButton.setFocusPainted(false);

        submitButton.setPreferredSize(new Dimension(150, 40));
        deleteButton.setPreferredSize(new Dimension(150, 40));
        updateButton.setPreferredSize(new Dimension(150, 40));

        // Initialize table model and JTable
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"Name", "ID", "Email", "Fire Date"});
        dataTable = new JTable(tableModel);
        dataTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dataTable.setRowHeight(30);
        dataTable.setSelectionBackground(new Color(0x4CAF50));
        dataTable.setSelectionForeground(Color.WHITE);

        // Add a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(dataTable);

        // Layout setup
        setLayout(new BorderLayout());

        // Panel for the form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Padding around components

        // Form components placement
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(fdateLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(new JLabel("Current date and time will be added automatically"), gbc);

        // Submit button placement
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1; // Set to span only one column
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(submitButton, gbc);

        // Delete button placement
        gbc.gridx = 0; // Set delete button in the left column
        gbc.gridy = 5;
        formPanel.add(deleteButton, gbc);

        // Update button placement
        gbc.gridx = 1; // Set update button in the right column
        gbc.gridy = 5;
        formPanel.add(updateButton, gbc);

        // Add form panel and table to the frame
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Fetch and display data
        fetchAndDisplayData();

        // Button actions
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the input values
                String name = nameField.getText();
                String idText = idField.getText();
                String email = emailField.getText();

                if (name.isEmpty() || idText.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(FireDataEntryGUI.this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int id = Integer.parseInt(idText);
                    Timestamp fdate = new Timestamp(System.currentTimeMillis());  // Get current date and time

                    // Insert data into the fire table
                    insertFireData(name, id, email, fdate);

                    // Clear the fields after submission
                    nameField.setText("");
                    idField.setText("");
                    emailField.setText("");

                    // Refresh table data
                    fetchAndDisplayData();

                    // Show success message
                    JOptionPane.showMessageDialog(FireDataEntryGUI.this, "Data inserted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(FireDataEntryGUI.this, "ID must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = dataTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) dataTable.getValueAt(selectedRow, 1); // Get the ID from the selected row
                    deleteFireData(id);
                    fetchAndDisplayData(); // Refresh data after deletion
                    JOptionPane.showMessageDialog(FireDataEntryGUI.this, "Data deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(FireDataEntryGUI.this, "Please select a row to delete.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = dataTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String name = nameField.getText();
                    String idText = idField.getText();
                    String email = emailField.getText();

                    if (name.isEmpty() || idText.isEmpty() || email.isEmpty()) {
                        JOptionPane.showMessageDialog(FireDataEntryGUI.this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        int id = Integer.parseInt(idText);
                        Timestamp fdate = new Timestamp(System.currentTimeMillis());  // Get current date and time

                        updateFireData(id, name, email, fdate);
                        fetchAndDisplayData(); // Refresh data after update
                        JOptionPane.showMessageDialog(FireDataEntryGUI.this, "Data updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(FireDataEntryGUI.this, "ID must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(FireDataEntryGUI.this, "Please select a row to update.", "Update Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
         setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void insertFireData(String name, int id, String email, Timestamp fdate) {
        String query = "INSERT INTO fire (name, id, email, fdate) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, id);
            preparedStatement.setString(3, email);
            preparedStatement.setTimestamp(4, fdate);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchAndDisplayData() {
        String query = "SELECT * FROM fire";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            tableModel.setRowCount(0); // Clear existing data in the table

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                Timestamp fdate = resultSet.getTimestamp("fdate");
                tableModel.addRow(new Object[]{name, id, email, fdate});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteFireData(int id) {
        String query = "DELETE FROM fire WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateFireData(int id, String name, String email, Timestamp fdate) {
        String query = "UPDATE fire SET name = ?, email = ?, fdate = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setTimestamp(3, fdate);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FireDataEntryGUI().setVisible(true);
            }
        });
    }
}
