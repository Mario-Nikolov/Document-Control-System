package com.logiclab.documentcontrolsystem.service.differenceService;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.*;
import com.logiclab.documentcontrolsystem.domain.DiffType;
import com.logiclab.documentcontrolsystem.dto.response.DiffLineResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DiffAlgorithmService {

    public List<DiffLineResponse> compareTexts(String oldText, String newText) {

        List<String> original = Arrays.asList(oldText.split("\\R", -1));
        List<String> revised = Arrays.asList(newText.split("\\R", -1));

        Patch<String> patch = DiffUtils.diff(original, revised);

        List<DiffLineResponse> result = new ArrayList<>();

        for (AbstractDelta<String> delta : patch.getDeltas()) {

            Chunk<String> source = delta.getSource();
            Chunk<String> target = delta.getTarget();

            switch (delta.getType()) {

                case INSERT:
                    for (String line : target.getLines()) {
                        result.add(createLine(DiffType.ADDED, null, line));
                    }
                    break;

                case DELETE:
                    for (String line : source.getLines()) {
                        result.add(createLine(DiffType.REMOVED, line, null));
                    }
                    break;

                case CHANGE:
                    int max = Math.max(source.getLines().size(), target.getLines().size());

                    for (int i = 0; i < max; i++) {
                        String oldLine = i < source.getLines().size() ? source.getLines().get(i) : null;
                        String newLine = i < target.getLines().size() ? target.getLines().get(i) : null;

                        result.add(createLine(DiffType.MODIFIED, oldLine, newLine));
                    }
                    break;

                default:
                    break;
            }
        }

        return result;
    }

    private DiffLineResponse createLine(DiffType type, String oldLine, String newLine) {
        DiffLineResponse line = new DiffLineResponse();
        line.setType(type);
        line.setOldLine(oldLine);
        line.setNewLine(newLine);
        return line;
    }
}