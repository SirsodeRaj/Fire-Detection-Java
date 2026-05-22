package fire;

import fire.MainApp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Fire extends JFrame {

    public Fire() {
        // Set window properties for the login page
        setTitle("Admin Login");
        setSize(400, 250);
        setLocationRelativeTo(null); // Center the window on the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set background color for the login page
        getContentPane().setBackground(new Color(245, 245, 245));

        // Create a panel for the login form with rounded corners and padding
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 20));  // 4 rows and 2 columns
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Username and password labels and fields with custom font
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

        // Login button with modern styling
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(70, 130, 180)); // Steel blue background
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(250, 40));
        loginButton.setBorder(BorderFactory.createBevelBorder(1));

        // Add components to the panel
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Empty cell for spacing
        panel.add(loginButton);

        // Add the panel to the frame
        add(panel, BorderLayout.CENTER);

        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                // Check credentials (replace with your own logic)
                if (username.equals("admin") && String.valueOf(password).equals("123")) {
                    // Close login page and open the main app
                    JOptionPane.showMessageDialog(null, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Close the login page
                    new MainApp(); // Open the MainApp window
                } else {
                    // Show error if credentials are incorrect
                    JOptionPane.showMessageDialog(null, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Make the login page visible
        setVisible(true);
    }

    public static void main(String[] args) {
        // Run the login page
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Fire();
            }
        });
    }
}
