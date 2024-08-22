package kg.neobis.smarttailor.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EndpointConstants {

    private static final String API_PREFIX = "/api";

    public static final String APP_USER_ENDPOINT = API_PREFIX + "/app-user";

    public static final String AUTH_ENDPOINT = API_PREFIX + "/auth";

    public static final String EMPLOYEE_ENDPOINT = API_PREFIX + "/employee";

    public static final String EQUIPMENT_ENDPOINT = API_PREFIX + "/equipment";

    public static final String ORDER_ENDPOINT = API_PREFIX + "/order";

    public static final String ORGANIZATION_ENDPOINT = API_PREFIX + "/organization";

    public static final String PERSONAL_ACCOUNT_ENDPOINT = API_PREFIX + "/account";

    public static final String POSITION_ENDPOINT = API_PREFIX + "/position";

    public static final String SERVICE_ENDPOINT = API_PREFIX + "/service";
    public static final String SEARCH_ENDPOINT = API_PREFIX + "/search";
    public static final String NOTIFICATION_ENDPOINT = API_PREFIX + "/notification ";




    public static final String[] WHITE_LIST_URL = {
            AUTH_ENDPOINT.concat("/confirm-email"),
            AUTH_ENDPOINT.concat("/login"),
            AUTH_ENDPOINT.concat("/login-admin"),
            AUTH_ENDPOINT.concat("/refresh-token"),
            AUTH_ENDPOINT.concat("/resend-confirmation-code"),
            AUTH_ENDPOINT.concat("/sign-up"),
            APP_USER_ENDPOINT.concat("/confirm-subscription-request**"),
            ORGANIZATION_ENDPOINT.concat("/accept-invitation**"),
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/websocket/**",
            "/app/**",
            "/topic/**",
            "/notification/push"
    };
}