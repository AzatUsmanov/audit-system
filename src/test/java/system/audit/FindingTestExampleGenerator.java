package system.audit;

import org.springframework.web.multipart.MultipartFile;
import system.audit.dto.Audit;
import system.audit.dto.AuditDocumentFile;
import system.audit.dto.Finding;
import system.audit.dto.FindingDocumentFile;
import system.audit.dto.FindingView;
import system.audit.enums.AuditType;
import system.audit.enums.CertificationBodyType;
import system.audit.enums.DocumentFileType;
import system.audit.enums.FindingGradationType;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FindingTestExampleGenerator implements Supplier<Finding> {

    @Override
    public Finding get() {
        return Finding
                .builder()
                .id(1)
                .findingName(generateRandomString(10))
                .findingGradation(FindingGradationType.NC)
                .findingText(generateRandomString(10))
                .rootCauseAnalysisDeadline(generateDate())
                .proposedCorrectiveActions(generateRandomString(10))
                .correctiveActionsImplementationDeadline(generateDate())
                .correctiveActionsAcceptance(generateDate())
                .correctiveActionsImplementationInformation(generateRandomString(10))
                .correctiveActionsImplementationFactualDate(generateDate())
                .findingClosureConfirmation(generateDate())
                .files(generateListOfFindingDocumentFiles())
                .build();
    }


    private List<FindingDocumentFile> generateListOfFindingDocumentFiles() {
        Random random = new Random();
        return new ArrayList<>(
                Stream
                        .generate(this::generateFindingDocumentFileTestExample)
                        .limit(random.nextInt(1, 10))
                        .toList());
    }

    private FindingDocumentFile generateFindingDocumentFileTestExample() {
        Random random = new Random();
        return new FindingDocumentFile(
                1,
                DocumentFileType.CORRECTIVE_ACTIONS,
                generateRandomString(random.nextInt(1, 10)),
                generateRandomString(random.nextInt(1, 10)),
                random.nextLong(10),
                generateRandomString(random.nextInt(1, 10)),
                null
        );
    }

    private String generateRandomString(int size) {
        return UUID
                .randomUUID()
                .toString()
                .substring(0, size);
    }

    private Date generateDate() {
        Random random = new Random();
        return Date.valueOf(
                LocalDate.of(
                        random.nextInt(2024),
                        random.nextInt(1, 12),
                        random.nextInt(1, 28)));
    }

}
