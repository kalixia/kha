package com.kalixia.ha.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.kalixia.ha.model.security.OAuthTokens;
import com.kalixia.ha.model.security.Role;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@ApiModel("User")
public class User extends AbstractAuditable {
    @ApiModelProperty(value = "the login of the user", required = true)
    private final String username;

    @ApiModelProperty(value = "the password of the user", required = true)
    private String password;

    @ApiModelProperty(value = "the email of the user", required = true)
    private String email;

    @ApiModelProperty(value = "the first name of the user", required = true)
    private String firstName;

    @ApiModelProperty(value = "the last name of the user", required = true)
    private String lastName;

    @ApiModelProperty(value = "the roles of the user (security)", required = true, dataType = "string",
            allowableValues = "ADMINISTRATOR, USER, ANONYMOUS")
    private Set<Role> roles;

    @ApiModelProperty(value = "the OAuth2 set of tokens (access and refresh tokens) of the user", required = true)
    private Set<OAuthTokens> oauthTokens;

    @JsonCreator
    public User(@JsonProperty("username") String username, @JsonProperty("password") String password,
                @JsonProperty("email") String email, @JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName,
                @JsonProperty("roles") Set<Role> roles, @JsonProperty("oauthTokens") Set<OAuthTokens> oauthTokens) {
        this(username, password, email, firstName, lastName, roles, oauthTokens, new DateTime(), new DateTime());
    }

    public User(String username, String password, String email, String firstName, String lastName,
                Set<Role> roles, Set<OAuthTokens> oauthTokens,
                DateTime creationDate, DateTime lastUpdateDate) {
        super(creationDate, lastUpdateDate);
        checkNotNull(username, "The username can't be null");
        checkNotNull(password, "The password can't be null");
        checkNotNull(password, "The password can't be null");
        checkNotNull(email, "The email can't be null");
        checkNotNull(firstName, "The first name can't be null");
        checkNotNull(lastName, "The last name can't be null");
        checkNotNull(roles, "The user roles can't be null");
        checkArgument(roles.size() > 0, "The user must have at least one role");
        if (oauthTokens == null)
            oauthTokens = new HashSet<>();
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.oauthTokens = oauthTokens;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return getFirstName() + ' ' + getLastName();
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public Set<OAuthTokens> getOauthTokens() {
        return oauthTokens;
    }

    public void addOAuthAccessToken(OAuthTokens tokens) {
        oauthTokens.add(tokens);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (!password.equals(user.password)) return false;
        if (!roles.equals(user.roles)) return false;
        if (!username.equals(user.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("username", username)
                .add("email", email)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("roles", roles)
                .toString();
    }
}
