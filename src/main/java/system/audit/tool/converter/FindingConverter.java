package system.audit.tool.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import system.audit.dto.Finding;
import system.audit.dto.FindingDocumentFile;
import system.audit.dto.FindingView;
import system.audit.enums.DocumentFileType;

import java.util.List;

/**
 * Класс, выполняющий процесс конвертации данных замечания, полученных из БД,
 * для предоставления информации клиенту
 */
@Component
public class FindingConverter implements Converter<Finding, FindingView> {

    @Override
    public FindingView convert(Finding source) {
        return FindingView.builder()
                .id
                        (source.getId())
                .findingName
                        (source.getFindingName())
                .findingGradation
                        (source.getFindingGradation())
                .findingText
                        (source.getFindingText())
                .rootCauseAnalysisDeadline
                        (source.getRootCauseAnalysisDeadline().toString())
                .proposedCorrectiveActions
                        (source.getProposedCorrectiveActions())
                .correctiveActionsImplementationDeadline
                        (source.getCorrectiveActionsImplementationDeadline().toString())
                .correctiveActionsAcceptance
                        (source.getCorrectiveActionsAcceptance().toString())
                .correctiveActionsImplementationInformation
                        (source.getCorrectiveActionsImplementationInformation())
                .correctiveActionsFiles
                        (toMultipartFiles(source.getFiles(), DocumentFileType.CORRECTIVE_ACTIONS))
                .correctiveActionsImplementationFactualDate
                        (source.getCorrectiveActionsImplementationFactualDate().toString())
                .findingClosureConfirmation
                        (source.getFindingClosureConfirmation().toString())
                .build();
    }

    public static <T extends FindingDocumentFile> List<MultipartFile> toMultipartFiles(
            List<FindingDocumentFile> files, DocumentFileType documentFileType) {
        return files.stream()
                .filter(x -> x.getDocumentFileType() == documentFileType)
                .map(x -> (MultipartFile) x )
                .toList();
    }

}
