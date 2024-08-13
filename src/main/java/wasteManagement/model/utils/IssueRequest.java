package wasteManagement.model.utils;

import lombok.Data;

@Data
public class IssueRequest {
    private String type;
    private String city;
    private String description;
    private Long binId;
    private String username;
}
