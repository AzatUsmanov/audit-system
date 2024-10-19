package system.audit.controller;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import system.audit.dto.AuditView;
import system.audit.dto.FindingView;
import system.audit.enums.AuditField;
import system.audit.enums.AuditType;
import system.audit.enums.CertificationBodyType;
import system.audit.service.AuditService;
import system.audit.service.FieldValidator;
import system.audit.tool.exception.AuditNameNotUniqueException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Класс, предоставляющей CRUD функционал для работы с аудитами
 * с использованием HTML форм.
 */

@Controller
@AllArgsConstructor
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;

    private final FieldValidator validator;

    /**
     *
     * @return {@link List<AuditField>} - список атрибутов модели аудита, который
     * используется в формах для проведения сортировки и фильтрации списков аудитов по полям
     */
    @ModelAttribute("auditFields")
    public List<AuditField> auditFields() {
        return Arrays.stream(AuditField.values()).toList();
    }

    /**
     *
     * @return {@link List<CertificationBodyType>} - список названий органов по сертификации
     * для представления ограниченного выбора значений для поля "certificationBody" для {@link AuditView}
     */
    @ModelAttribute("certificationBodyType")
    public List<CertificationBodyType> certificationBodyType() {
        return Arrays.stream(CertificationBodyType.values()).toList();
    }

    /**
     *
     * @return {@link List<CertificationBodyType>} - список названий типов аудита
     * для представления ограниченного выбора значений для поля "auditType" для {@link AuditView}
     * */
    @ModelAttribute("auditType")
    public List<AuditType> auditType() {
        return Arrays.stream(AuditType.values()).toList();
    }

    /**
     *
     * @return {@link ModelAndView} - модель, содержащая путь до HTML формы,
     * контейнер для получения данных об аудите {@link AuditView}
     * и контейнер для ошибок, возникших при заполнении информации об аудите.
     */
    @GetMapping("/create-form")
    public ModelAndView getCreateAuditForm(){
        final var modelAndView = new ModelAndView("audit/create-form.html");
        modelAndView.getModel().put("audit", new AuditView());
        modelAndView.getModel().put("errors", new HashMap<String, String>());
        return modelAndView;
    }


    /**
     *
     * @param audit - данные, полученные из HTML формы, об аудите,
     * необходимые для сохранения в системе.
     * @return {@link ModelAndView} - модель, перенаправляющая на форму создания аудита,
     * содержащую ошибки при заполнении формы, если такие есть.
     * @throws AuditNameNotUniqueException - исключение, возникшее при сохранении аудита с неуникальным
     * атрибутом "auditName" {@link AuditView}
     */
    @PostMapping("/create-form")
    public ModelAndView createAudit(@ModelAttribute("audit") AuditView audit) throws AuditNameNotUniqueException {
        final var errors = validator.validate(audit);

        if (!errors.isEmpty()) {
            final var modelAndView = new ModelAndView("audit/create-form.html");
            modelAndView.getModel().put("audit", new AuditView());
            modelAndView.getModel().put("errors", errors);
            return modelAndView;
        }
        auditService.save(audit);
        return new ModelAndView("redirect:/audit/create-form");
    }


    /**
     *
     * @param id - идентификатор аудита, требуемого для редактирования
     * @return {@link ModelAndView} - модель, содержащая путь до HTML формы,
     * контейнер, содержащий данные для редактирования аудита {@link AuditView} с идентификатором равным {@param id},
     * и контейнер для ошибок, возникших при заполнении информации об аудите.
     */
    @GetMapping("/update-form/{id}")
    public ModelAndView getUpdateAuditFormById(@PathVariable Integer id) {
        final var modelAndView = new ModelAndView("audit/update-form.html");
        final var auditView =  auditService.getById(id);
        modelAndView.getModel().put("audit", auditView);
        modelAndView.getModel().put("errors", new HashMap<String, String>());
        return modelAndView;
    }

    /**
     *
     * @param audit - данные, полученные из HTML формы, об отредактированным аудите,
     * необходимые для сохранения в системе.
     * @return {@link ModelAndView} - модель, перенаправляющая на форму редактирования аудита,
     * содержащую ошибки (если такие есть) при заполнении формы.
     * @throws AuditNameNotUniqueException - исключение, возникшее при сохранении аудита с неуникальным
     * атрибутом "auditName" {@link AuditView} (не возникает при сохранении исходного значения "auditName")
     */
    @PostMapping("/update-form")
    public ModelAndView updateAudit(@ModelAttribute("audit") AuditView audit) throws AuditNameNotUniqueException {
        final var auditId = audit.getId();
        final var errors = validator.validate(audit);

        if (!errors.isEmpty()) {
            final var modelAndView = new ModelAndView("audit/update-form.html");
            final var newAuditView =  auditService.getById(auditId);
            modelAndView.getModel().put("audit", newAuditView);
            modelAndView.getModel().put("errors", errors);
            return modelAndView;
        }
        auditService.updateById(audit, auditId);
        return new ModelAndView("redirect:/audit/update-form/" + auditId);
    }

    /**
     *
     * @param sortAuditField - название поля по которому нужна сортировка списка аудитов.
     * По умолчанию сортировка идет по полю id {@link AuditView}.
     * @return список всех аудитов в соответствии с ролью
     */
    @GetMapping("/list")
    public ModelAndView getAuditList(@RequestParam(defaultValue = "id") String sortAuditField) {
        final var modelAndView = new ModelAndView("audit/list.html");
        final var audits = auditService.getAllSorted(sortAuditField);
        modelAndView.getModel().put("auditList", audits);
        return modelAndView;
    }

    /**
     *
     * @param id - идентификатор аудита, по которому идет запрос
     * @return {@link ModelAndView} информация об аудите, с идентификатором равным {@param id}
     */
    @GetMapping("/one/{id}")
    public ModelAndView getAuditById(@PathVariable Integer id) {
        final var modelAndView = new ModelAndView("audit/one.html");
        final var auditView =  auditService.getById(id);
        modelAndView.getModel().put("audit", auditView);
        return modelAndView;
    }

}
