package kg.neobis.smarttailor.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EndpointConstants {

    private static final String API_PREFIX = "/api";

    public static final String API_USER_ENDPOINT = API_PREFIX + "/api-user";

    public static final String AUTH_ENDPOINT = API_PREFIX + "/auth";

    public static final String PERSONAL_ACCOUNT_ENDPOINT = API_PREFIX + "/account";

    public static final String[] WHITE_LIST_URL = {
            API_USER_ENDPOINT.concat("/**"),
            AUTH_ENDPOINT.concat("/confirm-email"),
            AUTH_ENDPOINT.concat("/login"),
            AUTH_ENDPOINT.concat("/login-admin"),
            AUTH_ENDPOINT.concat("/login-admin"),
            AUTH_ENDPOINT.concat("/resend-confirmation-code"),
            AUTH_ENDPOINT.concat("/sign-up"),
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}