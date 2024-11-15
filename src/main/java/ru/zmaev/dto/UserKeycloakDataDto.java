package ru.zmaev.dto;

public class UserKeycloakDataDto {
    private String keycloakUID;
    private String username;
    private String email;

    public UserKeycloakDataDto(String keycloakUID, String username, String email) {
        this.keycloakUID = keycloakUID;
        this.username = username;
        this.email = email;
    }

    public UserKeycloakDataDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKeycloakUID() {
        return keycloakUID;
    }

    public void setKeycloakUID(String keycloakUID) {
        this.keycloakUID = keycloakUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

