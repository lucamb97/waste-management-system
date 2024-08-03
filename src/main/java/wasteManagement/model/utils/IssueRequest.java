package wasteManagement.model.utils;

import lombok.Data;

@Data
public class IssueRequest {
    private String city;
    private String Description;
    private Long binId;
    private String username;
}
