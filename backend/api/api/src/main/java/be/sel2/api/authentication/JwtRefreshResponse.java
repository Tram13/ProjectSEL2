package be.sel2.api.authentication;

import lombok.Getter;
import lombok.Setter;

/**
 * Class used to return the sessionToken upon refreshing the JWT token
 */
@Getter
@Setter
public class JwtRefreshResponse {
    private String sessionToken;

    public JwtRefreshResponse(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public String toString() {
        return "JwtSessionToken{" +
                "sessionToken='" + sessionToken + '\'' +
                '}';
    }
}
