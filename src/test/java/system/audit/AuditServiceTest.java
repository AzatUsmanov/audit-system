package system.audit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import system.audit.dto.Audit;
import system.audit.dto.AuditDocumentFile;
import system.audit.dto.AuditView;
import system.audit.enums.AuditField;
import system.audit.enums.AuditType;
import system.audit.enums.CertificationBodyType;
import system.audit.enums.DocumentFileType;
import system.audit.repositories.AuditRepository;
import system.audit.service.AuditService;
import system.audit.service.AuditServiceImpl;
import system.audit.tool.converter.AuditConverter;
import system.audit.tool.converter.AuditViewConverter;
import system.audit.tool.exception.AuditNameNotUniqueException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;

    private Converter<AuditView,Audit> auditViewConverter;

    private Converter<Audit,AuditView> auditConverter;

    private Supplier<Audit> auditTestExampleGenerator;

    private AuditService auditService;

    @BeforeEach
    public void initAuditService() {
        this.auditConverter = new AuditConverter();
        this.auditViewConverter = new AuditViewConverter();
        this.auditTestExampleGenerator = new AuditTestExampleGenerator();
        this.auditService = new AuditServiceImpl(
                auditRepository, auditViewConverter, auditConverter);
    }

    @Test
    public void saveAuditWithCorrectScript() throws AuditNameNotUniqueException {
        Audit audit = auditTestExampleGenerator.get();
        AuditView auditView = auditConverter.convert(audit);
        updateFilesFieldsInAuditView(auditView);
        when(auditRepository.existsByAuditName(auditView.getAuditName()))
                .thenReturn(false);

        auditService.save(auditView);

        verify(auditRepository, times(1)).save(audit);
    }

    @Test
    public void saveAuditWithExistentName() {
        Audit audit = auditTestExampleGenerator.get();
        AuditView auditView = auditConverter.convert(audit);
        updateFilesFieldsInAuditView(auditView);
        when(auditRepository.existsByAuditName(auditView.getAuditName()))
                .thenReturn(true);

        assertThatThrownBy(() -> auditService.save(auditView))
                .isInstanceOf(AuditNameNotUniqueException.class);
    }

    @Test
    public void updateAuditWithCorrectScript() throws AuditNameNotUniqueException {
        Audit auditPrevious = auditTestExampleGenerator.get();
        Audit auditCurrent = auditTestExampleGenerator.get();
        AuditView auditViewPrevious = auditConverter.convert(auditPrevious);
        AuditView auditViewCurrent = auditConverter.convert(auditCurrent);
        Audit updatedAudit = getUpdatedAudit(auditPrevious, auditCurrent);
        updateFilesFieldsInAuditView(auditViewPrevious);
        updateFilesFieldsInAuditView(auditViewCurrent);
        when(auditRepository.existsByAuditName(auditCurrent.getAuditName()))
                .thenReturn(auditCurrent.getAuditName().equals(auditPrevious.getAuditName()));
        when(auditRepository.findById(auditPrevious.getId()))
                .thenReturn(Optional.of(auditPrevious));

        auditService.updateById(auditViewCurrent, auditViewPrevious.getId());

        verify(auditRepository, times(1)).save(updatedAudit);
    }

    @Test
    public void updateAuditWithNonExistentId() {
        Audit auditPrevious = auditTestExampleGenerator.get();
        Audit auditCurrent = auditTestExampleGenerator.get();
        AuditView auditViewCurrent = auditConverter.convert(auditCurrent);
        updateFilesFieldsInAuditView(auditViewCurrent);
        when(auditRepository.findById(auditPrevious.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> auditService.updateById(auditViewCurrent, auditPrevious.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateNameOfAuditorToNonUniqueName() {
        Audit auditPrevious = auditTestExampleGenerator.get();
        Audit auditCurrent = auditTestExampleGenerator.get();
        AuditView auditViewPrevious = auditConverter.convert(auditPrevious);
        AuditView auditViewCurrent = auditConverter.convert(auditCurrent);
        updateFilesFieldsInAuditView(auditViewPrevious);
        updateFilesFieldsInAuditView(auditViewCurrent);
        when(auditRepository.existsByAuditName(auditCurrent.getAuditName()))
                .thenReturn(!auditCurrent.getAuditName().equals(auditPrevious.getAuditName()));
        when(auditRepository.findById(auditPrevious.getId()))
                .thenReturn(Optional.of(auditPrevious));

        assertThatThrownBy(() -> auditService.updateById(auditViewCurrent, auditViewPrevious.getId()))
                .isInstanceOf(AuditNameNotUniqueException.class);
    }

    @Test
    public void findAuditByIdWithCorrectScript() {
        Audit audit = auditTestExampleGenerator.get();
        AuditView auditView = auditConverter.convert(audit);
        when(auditRepository.findById(audit.getId()))
                .thenReturn(Optional.of(audit));

        AuditView newAudit = auditService.getById(audit.getId());

        Assertions.assertEquals(auditView, newAudit);
    }

    @Test
    public void findAuditByIdWithNonExistentId() {
        Audit audit = auditTestExampleGenerator.get();
        AuditView auditView = auditConverter.convert(audit);
        when(auditRepository.findById(auditView.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> auditService.getById(auditView.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void updateFilesFieldsInAuditView(AuditView auditView) {
        AuditDocumentFile auditReportDocumentFile = new AuditDocumentFile(
                1,
                DocumentFileType.AUDIT_REPORT,
                "",
                "",
                0L,
                "",
                null
        );
        AuditDocumentFile paymentConfirmationDocumentFile = new AuditDocumentFile(
                1,
                DocumentFileType.PAYMENT_CONFIRMATION,
                "",
                "",
                0L,
                "",
                null
        );
        auditView.setAuditReportFile(auditReportDocumentFile);
        auditView.setPaymentConfirmationFile(paymentConfirmationDocumentFile);
    }

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
