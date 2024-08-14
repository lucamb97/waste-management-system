package wasteManagement.model.entities.issues;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.repositorys.BinsRepository;

@Component
public class IssueFactory {
    @Autowired
    private BinsRepository binsRepository;

    public Issue createIssue(String issueType, Long binId) {
        Bin bin;
        //Handle different issues differently
        return switch (issueType.toUpperCase()) {
            case "BROKEN_BIN" -> {
                bin = binsRepository.findById(binId).orElseThrow(() -> new IllegalArgumentException("Invalid bin ID: " + binId));
                bin.setStatus("BROKEN");
                binsRepository.save(bin);
                yield new BrokenBinIssue();
            }
            case "MISSING_BIN" -> {
                bin = binsRepository.findById(binId).orElseThrow(() -> new IllegalArgumentException("Invalid bin ID: " + binId));
                bin.setStatus("MISSING");
                binsRepository.save(bin);
                yield new MissingBinIssue();
            }
            case "NEED_EMPTY" -> new NeedEmergencyEmptyIssue();
            case "NEED_REMOVAL" -> new NeedRemovalIssue();
            default -> throw new IllegalArgumentException("Unknown issue type: " + issueType);
        };
    }
}
