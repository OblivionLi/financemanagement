package org.balaur.financemanagement.model.user;

import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDetails {

    @Value("${email.details.app.url}")
    private String MY_APP_URL;

    private String recipient;
    private String message;
    private String subject;
    private String attachment;

    public String generateResetPasswordMail(String resetToken) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Password Reset</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: Arial, sans-serif;\">\n" +
                "\n" +
                "    <div style=\"max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd;\">\n" +
                "\n" +
                "        <h2>Password Reset Request</h2>\n" +
                "\n" +
                "        <p>Hello,</p>\n" +
                "\n" +
                "        <p>We received a request to reset your password. If you didn't make this request, you can ignore this email.</p>\n" +
                "\n" +
                "        <p>If you did request a password reset, click the button below:</p>\n" +
                "\n" +
                "        <a href=\"" + MY_APP_URL + "/reset-password?token=" + resetToken + "\" style=\"display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;\">\n" +
                "            Reset My Password\n" +
                "        </a>\n" +
                "\n" +
                "        <p>If the button above doesn't work, you can also copy and paste the following link into your browser:</p>\n" +
                "\n" +
                "        <p>" + MY_APP_URL + "/reset-password?token=" + resetToken + "</p>\n" +
                "\n" +
                "        <p>This link will expire in 24 hours for security reasons.</p>\n" +
                "\n" +
                "        <p>Thank you,</p>\n" +
                "        <p>Your Finance Management - staff team</p>\n" +
                "\n" +
                "    </div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }
}
