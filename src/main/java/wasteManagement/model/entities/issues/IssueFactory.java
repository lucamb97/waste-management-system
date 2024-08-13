package wasteManagement.model.entities.issues;

import org.springframework.stereotype.Component;

@Component
public class IssueFactory {

    public Issue createIssue(String issueType) {
        return switch (issueType.toUpperCase()) {
            case "BROKEN_BIN" -> new BrokenBinIssue();
            case "MISSING_BIN" -> new MissingBinIssue();
            case "NEED_EMPTY" -> new NeedEmergencyEmptyIssue();
            case "NEED_REMOVAL" -> new NeedRemovalIssue();
            default -> throw new IllegalArgumentException("Unknown issue type: " + issueType);
        };
    }
}
