package system.audit.controller;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import system.audit.service.EmailService;

import java.util.HashMap;

/**
 *
 * Класс, предоставляющей функционал для отправки писем на почту
 * с использованием HTML форм.
 */
@Controller
@AllArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;


    /**
     *
     * @return {@link ModelAndView} - модель, содержащая путь до HTML формы,
     * контейнер для создания письма {@link SimpleMailMessage}
     * и контейнер для ошибок возникших при создании письма.
     */
    @GetMapping
    public ModelAndView getEmailForm() {
        final var modelAndView = new ModelAndView("email.html");
        modelAndView.getModel().put("message", new SimpleMailMessage());
        modelAndView.getModel().put("errors", new HashMap<String, String>());
        return modelAndView;
    }

    /**
     *
     * @param message - письмо, которое необходимо отправить
     * @return модель, перенаправляющая на форму создания письма,
     * содержащую ошибки при заполнении формы, если такие есть.
     */
    @PostMapping
    public ModelAndView send(@ModelAttribute("message") SimpleMailMessage message) {
        emailService.send(message);
        return new ModelAndView("redirect:/email");
    }

}
