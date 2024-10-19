package system.audit.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


/**
 * Класс, реализующий функционал отправки электронных писем на почты сотрудников
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(SimpleMailMessage message) {
        message.setFrom(from);
        mailSender.send(message);

        log.info("Sended : {}", message);
    }

}
