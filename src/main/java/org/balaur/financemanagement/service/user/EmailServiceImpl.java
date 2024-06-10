package org.balaur.financemanagement.service.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.model.user.EmailDetails;
import org.balaur.financemanagement.model.user.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Override
    public String sendHtmlMail(EmailDetails emailDetails) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            String sender = mailUsername;
            helper.setFrom(sender);
            helper.setTo(emailDetails.getRecipient());
            helper.setSubject(emailDetails.getSubject());

            helper.setText(emailDetails.getMessage(), true);

            javaMailSender.send(mimeMessage);
            return "Mail sent with success.";
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String sendSimpleMail(EmailDetails emailDetails) {
        return null;
    }

    @Override
    public String sendMailWithAttachment(EmailDetails emailDetails) {
        return null;
    }
}
