package com.akul;


import com.akul.entity.Birthday;
import com.akul.entity.Company;
import com.akul.entity.LocaleInfo;
import com.akul.entity.PersonalInfo;
import com.akul.entity.Profile;
import com.akul.entity.Programmer;
import com.akul.entity.Role;
import com.akul.entity.User;
import com.akul.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.time.LocalDate;

@Slf4j
public class HibernateRunner {

    public static void main(String[] args) throws SQLException {

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            try (session) {
                Company company = Company.builder()
                        .name("Google")
                        .build();

                Profile profile = Profile.builder()
                        .language("eng")
                        .street("Britenbeach 33")
                        .build();

                company.getLocales().put("ukr", "Description by ukraine");
                company.getLocales().put("eng", "Description by english");


                User user = Programmer.builder()
                        .username("john3@gmail.com")
                        .personalInfo(PersonalInfo.builder()
                                .firstname("Johny")
                                .lastname("Gibsen")
                                .birthDate(new Birthday(LocalDate.of(1972, 12, 9)))
                                .build())
                        .role(Role.USER)
                        .info("""
                                {
                                        "name": "Johny",
                                        "age": 52
                                }
                                """)
                        .build();
                log.info("User entity is in transient state, object: {}", user);
                Transaction transaction = session.beginTransaction();
                log.trace("Transaction is created, {}", transaction);
                user.setProfile(profile);
                user.setCompany(company);
                profile.setUser(user);
                company.addUser(user);
                session.save(company);
                session.flush();
                log.info("Company is in persistent state: {}, session {}", company, session);
                session.clear();
                session.getTransaction().commit();

            }
            log.warn("User is in detached state, session is closed {}", session);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            throw ex;
        }
    }

}
