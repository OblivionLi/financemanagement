package org.balaur.financemanagement.model.user;

public interface EmailService {
    String sendSimpleMail(EmailDetails emailDetails);
    String sendMailWithAttachment(EmailDetails emailDetails);
    String sendHtmlMail(EmailDetails emailDetails);
}
