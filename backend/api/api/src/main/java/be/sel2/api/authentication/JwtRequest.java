package be.sel2.api.authentication;

import javax.validation.constraints.NotNull;

/**
 * Dto used to request a JWT in login
 */
public class JwtRequest {

    private @NotNull String email;
    private @NotNull String password;

    public JwtRequest() {

    }

    public JwtRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "JwtRequest{" +
                "username='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
