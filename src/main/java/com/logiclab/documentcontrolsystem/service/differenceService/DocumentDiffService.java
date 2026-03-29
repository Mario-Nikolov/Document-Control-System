package com.logiclab.documentcontrolsystem.service.differenceService;

import com.logiclab.documentcontrolsystem.domain.DocumentVersion;
import com.logiclab.documentcontrolsystem.dto.response.DiffLineResponse;
import com.logiclab.documentcontrolsystem.dto.response.VersionDiffResponse;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DocumentDiffService {

    private final DocumentVersionRepository documentVersionRepository;
    private final ContentExtractionService contentExtractionService;
    private final DiffAlgorithmService diffAlgorithmService;

    public VersionDiffResponse compareVersions(Integer oldVersionId, Integer newVersionId) {
        validateVersionIds(oldVersionId, newVersionId);

        DocumentVersion oldVersion = documentVersionRepository.findById(oldVersionId)
                .orElseThrow(() -> new RuntimeException("Old version not found!"));

        DocumentVersion newVersion = documentVersionRepository.findById(newVersionId)
                .orElseThrow(() -> new RuntimeException("New version not found!"));

        validateSameDocument(oldVersion, newVersion);

        String oldText = contentExtractionService.extractText(
                oldVersion.getContent(),
                oldVersion.getExtension()
        );

        String newText = contentExtractionService.extractText(
                newVersion.getContent(),
                newVersion.getExtension()
        );

        List<DiffLineResponse> diffLines = diffAlgorithmService.compareTexts(oldText, newText);

        return buildResponse(oldVersion, newVersion, diffLines);
    }

    private void validateVersionIds(Integer oldVersionId, Integer newVersionId) {
        if (oldVersionId == null || newVersionId == null) {
            throw new RuntimeException("Both version IDs are required!");
        }

        if (oldVersionId.equals(newVersionId)) {
            throw new RuntimeException("You must select two different versions!");
        }
    }

    private void validateSameDocument(DocumentVersion oldVersion, DocumentVersion newVersion) {
        if (!oldVersion.getDocument().getId().equals(newVersion.getDocument().getId())) {
            throw new RuntimeException("Versions do not belong to the same document!");
        }
    }

    private VersionDiffResponse buildResponse(
            DocumentVersion oldVersion,
            DocumentVersion newVersion,
            List<DiffLineResponse> diffLines
    ) {
        VersionDiffResponse response = new VersionDiffResponse();
        response.setOldVersionId(oldVersion.getId());
        response.setNewVersionId(newVersion.getId());
        response.setOldVersionNumber(oldVersion.getVersionNumber());
        response.setNewVersionNumber(newVersion.getVersionNumber());
        response.setDocumentId(oldVersion.getDocument().getId());
        response.setDocumentTitle(oldVersion.getDocument().getTitle());
        response.setLines(diffLines);

        return response;
    }
}