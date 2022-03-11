package vn.nuce.datn_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Component
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Autowired
    public SimpleMailMessage template;

    public void sendSimpleHtmlMessage(String to, String subject) {
        String text = String.format(template.getText());
        sendSimpleMessage(to, subject, text);
    }

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        //ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//        ClassLoader classLoader= this.getClass().getClassLoader();
//        for (int i = 1; i < 8; i++) {
//            String fileName = "static/mail/images/image-" + i + ".png";
////            File file = new File("..\\..\\..\\..\\resources\\templates\\thymeleaf\\images\\image-" + i + ".png");
//            File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
//            helper.addInline("image_" + i + ".png", file);
//        }
        emailSender.send(message);
    }

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    public void sendMessageUsingThymeleafTemplate(
            String to, String subject, Map templateModel)
            throws MessagingException {

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process("thymeleaf/mail.html", thymeleafContext);
        sendHtmlMessage(to, subject, htmlBody);
    }

}

