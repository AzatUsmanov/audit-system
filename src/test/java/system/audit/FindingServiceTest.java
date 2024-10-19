package system.audit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import system.audit.dto.Audit;
import system.audit.dto.AuditView;
import system.audit.dto.Finding;
import system.audit.dto.FindingView;
import system.audit.repositories.AuditRepository;
import system.audit.repositories.FindingRepository;
import system.audit.service.AuditService;
import system.audit.service.AuditServiceImpl;
import system.audit.service.FindingService;
import system.audit.service.FindingServiceImpl;
import system.audit.tool.converter.AuditConverter;
import system.audit.tool.converter.AuditViewConverter;
import system.audit.tool.converter.FindingConverter;
import system.audit.tool.converter.FindingViewConverter;
import system.audit.tool.exception.AuditNameNotUniqueException;
import system.audit.tool.exception.FindingNameNotUniqueException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindingServiceTest {

    @Mock
    private FindingRepository findingRepository;

    @Mock
    private AuditRepository auditRepository;

    private Converter<FindingView, Finding> findingViewConverter;

    private Converter<Finding, FindingView> findingConverter;

    private FindingService findingService;

    private Supplier<Finding> findingTestExampleGenerator;

    private Supplier<Audit> auditTestExampleGenerator;

    @BeforeEach
    public void initAuditService() {
        this.findingConverter = new FindingConverter();
        this.findingViewConverter = new FindingViewConverter();
        this.auditTestExampleGenerator = new AuditTestExampleGenerator();
        this.findingTestExampleGenerator = new FindingTestExampleGenerator();
        this.findingService = new FindingServiceImpl(
                auditRepository, findingRepository, findingViewConverter, findingConverter);
    }

    @Test
    public void saveFindingWithCorrectScript() throws FindingNameNotUniqueException {
        Finding finding = findingTestExampleGenerator.get();
        FindingView findingView = findingConverter.convert(finding);
        Audit audit = auditTestExampleGenerator.get();
        when(findingRepository.existsByFindingName(findingView.getFindingName()))
                .thenReturn(false);
        when(auditRepository.findById(audit.getId()))
                .thenReturn(Optional.of(audit));

        findingService.save(findingView, audit.getId());

        verify(auditRepository, times(1)).save(audit);
    }

    @Test
    public void saveFindingWithExistentName() throws FindingNameNotUniqueException {
        Finding finding = findingTestExampleGenerator.get();
        FindingView findingView = findingConverter.convert(finding);
        Integer auditId = 1;
        when(findingRepository.existsByFindingName(findingView.getFindingName()))
                .thenReturn(true);

        assertThatThrownBy(() -> findingService.save(findingView, auditId))
                .isInstanceOf(FindingNameNotUniqueException.class);
    }

    @Test
    public void saveFindingWithNonExistentAuditId() {
        Finding finding = findingTestExampleGenerator.get();
        FindingView findingView = findingConverter.convert(finding);
        Audit audit = auditTestExampleGenerator.get();
        when(findingRepository.existsByFindingName(findingView.getFindingName()))
                .thenReturn(false);
        when(auditRepository.findById(audit.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> findingService.save(findingView, audit.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateFindingWithCorrectScript() throws FindingNameNotUniqueException {
        Finding findingPrevious = findingTestExampleGenerator.get();
        Finding findingCurrent = findingTestExampleGenerator.get();
        FindingView findingViewCurrent = findingConverter.convert(findingCurrent);
        updateFindingFields(findingPrevious, findingCurrent);
        when(findingRepository.findById(findingPrevious.getId()))
                .thenReturn(Optional.of(findingPrevious));
        when(findingRepository.existsByFindingName(findingViewCurrent.getFindingName()))
                .thenReturn(findingCurrent.getFindingName().equals(findingPrevious.getFindingName()));

        findingService.updateByFindingId(findingViewCurrent, findingPrevious.getId());

        verify(findingRepository, times(1)).save(findingCurrent);
    }

    @Test
    public void updateFindingWithNonExistentId() {
        Finding findingPrevious = findingTestExampleGenerator.get();
        Finding findingCurrent = findingTestExampleGenerator.get();
        FindingView findingViewCurrent = findingConverter.convert(findingCurrent);
        updateFindingFields(findingPrevious, findingCurrent);
        when(findingRepository.findById(findingPrevious.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> findingService.updateByFindingId(findingViewCurrent, findingPrevious.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateNameOfFindingToNonUniqueName() {
        Finding findingPrevious = findingTestExampleGenerator.get();
        Finding findingCurrent = findingTestExampleGenerator.get();
        FindingView findingViewCurrent = findingConverter.convert(findingCurrent);
        updateFindingFields(findingPrevious, findingCurrent);
        when(findingRepository.findById(findingPrevious.getId()))
                .thenReturn(Optional.of(findingPrevious));
        when(findingRepository.existsByFindingName(findingViewCurrent.getFindingName()))
                .thenReturn(!findingCurrent.getFindingName().equals(findingPrevious.getFindingName()));

        assertThatThrownBy(() -> findingService.updateByFindingId(findingViewCurrent, findingPrevious.getId()))
                .isInstanceOf(FindingNameNotUniqueException.class);
    }

    @Test
    public void findFindingIdWithCorrectScript() {
        Finding finding = findingTestExampleGenerator.get();
        FindingView findingView = findingConverter.convert(finding);
        when(findingRepository.findById(finding.getId()))
                .thenReturn(Optional.of(finding));

        FindingView newFinding = findingService.getById(finding.getId());

        Assertions.assertEquals(findingView, newFinding);
    }

    @Test
    public void findFindingByIdWithNonExistentId() {
        Finding finding = findingTestExampleGenerator.get();
        FindingView findingView = findingConverter.convert(finding);
        when(findingRepository.findById(finding.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> findingService.getById(finding.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void updateFindingFields(Finding previous, Finding current) {
        current.setId(previous.getId());
        current.setFiles(
                Stream.concat(
                                current.getFiles().stream(),
                                previous.getFiles().stream())
                        .toList());
    }

}
