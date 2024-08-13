package wasteManagement.configuration.utils;

import java.util.List;
import java.util.Arrays;

public class Constants {
    //max number of stops for a certain worker route
    //min 5 or tests will fail
    public static final int MAX_ROUTE_STOPS = 10;
    //List of auth roles allowed
    public static final List<String> ALLOWED_ROLES = Arrays.asList("WORKER","USER","ADMIN");
}
