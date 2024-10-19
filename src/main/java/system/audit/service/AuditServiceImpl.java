package system.audit.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import system.audit.dto.Audit;
import system.audit.dto.AuditDocumentFile;
import system.audit.dto.AuditView;
import system.audit.enums.DocumentFileType;
import system.audit.repositories.AuditRepository;
import system.audit.tool.exception.AuditNameNotUniqueException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * Класс, реализующий бизнес логику по работе с аудитами
 */
@Slf4j
@Service
@AllArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    private final Converter<AuditView,Audit> auditViewConverter;

    private final Converter<Audit, AuditView> auditConverter;


    /**
     *
     * @param auditView - данные аудита для сохранения
     * @throws AuditNameNotUniqueException - исключение, возникающее при
     * попытке сохранить аудит с неуникальным полем "auditName" модели {@link Audit}
     */
    @Override
    public void save(@NotNull AuditView auditView) throws AuditNameNotUniqueException {
        if (auditRepository.existsByAuditName(auditView.getAuditName())) {
            throw new AuditNameNotUniqueException();
        }
        var audit = auditViewConverter.convert(auditView);
        auditRepository.save(audit);

        log.info("Saved : " +  audit);
    }

    /**
     *
     * @param auditView - новые данные аудита
     * @param id - идентификатор аудита, который нужно отредактировать
     * @throws AuditNameNotUniqueException - возникающее, при попытке сохранить аудит с неуникальным именем.
     * {@link Audit} (не возникает при сохранении исходного поля "auditName")
     */
    @Override
    @Transactional
    public void updateById(@NotNull AuditView auditView, @NotNull Integer id) throws AuditNameNotUniqueException {
        var auditNew = auditViewConverter.convert(auditView);
        var auditPrevious = auditRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        if (auditRepository.existsByAuditName(auditView.getAuditName())
                && !auditPrevious.getAuditName().equals(auditNew.getAuditName())) {
            throw new AuditNameNotUniqueException();
        }

        final var updatedAudit = getUpdatedAudit(auditPrevious, auditNew);
        auditRepository.save(updatedAudit);

        log.info("Updated with id = " + id + " : " +  updatedAudit);
    }

    @Override
    @Transactional
    public List<AuditView> getAllSorted(String sortField) {
        return auditRepository
                .findAll(Sort.by(Sort.Direction.ASC, sortField))
                .stream()
                .map(auditConverter::convert)
                .toList();
    }

    @Override
    @Transactional
    public AuditView getById(Integer id) {
        var audit =  auditRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        return auditConverter.convert(audit);
    }

    /**
     * Метод, создающий обновленный аудит
     * @param auditPrevious - предыдущие данные об аудите
     * @param auditNew - новые данные об аудите
     */
    private Audit getUpdatedAudit(Audit auditPrevious, Audit auditNew) {
        final var updatedListOfFiles = getUpdatedListOfFiles(auditNew, auditPrevious);

        return Audit.builder()
                .id
                        (auditPrevious.getId())
                .auditName
                        (auditNew.getAuditName())
                .certificationBody
                        (auditNew.getCertificationBody())
                .auditStartDate
                        (auditNew.getAuditStartDate())
                .auditCompletionDate
                        (auditNew.getAuditCompletionDate())
                .office
                        (auditNew.getOffice())
                .contactDetails
                        (auditNew.getOffice())
                .auditType
                        (auditNew.getAuditType())
                .auditPlanAcceptance
                        (auditNew.getAuditPlanAcceptance())
                .auditReportAcceptance
                        (auditNew.getAuditReportAcceptance())
                .files
                        (updatedListOfFiles)
                .build();
    }

    /**
     *
     * @param auditPrevious - старые данные об аудите
     * @param auditNew - новые данные об аудите
     * @return обновленные список файлов, состоящий из новых и старых файлов.
     * (В случае наличия новых файлов PaymentConfirmation и AuditReport, хранящихся в одиночном количестве,
     * всегда заменяются на новые при их наличии)
     */
    private  List<AuditDocumentFile> getUpdatedListOfFiles(Audit auditPrevious, Audit auditNew) {
        List<AuditDocumentFile> updatedListOfNewFiles = auditNew.getFiles();

        updatedListOfNewFiles = replaceFileThatStoredOnlyInSingleQuantity(
                auditPrevious.getFiles(),
                updatedListOfNewFiles,
                DocumentFileType.AUDIT_REPORT);

        updatedListOfNewFiles = replaceFileThatStoredOnlyInSingleQuantity(
                auditPrevious.getFiles(),
                updatedListOfNewFiles,
                DocumentFileType.PAYMENT_CONFIRMATION);

        List<AuditDocumentFile> updatedListOfPreviousFiles = auditPrevious.getFiles();

        updatedListOfPreviousFiles = getFilesExcludingType(updatedListOfPreviousFiles, DocumentFileType.AUDIT_REPORT);
        updatedListOfPreviousFiles = getFilesExcludingType(updatedListOfPreviousFiles, DocumentFileType.PAYMENT_CONFIRMATION);

        return Stream.concat(
                        updatedListOfNewFiles.stream(),
                        updatedListOfPreviousFiles.stream())
                .toList();
    }

    /**
     *
     * @param previousFiles - старый список файлов аудита
     * @param newFiles - новый список файлов аудита
     * @param fileTypeThatStoredOnlyInSingleQuantity -  тип одиночного файла,
     * которого нужно заменить при необходимости
     * @return - обновленный список файлов
     */
    private List<AuditDocumentFile> replaceFileThatStoredOnlyInSingleQuantity(
            List<AuditDocumentFile> previousFiles,
            List<AuditDocumentFile> newFiles,
            DocumentFileType fileTypeThatStoredOnlyInSingleQuantity) {

        final var previousFile = getFileByType(previousFiles, fileTypeThatStoredOnlyInSingleQuantity);
        final var newFile = getFileByType(newFiles, fileTypeThatStoredOnlyInSingleQuantity);

        if (previousFile.isPresent() && newFile.isPresent()) {
            newFile.get().setId(previousFile.get().getId());
        }

        if (previousFile.isPresent() && newFile.isEmpty()) {
            List<AuditDocumentFile> filesExcludingType = getFilesExcludingType(
                    newFiles, fileTypeThatStoredOnlyInSingleQuantity);

            return Stream.concat(
                    filesExcludingType.stream(),
                    Stream.of(previousFile.get()))
                    .toList();

        } else return newFiles;
    }

    private Optional<AuditDocumentFile> getFileByType(
            List<AuditDocumentFile> files, DocumentFileType documentFileType) {
        List<AuditDocumentFile> filesOfSeparateType = files
                .stream()
                .filter(x -> x.getDocumentFileType() == documentFileType)
                .toList();
        return filesOfSeparateType.isEmpty() ? Optional.empty() : Optional.of(filesOfSeparateType.getFirst());
    }

    private List<AuditDocumentFile> getFilesExcludingType(
            List<AuditDocumentFile> files, DocumentFileType documentFileType) {
        return files
                .stream()
                .filter(x -> x.getDocumentFileType() != documentFileType)
                .toList();
    }

}

