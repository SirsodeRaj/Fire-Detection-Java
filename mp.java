package fire;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

class FireEmailSender {
    
    // Method to send the email
    public static void sendEmail(String to, String subject, String body, String style) {
        final String from = "detectivefire69@gmail.com";  // Sender's email
        final String username = "detectivefire69@gmail.com";  // Gmail username
        final String password = "bdpz lfun myrd dsgf";  // Gmail password or App Password
        String host = "smtp.gmail.com";  // SMTP host for Gmail
        
        // Set properties for the mail server
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587"); // Use port 587 for STARTTLS
        props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Enforce TLSv1.2
        props.put("mail.debug", "true"); // Enable debugging

        // Get the Session object
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);
            
            // Set From: header field
            message.setFrom(new InternetAddress(from));
            
            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            
            // Set Subject: header field
            message.setSubject(subject);
            
            // Create HTML body content with style
            String htmlBody = "<html><head><style>" + style + "</style></head><body>" + body + "</body></html>";
            
            // Set the actual message (HTML content)
            message.setContent(htmlBody, "text/html");

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Sample data array (you can replace it with your actual data)
        String[][] data = {
            {"fat12abc@gmail.com"},
            {"fat12abc@gmail.com"}
        };

        // Iterate over the data and send emails
        for (String[] row : data) {
            System.out.println("Sending email to: " + row[0]);
            sendEmail(row[0], "Test", "Fire is detected", "h1 {color: red}");
        }
    }
}
