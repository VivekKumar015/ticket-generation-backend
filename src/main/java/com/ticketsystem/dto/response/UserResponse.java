package com.ticketsystem.dto.response;

import java.time.LocalDateTime;

public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDepartment() { return department; }
    public String getRole() { return role; }
    public Boolean getActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UserResponse r = new UserResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder firstName(String v) { r.firstName = v; return this; }
        public Builder lastName(String v) { r.lastName = v; return this; }
        public Builder email(String v) { r.email = v; return this; }
        public Builder phone(String v) { r.phone = v; return this; }
        public Builder department(String v) { r.department = v; return this; }
        public Builder role(String v) { r.role = v; return this; }
        public Builder active(Boolean v) { r.active = v; return this; }
        public Builder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public UserResponse build() { return r; }
    }
}