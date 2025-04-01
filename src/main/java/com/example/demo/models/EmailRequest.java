package com.example.demo.models;

public class EmailRequest {
    private String name;
    private String email;
    private String phone; // ✅ Add this field
    private String subject;
    private String message;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; } // ✅ Getter
    public void setPhone(String phone) { this.phone = phone; } // ✅ Setter

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
