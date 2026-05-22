package fire;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

class ImageDisplayApp extends JFrame {
    private JPanel imagePanel;
    private JTable dateTimeTable;
    private DefaultTableModel tableModel;

    public ImageDisplayApp() {
        setTitle("Image Display - Date and Time Wise");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Layout for the date/time and image display
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Panel to display images
        imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        JScrollPane imageScrollPane = new JScrollPane(imagePanel);

        // Table to display date and time entries
        tableModel = new DefaultTableModel(new Object[]{"Date and Time"}, 0);
        dateTimeTable = new JTable(tableModel);
        dateTimeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(dateTimeTable);

        splitPane.setLeftComponent(tableScrollPane);
        splitPane.setRightComponent(imageScrollPane);

        add(splitPane);

        // Load the date and time entries from the database
        loadDateTimeEntriesFromDatabase();

        // Listen for row selection to show images for the selected date and time
        dateTimeTable.getSelectionModel().addListSelectionListener(e -> showImageForSelectedDateTime());
         setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loadDateTimeEntriesFromDatabase() {
        String dbUrl = "jdbc:mysql://localhost:3306/fire"; // Replace with your DB URL
        String dbUsername = "root"; // Replace with your DB username
        String dbPassword = "raj05"; // Replace with your DB password

        String query = "SELECT DISTINCT d FROM images ORDER BY d DESC"; // Get distinct dates and times

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("d");
                tableModel.addRow(new Object[]{timestamp.toString()}); // Add date/time to table
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showImageForSelectedDateTime() {
        int selectedRow = dateTimeTable.getSelectedRow();
        if (selectedRow != -1) {
            String selectedDateTime = (String) tableModel.getValueAt(selectedRow, 0);
            loadImageForDateTime(selectedDateTime);
        }
    }

    private void loadImageForDateTime(String dateTime) {
        imagePanel.removeAll(); // Clear previous images

        String dbUrl = "jdbc:mysql://localhost:3306/fire"; // Replace with your DB URL
        String dbUsername = "root"; // Replace with your DB username
        String dbPassword = "raj05"; // Replace with your DB password

        String query = "SELECT image, d FROM images WHERE d = ?"; // Get image for selected date and time

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, dateTime); // Set the selected date and time as parameter
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                byte[] imageData = resultSet.getBytes("image");
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

                ImageIcon imageIcon = new ImageIcon(image);
                JLabel imageLabel = new JLabel(imageIcon);

                // Add the image to the panel
                imagePanel.add(imageLabel);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        // Refresh the panel to show the new images
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageDisplayApp());
    }
}
