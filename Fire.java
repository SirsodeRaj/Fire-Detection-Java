package fire;

import fire.FireEmailSender;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.util.concurrent.*;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import javazoom.jl.player.Player;
import java.sql.*;
import java.awt.*;

class FireDetectionSystem extends JFrame {
    private VideoCapture camera;
    private CascadeClassifier fireCascade;
    private JLabel cameraLabel;
    private ExecutorService executorService;
    private boolean isAlarmPlaying = false;
    private Player mp3Player; // Reference to the MP3 player
    private Thread alarmThread; // Reference to the alarm thread

    public FireDetectionSystem() {
        setTitle("Fire Detection System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the camera label for displaying video feed
        cameraLabel = new JLabel();
        add(cameraLabel);

        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Initialize the camera and cascade classifier
        camera = new VideoCapture(1);  // Use the default camera (index 0)
        fireCascade = new CascadeClassifier("F:/Fire Detection using deep learning/fire_detection_cascade_model.xml"); // Use the correct path

        // Check if the camera and cascade model are properly loaded
        File file = new File("F:/Fire Detection using deep learning/fire_detection_cascade_model.xml");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Error: Cascade file not found at specified path.", "File Error", JOptionPane.ERROR_MESSAGE);
        }

        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(this, "Error: Camera could not be opened.", "Camera Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        if (fireCascade.empty()) {
            JOptionPane.showMessageDialog(this, "Error: Cascade file not found or loaded incorrectly.", "Cascade Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Use a thread pool to handle fire detection
        executorService = Executors.newFixedThreadPool(2);
        executorService.submit(this::detectFire);

        // Create a button to stop the alarm
        JButton stopAlarmButton = new JButton("Stop Alarm");
        stopAlarmButton.addActionListener(e -> askPasswordToStopAlarm());
        add(stopAlarmButton, "South"); // Add button at the bottom of the frame

        // Handle window closing to release resources
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                executorService.shutdownNow();
                if (camera.isOpened()) {
                    camera.release();
                }
                stopAlarm(); // Stop the alarm when the window is closed
            }
        });
         setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void detectFire() {
        Mat frame = new Mat();
        Mat grayFrame = new Mat();

        while (!Thread.currentThread().isInterrupted()) {
            if (camera.read(frame)) {
                try {
                    // Convert the frame to grayscale for detection
                    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                    // Detect fires in the frame
                    MatOfRect fireRects = new MatOfRect();
                    fireCascade.detectMultiScale(grayFrame, fireRects);

                    Rect[] detectedFires = fireRects.toArray();
                    if (detectedFires.length > 0) {
                        // Fire detected, play the MP3 alarm if not already playing
                        if (!isAlarmPlaying) {
                            isAlarmPlaying = true;
                            playMP3Alarm(); // Play MP3 alarm
                            sendFireDetectionEmail(); // Send email when fire is detected
                        }

                        // Store the image in the database when fire is detected
                        storeImageInDatabase(frame);

                        // Draw rectangles around detected fires
                        for (Rect rect : detectedFires) {
                            Imgproc.rectangle(frame, new org.opencv.core.Point(rect.x, rect.y),
                  new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height),
                  new Scalar(255, 0, 0), 2);

                        }
                    }

                    // Convert the frame to a BufferedImage for display
                    Image image = toBufferedImage(frame);
                    SwingUtilities.invokeLater(() -> cameraLabel.setIcon(new ImageIcon(image)));

                } catch (Exception e) {
                    System.err.println("Error in fire detection process: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // Convert Mat frame to BufferedImage
    // Convert Mat frame to BufferedImage
// Convert Mat frame to BufferedImage
private BufferedImage toBufferedImage(Mat mat) {
    int type = BufferedImage.TYPE_BYTE_GRAY;
    if (mat.channels() > 1) {
        type = BufferedImage.TYPE_3BYTE_BGR;
    }
    int bufferSize = mat.channels() * mat.cols() * mat.rows();
    byte[] buffer = new byte[bufferSize];
    mat.get(0, 0, buffer); // Extract the byte data from the matrix
    BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
    image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), buffer); // Set the byte data to the image
    return image;  // Return the BufferedImage, not Image
}



    // Store the image in the database
    private void storeImageInDatabase(Mat frame) {
        // Convert the Mat frame to a byte array
        BufferedImage bufferedImage = toBufferedImage(frame);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", baos); // You can also use other formats like "png"
            baos.flush();
            byte[] imageData = baos.toByteArray();
            baos.close();

            // Insert the image data into the database
            String query = "INSERT INTO images (image, d) VALUES (?, NOW())";
            String DB_URL = "jdbc:mysql://localhost:3306/fire"; // Your actual DB URL
            String DB_USERNAME = "root"; // Your DB username
            String DB_PASSWORD = ""; // Your DB password

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setBytes(1, imageData); // Set image data as the first parameter
                statement.executeUpdate(); // Execute the insert statement
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Play MP3 alarm
    private void playMP3Alarm() {
        alarmThread = new Thread(() -> {
            while (isAlarmPlaying) {
                try {
                    FileInputStream fis = new FileInputStream("F:/Fire Detection using deep learning/alarm-sound.mp3"); // Provide the correct path to your MP3 file
                    mp3Player = new Player(fis);
                    mp3Player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alarmThread.start();
    }

    // Send fire detection email
    private void sendFireDetectionEmail() {
        String to = getEmailFromDatabase(); // Retrieve the email from the database
        if (to != null && !to.isEmpty()) {
            String subject = "Fire Detected";
            String body = "Fire has been detected in the area.";
            String style = "h1 {color: red;}"; // Optional styling for the email

            FireEmailSender.sendEmail(to, subject, body, style);
        } else {
            System.out.println("No email found to send the fire detection alert.");
        }
    }

    // Get email from the database
    private String getEmailFromDatabase() {
        String email = null;
        String query = "SELECT email FROM fire ORDER BY fdate DESC LIMIT 1"; // Get the most recent email
        String DB_URL = "jdbc:mysql://localhost:3306/fire"; // Replace with your actual DB URL
        String DB_USERNAME = "root"; // Replace with your DB username
        String DB_PASSWORD = ""; // Replace with your DB password
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                email = resultSet.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return email;
    }

    // Ask for password to stop alarm
    private void askPasswordToStopAlarm() {
        String password = JOptionPane.showInputDialog(this, "Enter password to stop the alarm:");
        if ("loki".equals(password)) { // Replace "loki" with the actual password
            stopAlarm();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect password.", "Access Denied", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Stop the alarm
    private void stopAlarm() {
        if (mp3Player != null) {
            mp3Player.close(); // Stop the MP3 player
        }
        isAlarmPlaying = false;
        if (alarmThread != null) {
            alarmThread.interrupt(); // Interrupt the alarm thread
        }
    }

    public static void main(String[] args) {
        new FireDetectionSystem();
    }
}
