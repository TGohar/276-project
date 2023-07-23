package trackour.trackour.views.forgotPassword;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class ResetLinkService {

    private String host;
    private Integer port;
    private String senderUsername;
    private String senderPassword;
    private String recipientEmail;
    private String resetUrl;
    
    public ResetLinkService (
        String host,
        Integer port,
        String senderUsername,
        String senderPassword,
        String recipientEmail,
        String resetUrl) {
            this.resetUrl = resetUrl;
            this.host = host;
            this.port = port;
            this.senderPassword = senderPassword;
            this.senderUsername = senderUsername;
            this.recipientEmail = recipientEmail;

            System.out.println(host);
            System.out.println(port);
            System.out.println(senderUsername);
            System.out.println(senderPassword);
            System.out.println(recipientEmail);
            System.out.println(resetUrl);
    }

    public void sendEmail(String usrname) {
        final String username = senderUsername;
        final String password = senderPassword;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port.toString());

        // Get the Session object.
        Session session = Session.getInstance(props,
        new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("trackourwebapp@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );
            message.setSubject("Reset Trackour Email");
            message.setContent("<p>Hi "+usrname+", your 24hr one-time reset link is " + "<a href=\"" +resetUrl+ "\">here</a></p>", "text/html");

            Transport.send(message);

            System.out.println("Sent reset email successfully....");

        } catch (MessagingException e) {
                throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String token) {
        return true;
    }
}
