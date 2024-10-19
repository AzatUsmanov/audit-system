package system.audit.tool.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import system.audit.dto.Finding;
import system.audit.dto.FindingDocumentFile;
import system.audit.dto.FindingView;
import system.audit.enums.DocumentFileType;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, выполняющий процесс конвертации данных замечания, полученных от клиента,
 * для сохранения в БД
 */
@Component
public class FindingViewConverter implements Converter<FindingView, Finding> {

    @Override
    public Finding convert(FindingView source) {
        return Finding.builder()
                .id
                        (source.getId())
                .findingName
                        (source.getFindingName())
                .findingGradation
                        (source.getFindingGradation())
                .findingText
                        (source.getFindingText())
                .rootCauseAnalysisDeadline
                        (Date.valueOf(source.getRootCauseAnalysisDeadline()))
                .proposedCorrectiveActions
                        (source.getProposedCorrectiveActions())
                .correctiveActionsImplementationDeadline
                        (Date.valueOf(source.getCorrectiveActionsImplementationDeadline()))
                .correctiveActionsAcceptance
                        (Date.valueOf(source.getCorrectiveActionsAcceptance()))
                .correctiveActionsImplementationInformation
                        (source.getCorrectiveActionsImplementationInformation())
                .correctiveActionsImplementationFactualDate
                        (Date.valueOf(source.getCorrectiveActionsImplementationFactualDate()))
                .findingClosureConfirmation
                        (Date.valueOf(source.getFindingClosureConfirmation()))
                .files
                        (getFilesFromFindingView(source))
                .build();
    }

    private static List<FindingDocumentFile> getFilesFromFindingView(FindingView findingView) {
        List<FindingDocumentFile> findingDocumentFiles = new ArrayList<>();

        findingDocumentFiles.addAll(
                toDocumentFiles(findingView.getCorrectiveActionsFiles(), DocumentFileType.CORRECTIVE_ACTIONS));

        return findingDocumentFiles;
    }

    public static List<FindingDocumentFile> toDocumentFiles(
            List<MultipartFile> files, DocumentFileType documentFileType) {
        return files.stream()
                .map(FindingDocumentFile::new)
                .peek(x -> x.setDocumentFileType(documentFileType))
                .filter(x -> x.getOriginalFilename() != null && !x.getOriginalFilename().isEmpty())
                .toList();
    }

}
