package wasteManagement.model.entitys;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationRequest {

    private String username;
    private String password;
    private String role;
}
