package com.young.blogusbackend.service;

import com.young.blogusbackend.exception.SpringBlogusException;
import com.young.blogusbackend.model.NotificationEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor @Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final String EMAIL_FROM = "mayerjeon0116@gmail.com";

    @Async
    public void sendMail(NotificationEmail notificationEmail) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(EMAIL_FROM);
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(notificationEmail.getBody(), true);
        };

        try {
            mailSender.send(messagePreparator);
            log.info("Activation email sent!!");
        } catch (MailException e) {
            log.info("Exception occurred when sending email", e);
            throw new SpringBlogusException(
                    "Exception occurred when sending mail to " + notificationEmail.getRecipient(), e
            );
        }
    }
}
