package system.audit;


import system.audit.dto.Audit;
import system.audit.dto.AuditDocumentFile;
import system.audit.dto.AuditView;
import system.audit.enums.AuditType;
import system.audit.enums.CertificationBodyType;
import system.audit.enums.DocumentFileType;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AuditTestExampleGenerator implements Supplier<Audit> {

    @Override
    public Audit get() {
        return Audit
                .builder()
                .id(1)
                .auditName(generateRandomString(10))
                .certificationBody(CertificationBodyType.IFC_GLOBAL)
                .auditStartDate(generateDate())
                .auditCompletionDate(generateDate())
                .office(generateRandomString(10))
                .contactDetails(generateRandomString(10))
                .auditType(AuditType.PAC)
                .auditPlanAcceptance(true)
                .auditReportAcceptance(true)
                .files(generateListOfAuditDocumentFile())
                .findings(new ArrayList<>())
                .build();
    }

    private List<AuditDocumentFile> generateListOfAuditDocumentFile() {
        Random random = new Random();
        return new ArrayList<>(
                Stream
                        .generate(this::generateAuditDocumentFileTestExample)
                        .limit(random.nextInt(1, 10))
                        .toList());
    }

    private AuditDocumentFile generateAuditDocumentFileTestExample() {
        Random random = new Random();
        return new AuditDocumentFile(
                random.nextInt(1000),
                DocumentFileType.VISA,
                generateRandomString(random.nextInt(1, 10)),
                generateRandomString(random.nextInt(1,10)),
                random.nextLong(10),
                generateRandomString(random.nextInt(1,10)),
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
