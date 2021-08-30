package com.young.blogusbackend.infra.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

import static com.young.blogusbackend.infra.constant.EmailConstant.FROM_ADDRESS;
import static com.young.blogusbackend.infra.constant.EmailConstant.SUBJECT_TO_VALIDATE;

@Service @RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendMail(String toAddress, String url, String txt) throws MessagingException {
        MailHandler mailHandler = new MailHandler(mailSender);
        mailHandler.setTo(toAddress);
        mailHandler.setFrom(FROM_ADDRESS);
        mailHandler.setSubject(SUBJECT_TO_VALIDATE);
        mailHandler.setText(setHtmlContent(url, txt), true);
        mailHandler.send();
    }

    private String setHtmlContent(String url, String txt) {
        return
                "<div style=\"max-width: 700px; margin:auto; border: 10px solid #ddd; padding: 50px 20px; font-size: 110%;\">" +
                "<h2 style=\"text-align: center; text-transform: uppercase;color: teal;\">블로거스에 가입하신 것을 환영합니다.</h2>" +
                "<p>환영합니다! 블로거스 서비스를 이용하시려면 아래 버튼 클릭하세요.</p>" +
                "<a href=\"" + url + "\" style=\"background: crimson; text-decoration: none; color: white; padding: 10px 20px; margin: 10px 0; display: inline-block;\">" + txt + "</a>" +
                "<p>만약 버튼이 제대로 동작하지 않으면 아래 링크를 클릭하세요:</p>\n" +
                "<div>" + url + "</div>";
    }
}
