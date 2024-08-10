package com.akul.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static com.akul.util.StringUtil.SPACE;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"company", "profile", "usersChats"})
@EqualsAndHashCode(of = "username")
@Builder
@Entity
@Table(name = "users", schema = "public")
@TypeDef(name = "akul", typeClass = JsonBinaryType.class)
public class User implements Comparable<User>, BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @AttributeOverride(
            name = "birthdate",
            column = @Column(name = "birth_date"))
    private PersonalInfo personalInfo;

    @Type(type = "akul")
    @Column(columnDefinition = "jsonb")
    private String info;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private Profile profile;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UsersChat> usersChats = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receiver")
    private List<Payment> payments = new ArrayList<>();

    @Override
    public int compareTo(User obj) {

        return username.compareTo(obj.username);
    }

    public String fullName() {

        return getPersonalInfo().getFirstname() + SPACE + getPersonalInfo().getLastname();
    }
}
