package com.akul.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.List;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("manager")
public class Manager extends User {

    private String project_name;

    @Builder
    public Manager(Long id, String username, PersonalInfo personalInfo, String info, Role role, Company company, Profile profile, List<UsersChat> usersChats, String project_name) {
        super(id, username, personalInfo, info, role, company, profile, usersChats);
        this.project_name = project_name;
    }
}
