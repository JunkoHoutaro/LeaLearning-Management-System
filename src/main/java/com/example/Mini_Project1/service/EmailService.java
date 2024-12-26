package com.example.Mini_Project1.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;

    private String loadTemplate(String templateName, Map<String, Object> model) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error("Error loading template {}: {}", templateName, e.getMessage());
            throw new RuntimeException("Error loading email template", e);
        }
    }

    public void sendResetPasswordEmail(String to, String token, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Map<String, Object> model = new HashMap<>();
            model.put("name", name);
            model.put("reset_password_url", "http://localhost:3000/reset-password?token=" + token);
            model.put("to", to);

            String htmlContent = loadTemplate("auth/mail-reset-password.html", model);

            helper.setTo(to);
            helper.setSubject("Reset Your Password");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Reset password email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send reset password email: {}", e.getMessage());
            throw new RuntimeException("Error sending reset password email", e);
        }
    }
}
