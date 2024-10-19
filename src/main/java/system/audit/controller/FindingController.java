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

import system.audit.dto.FindingView;
import system.audit.enums.FindingField;
import system.audit.enums.FindingGradationType;
import system.audit.service.FieldValidator;
import system.audit.service.FindingService;
import system.audit.tool.exception.FindingNameNotUniqueException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 *
 * Класс, предоставляющей CRUD функционал для работы с замечаниями для аудитов
 * с использованием HTML форм.
 */

@Controller
@AllArgsConstructor
@RequestMapping("/finding")
public class FindingController {

    private final FindingService findingService;

    private final FieldValidator validator;

    /**
     *
     * @return {@link List<FindingField>} - список атрибутов модели замечания, который
     * используется в формах для проведения сортировки и фильтрации списков комментариев по полям
     */
    @ModelAttribute("findingFields")
    public List<FindingField> fields() {
        return Arrays.stream(FindingField.values()).toList();
    }

    /**
     *
     * @return {@link List<FindingGradationType>} - список градаций замечания
     * для представления ограниченного выбора значений для поля "findingGradation" для {@link FindingView}
     */
    @ModelAttribute("findingGradationType")
    public List<FindingGradationType> findingGradationType() {
        return Arrays.stream(FindingGradationType.values()).toList();
    }

    /**
     *
     * @param auditId - идентификатор аудита, для которого создается замечание
     * @return {@link ModelAndView} - модель, содержащая путь до HTML формы,
     * контейнер для получения данных об замечании {@link FindingView}
     * и контейнер для ошибок, возникших при заполнении информации об аудите.
     */
    @GetMapping("/create-form/{auditId}")
    public ModelAndView getCreateFindingForm(@PathVariable("auditId") Integer auditId) {
        final var modelAndView = new ModelAndView("finding/create-form.html");
        modelAndView.getModel().put("finding", new FindingView());
        modelAndView.getModel().put("auditId", auditId);
        modelAndView.getModel().put("errors", new HashMap<String, String>());
        return modelAndView;
    }

    /**
     *
     * @param findingView - данные, полученные из HTML формы, об замечание,
     * необходимые для сохранения в системе.
     * @param auditId - идентификатор аудита, для которого создается замечание
     * @return {@link ModelAndView} - модель, перенаправляющая на форму создания замечания,
     * содержащую ошибки при заполнении формы, если такие есть.
     * @throws FindingNameNotUniqueException - исключение, возникшее при сохранении замечания с неуникальным
     * атрибутом "findingName" {@link FindingView}
     */
    @PostMapping("/create-form")
    public ModelAndView createFinding(@ModelAttribute("finding") FindingView findingView,
                              @RequestParam("auditId") Integer auditId) throws FindingNameNotUniqueException {
        var errors = validator.validate(findingView);

        if (!errors.isEmpty()) {
            final var modelAndView = new ModelAndView("finding/create-form.html");
            modelAndView.getModel().put("auditId", auditId);
            modelAndView.getModel().put("finding", new FindingView());
            modelAndView.getModel().put("errors", errors);
            return modelAndView;
        }
        findingService.save(findingView, auditId);
        return new ModelAndView("redirect:/finding/create-form/" + auditId);
    }


    /**
     *
     * @param id - идентификатор замечания, требуемого для редактирования
     * @return {@link ModelAndView} - модель, содержащая путь до HTML формы,
     * контейнер, содержащий данные для редактирования замечания {@link FindingView} с идентификатором равным {@param id},
     * и контейнер для ошибок, возникших при заполнении информации о замечании.
     */
    @GetMapping("/update-form/{id}")
    public ModelAndView getUpdateFindingFormById(@PathVariable("id") Integer id){
        final var modelAndView = new ModelAndView("finding/update-form.html");
        final var findingView =  findingService.getById(id);
        modelAndView.getModel().put("finding", findingView);
        modelAndView.getModel().put("errors", new HashMap<String, String>());
        return modelAndView;
    }

    /**
     *
     * @param findingView - данные, полученные из HTML формы, об отредактированным замечании,
     * необходимые для сохранения в системе.
     * @return {@link ModelAndView} - модель, перенаправляющая на форму редактирования замечания,
     * содержащую ошибки (если такие есть) при заполнении формы.
     * @throws FindingNameNotUniqueException  - исключение, возникшее при сохранении замечния с неуникальным
     * атрибутом "findingName" {@link FindingView} (не возникает при сохранении исходного значения "findingName")
     */
    @PostMapping("/update-form")
    public ModelAndView updateFinding(@ModelAttribute("finding") FindingView findingView) throws FindingNameNotUniqueException {
        final var errors = validator.validate(findingView);
        final var findingId = findingView.getId();

        if (!errors.isEmpty()) {
            final var modelAndView = new ModelAndView("finding/update-form.html");
            final var newFindingView =  findingService.getById(findingId);
            modelAndView.getModel().put("finding", newFindingView);
            modelAndView.getModel().put("errors", errors);
            return modelAndView;
        }

        findingService.updateByFindingId(findingView, findingId);
        return new ModelAndView("redirect:/finding/update-form/" + findingView.getId());
    }

    /**
     *
     * @param sortFindingField название поля по которому нужна сортировка.
     * @param auditId - идентификатор аудита, для которого выдается список замечаний
     * @return список всех замечаний для адуита, с идентификатором равным {@param id}
     */
    @GetMapping("/list")
    public ModelAndView getFindingList(@RequestParam(defaultValue = "id") String sortFindingField,
                                       @RequestParam Integer auditId) {
        final var modelAndView = new ModelAndView("finding/list.html");
        final var findings = findingService.getAllSortedByAuditId(sortFindingField, auditId);
        modelAndView.getModel().put("findingList", findings);
        modelAndView.getModel().put("auditId", auditId);
        return modelAndView;
    }

    /**
     *
     * @param id - идентификатор замечания, по которому идет запрос
     * @return {@link ModelAndView} - информация о замечании, с идентификатором равным {@param id}
     */
    @GetMapping("/one/{id}")
    public ModelAndView getFindingById(@PathVariable("id") Integer id) {
        final var modelAndView = new ModelAndView("finding/one.html");
        final var findingView =  findingService.getById(id);
        modelAndView.getModel().put("finding", findingView);
        return modelAndView;
    }

}
