package com.young.blogusbackend.infra.mail;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

public class MailHandler {

    private JavaMailSender mailSender;
    private MimeMessage mimeMessage;
    private MimeMessageHelper messageHelper;

    public MailHandler(JavaMailSender mailSender) throws MessagingException {
        this.mailSender = mailSender;
        mimeMessage = mailSender.createMimeMessage();
        messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    }

    public void setFrom(String fromAddress) throws MessagingException {
        messageHelper.setFrom(fromAddress);
    }

    public void setTo(String email) throws MessagingException {
        messageHelper.setTo(email);
    }

    public void setSubject(String subject) throws MessagingException {
        messageHelper.setSubject(subject);
    }

    public void setText(String text, boolean useHtml) throws MessagingException {
        messageHelper.setText(text, useHtml);
    }

    public void setAttach(String displayFileName, String pathToAttachment) throws MessagingException, IOException {
        File file = new ClassPathResource(pathToAttachment).getFile();
        FileSystemResource resource = new FileSystemResource(file);

        messageHelper.addAttachment(displayFileName, resource);
    }

    public void setInline(String contentId, String pathToInline) throws MessagingException, IOException {
        File file = new ClassPathResource(pathToInline).getFile();
        FileSystemResource resource = new FileSystemResource(file);

        messageHelper.addInline(contentId, resource);
    }

    public void send() {
        mailSender.send(mimeMessage);
    }
}
