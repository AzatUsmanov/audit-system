package system.audit.controller.aspect;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import system.audit.controller.AuditController;
import system.audit.controller.EmailController;
import system.audit.controller.FindingController;
import system.audit.dto.AuditView;
import system.audit.dto.FindingView;
import system.audit.tool.exception.AuditNameNotUniqueException;
import system.audit.tool.exception.FindingNameNotUniqueException;

import java.util.Map;

/**
 * Класс, исполняющий роль обработчика ошибок
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class ExceptionHandlerAspect {

    private final EmailController emailController;

    private final AuditController auditController;

    private final FindingController findingController;

    @Around("execution(org.springframework.web.servlet.ModelAndView" +
            " system.audit.controller.EmailController.send(..))")
    public Object handleEmailException(ProceedingJoinPoint joinPoint) throws Throwable {
        final var emailForm = emailController.getEmailForm();
        return handleEmailExceptions(joinPoint, emailForm);
    }

    @Around("execution(org.springframework.web.servlet.ModelAndView" +
            " system.audit.controller.AuditController.createAudit(..))")
    public Object handleAuditCreateException(ProceedingJoinPoint joinPoint) throws Throwable {
        final var createForm = auditController.getCreateAuditForm();
        return handleAuditExceptions(joinPoint, createForm);
    }

    @Around("execution(org.springframework.web.servlet.ModelAndView" +
            " system.audit.controller.AuditController.updateAudit(..))")
    public Object handleAuditUpdateException(ProceedingJoinPoint joinPoint) throws Throwable {
        final var auditId =  ((AuditView) joinPoint.getArgs()[0]).getId();
        final var updateForm = auditController.getUpdateAuditFormById(auditId);
        return handleAuditExceptions(joinPoint, updateForm);
    }

    @Around("execution(org.springframework.web.servlet.ModelAndView" +
            " system.audit.controller.FindingController.createFinding(..))")
    public Object handleFindingCreateException(ProceedingJoinPoint joinPoint) throws Throwable {
        final var auditId =  ((int) joinPoint.getArgs()[1]);
        final var createForm = findingController.getCreateFindingForm(auditId);
        return handleFindingExceptions(joinPoint, createForm);

    }

    @Around("execution(org.springframework.web.servlet.ModelAndView" +
            " system.audit.controller.FindingController.updateFinding(..))")
    public Object handleFindingUpdateException(ProceedingJoinPoint joinPoint) throws Throwable {
        final var findingId = ((FindingView) joinPoint.getArgs()[0]).getId();
        final var updateForm = findingController.getUpdateFindingFormById(findingId);
        return handleFindingExceptions(joinPoint, updateForm);
    }

    /**
     *
     * Метод, вызываемый для обработки исключений, возникших
     * при попытке сохранить аудит с неуникальным полем "auditName" {@link AuditView}
     */
    public ModelAndView handleAuditExceptions(ProceedingJoinPoint joinPoint, ModelAndView form) throws Throwable {
        final var errors = (Map<String, String>) form.getModel().get("errors");
        ModelAndView result;
        try {
            result = (ModelAndView) joinPoint.proceed();
        }
        catch (AuditNameNotUniqueException e) {
            errors.put("globalMessage", "Audit ID is not unique");
            result = form;
        }
        return result;
    }

    /**
     *
     * Метод, вызываемый для обработки исключений, возникших
     * при попытке сохранить аудит с неуникальным полем "findingName" {@link FindingView}
     */
    public ModelAndView handleFindingExceptions(ProceedingJoinPoint joinPoint, ModelAndView form) throws Throwable {
        final var errors = (Map<String, String>) form.getModel().get("errors");
        ModelAndView result;
        try {
            result = (ModelAndView) joinPoint.proceed();
        } catch (FindingNameNotUniqueException e) {
            errors.put("globalMessage", "Finding ID is not unique");
            result = form;
        }
        return result;
    }

    /**
     *
     * Метод, вызываемый для обработки исключений, возникших
     * при отправке исключений по почте
     */
    public ModelAndView handleEmailExceptions(ProceedingJoinPoint joinPoint, ModelAndView form) throws Throwable {
        final var errors = (Map<String, String>) form.getModel().get("errors");
        ModelAndView result;
        try {
            result = (ModelAndView) joinPoint.proceed();
        } catch (MailSendException e) {
            errors.put("globalMessage", "There was an error sending your email");
            result = form;
        }
        return result;
    }

}
