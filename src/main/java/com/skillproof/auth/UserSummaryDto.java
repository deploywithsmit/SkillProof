package com.skillproof.auth;

import com.skillproof.user.Role;
import com.skillproof.user.User;
import com.skillproof.user.UserStatus;

public class UserSummaryDto {
    private Long id;
    private String email;
    private String username;
    private String fullName;
    private Role role;
    private UserStatus status;
    private String avatarUrl;

    public UserSummaryDto() {}

    public UserSummaryDto(User user) {
        if (user != null) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.fullName = user.getFullName();
            this.role = user.getRole();
            this.status = user.getStatus();
            this.avatarUrl = user.getAvatarUrl();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
