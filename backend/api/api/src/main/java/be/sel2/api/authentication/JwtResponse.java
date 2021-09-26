package be.sel2.api.authentication;

import lombok.Getter;
import lombok.Setter;

/**
 * Class used to return the sessionToken and refreshToken upon login
 */
@Getter
@Setter
public class JwtResponse {

    private String sessionToken;
    private String refreshToken;

    public JwtResponse() {
    }

    public JwtResponse(String sessionToken, String refreshToken) {
        this.sessionToken = sessionToken;
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "sessionToken='" + sessionToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
