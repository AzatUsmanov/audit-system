package system.audit.service;

import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import system.audit.dto.Finding;
import system.audit.dto.FindingView;
import system.audit.repositories.AuditRepository;
import system.audit.repositories.FindingRepository;
import system.audit.tool.exception.FindingNameNotUniqueException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Класс, реализующий бизнес логику по работе с замечаниями для аудитов
 */
@Slf4j
@Service
@AllArgsConstructor
public class FindingServiceImpl implements FindingService {

    private final AuditRepository auditRepository;

    private final FindingRepository findingRepository;

    private final Converter<FindingView, Finding> findingViewConverter;

    private final Converter<Finding, FindingView> findingConverter;

    /**
     *
     * @param findingView - данные замечания для сохранения
     * @param auditId - идентификатор аудита для которого создается замечание
     * @throws FindingNameNotUniqueException - исключение,
     * возникающее при попытке сохранить замечании с неуникальным именем.
     */
    @Override
    public void save(FindingView findingView, Integer auditId) throws FindingNameNotUniqueException {
        if (findingRepository.existsByFindingName(findingView.getFindingName())) {
            throw new FindingNameNotUniqueException();
        }
        final var finding = findingViewConverter.convert(findingView);
        final var audit = auditRepository.findById(auditId)
                .orElseThrow(IllegalArgumentException::new);

        audit.getFindings().add(finding);
        auditRepository.save(audit);

        log.info("Saved : {}", finding);
    }

    /**
     *
     * @param findingView - данные замечания для редактирования
     * @param id - идентификатор замечания, которого нужно отредактировать
     * @throws FindingNameNotUniqueException - возникающее,
     * при попытке сохранить замечание с неуникальным атрибутом
     * "findingName" {@link FindingView} (не возникает при сохранении исходного значения "findingName")
     */
    @Override
    @Transactional
    public void updateByFindingId(FindingView findingView, Integer id) throws FindingNameNotUniqueException {
        var findingCurrent = findingViewConverter.convert(findingView);
        var findingPrevious = findingRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        if (findingRepository.existsByFindingName(findingView.getFindingName())
                && !findingPrevious.getFindingName().equals(findingCurrent.getFindingName())) {
            throw new FindingNameNotUniqueException();
        }
        updateFindingFields(findingPrevious, findingCurrent);
        findingRepository.save(findingCurrent);

        log.info("Updated : {}", findingCurrent);
    }

    @Override
    @Transactional
    public List<FindingView> getAllSortedByAuditId(String sortField, Integer auditId) {
        final var findingIdSet = getSetOfFindingsIdByAuditId(auditId);
        return findingRepository
                .findAll(Sort.by(Sort.Direction.ASC, sortField))
                .stream()
                .filter(x -> findingIdSet.contains(x.getId()))
                .map(findingConverter::convert)
                .toList();
    }

    @Override
    @Transactional
    public FindingView getById(Integer id) {
        final var finding =  findingRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        return findingConverter.convert(finding);
    }

    private void updateFindingFields(Finding previous, Finding current) {
        current.setId(previous.getId());
        current.setFiles(
                Stream.concat(
                                current.getFiles().stream(),
                                previous.getFiles().stream())
                        .toList());
    }

    private Set<Integer> getSetOfFindingsIdByAuditId(Integer auditId) {
        return new HashSet<>(
                auditRepository
                        .findById(auditId)
                        .orElseThrow(IllegalArgumentException::new)
                        .getFindings()
                        .stream()
                        .map(Finding::getId)
                        .toList());
    }

}
