package system.audit.controller.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import system.audit.dto.AuditView;
import system.audit.enums.CertificationBodyType;
import system.audit.enums.UserRole;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Класс предназначенный для выполнения дополнительного функционала
 * в соответствии с ролью пользователя
 */
@Aspect
@Component
public class FilterByRightAspect {

    public final static String DEFAULT_TEXT_MESSAGE =
            "New information was registered" +
            " in Russian Maritime Register of " +
            "Shipping audit database. Information " +
            "or decision from your side are requested";

    @Value("${certification-body.mail}")
    private String certificationBodyMail;

    @Value("${rs.mail}")
    private String rsMail;

    /**
     *
     * Метод, добавляющий сообщение по умолчанию в форму для отправки письма
     */
    @Around("execution(org.springframework.web.servlet.ModelAndView" +
            " system.audit.controller.EmailController.getEmailForm(..))")
    public Object addDefaultTextToMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        final var modelAndView = (ModelAndView) joinPoint.proceed();
        final var model = modelAndView.getModel();
        final var attributeName = "message";

        if (!model.containsKey(attributeName)) {
            throw new IllegalArgumentException();
        }
        final var message = (SimpleMailMessage) model.get(attributeName);
        final var authorityName = getCurrentUserAuthorityName();
        final var defaultMail = getDefaultMail(authorityName);
        message.setTo(defaultMail);
        message.setText(DEFAULT_TEXT_MESSAGE);
        model.put(attributeName, message);
        return modelAndView;
    }

    /**
     *
     * Метод, выполняющий функционал фильтрации списка
     * аудита в соответствии с ролью пользователя
     */
    @Around("execution(org.springframework.web.servlet.ModelAndView" +
            " system.audit.controller.AuditController.getAuditList(..))")
    public Object filterAuditList(ProceedingJoinPoint joinPoint) throws Throwable {
        final var modelAndView = (ModelAndView) joinPoint.proceed();
        final var model = modelAndView.getModel();
        final var attributeName = "auditList";

        if (!model.containsKey(attributeName)) {
            throw new IllegalArgumentException();
        }
        final var audits = (List<AuditView>) model.get(attributeName);
        final var filteredAudits = filterAuditsByUserAuthority(audits, getCurrentUserAuthorityName());
        model.put(attributeName, filteredAudits);
        return modelAndView;
    }

    public List<AuditView> filterAuditsByUserAuthority(List<AuditView> audits, String authorityName) {
        final var bodies = getAppropriateCertificationBodies(authorityName);
        return audits
                .stream()
                .filter(x -> bodies.contains(x.getCertificationBody()))
                .toList();
    }

    public String getDefaultMail(String authorityName) {
        return Objects.equals(authorityName, UserRole.PC.name()) ? certificationBodyMail : rsMail;
    }

    public Set<CertificationBodyType> getAppropriateCertificationBodies(String authorityName) {
        final Set<CertificationBodyType> bodies = new HashSet<>();

        if (Objects.equals(authorityName, UserRole.PC.name())) {
            bodies.addAll(List.of(CertificationBodyType.values()));
        } else {
            bodies.add(
                    Arrays.stream(CertificationBodyType.values())
                    .filter(x -> x.name().equals(authorityName))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new));
        }

        return bodies;
    }

    public String getCurrentUserAuthorityName() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .toList()
                .getFirst()
                .getAuthority();
    }

}
