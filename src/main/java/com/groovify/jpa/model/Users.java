package com.groovify.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    private String name;
    private String password;
    private String image_file_name;
    private String Description;

    // Getters and setters
    public Long getID() { return ID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getImageFileName() { return image_file_name; }
    public void setImageFileName(String image_file_name) { this.image_file_name = image_file_name; }

    public String getDescription() { return Description; }
    public void setDescription(String description) { this.Description = description; }
}
