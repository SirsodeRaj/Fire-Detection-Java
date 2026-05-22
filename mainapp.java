package fire;

import fire.FireDataEntryGUI;
import fire.ImageDisplayApp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApp extends JFrame {

    public MainApp() {
        // Setting window properties
        setTitle("Main Application");
        setSize(400, 250);
        setLocationRelativeTo(null); // Center the window on the screen
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Main window will not close when secondary windows are closed
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        // Set the background color for the main window
        getContentPane().setBackground(new Color(240, 240, 240));

        // Create and style buttons
        JButton fireDetectionButton = createStyledButton("Fire Detection System");
        JButton imageDisplayButton = createStyledButton("Image Display App");
        JButton fireDataEntryButton = createStyledButton("Fire Data Entry");

        // Add action listeners to the buttons
        fireDetectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FireDetectionSystem(); // Start FireDetectionSystem
            }
        });

        imageDisplayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start ImageDisplayApp in a new window without closing the main app
                new ImageDisplayApp();
            }
        });

        fireDataEntryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FireDataEntryGUI fireDataEntryGUI = new FireDataEntryGUI();
                fireDataEntryGUI.setVisible(true); // Show FireDataEntryGUI
            }
        });

        // Add the buttons to the frame
        add(fireDetectionButton);
        add(imageDisplayButton);
        add(fireDataEntryButton);

        // Make the frame visible
        setVisible(true);
    }

    // Method to create and style buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180)); // Steel blue background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 40));
        button.setBorder(BorderFactory.createBevelBorder(1));
        return button;
    }

    public static void main(String[] args) {
        // Run the main app on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainApp();
            }
        });
    }
}
