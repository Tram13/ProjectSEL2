package be.sel2.api.authentication;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * DTO used to request a new session token
 */
@Getter
@Setter
public class JwtRefreshRequest {
    private @NotNull String refreshToken;

    public JwtRefreshRequest() {

    }

    public JwtRefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "JwtRefreshRequest{" +
                "refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
