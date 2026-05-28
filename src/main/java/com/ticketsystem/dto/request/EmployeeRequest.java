package com.ticketsystem.dto.request;

public class EmployeeRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String department;
    private String role;
    private Boolean active;

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getDepartment() { return department; }
    public String getRole() { return role; }
    public Boolean getActive() { return active; }
    public void setFirstName(String v) { this.firstName = v; }
    public void setLastName(String v) { this.lastName = v; }
    public void setEmail(String v) { this.email = v; }
    public void setPassword(String v) { this.password = v; }
    public void setPhone(String v) { this.phone = v; }
    public void setDepartment(String v) { this.department = v; }
    public void setRole(String v) { this.role = v; }
    public void setActive(Boolean v) { this.active = v; }
}