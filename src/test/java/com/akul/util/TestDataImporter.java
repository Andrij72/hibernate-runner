package com.akul.util;

import com.akul.entity.Company;
import com.akul.entity.Payment;
import com.akul.entity.PersonalInfo;
import com.akul.entity.User;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.time.Month;

@UtilityClass
public class TestDataImporter {

    public void importData(SessionFactory sessionFactory) {
        @Cleanup Session session = sessionFactory.openSession();

        Company microsoft = saveCompany(session, "Microsoft");
        Company apple = saveCompany(session, "Apple");
        Company google = saveCompany(session, "Google");

        User tarasShevchenko = saveUser(session, "Taras", "Shevchenko",
                LocalDate.of(1955, Month.OCTOBER, 28), microsoft);
        User andreMorua = saveUser(session, "Andre", "Morua",
                LocalDate.of(1955, Month.FEBRUARY, 24), apple);
        User norrisOstin = saveUser(session, "Norris", "Ostin",
                LocalDate.of(1973, Month.AUGUST, 21), google);
        User timKriss = saveUser(session, "Tim", "Kriss",
                LocalDate.of(1960, Month.NOVEMBER, 1), apple);
        User linaKostenko = saveUser(session, "Lina", "Kostenko",
                LocalDate.of(1955, Month.JANUARY, 1), google);

        savePayment(session, tarasShevchenko, 100);
        savePayment(session, tarasShevchenko, 300);
        savePayment(session, tarasShevchenko, 500);

        savePayment(session, andreMorua, 250);
        savePayment(session, andreMorua, 600);
        savePayment(session, andreMorua, 500);

        savePayment(session, timKriss, 400);
        savePayment(session, timKriss, 300);

        savePayment(session, norrisOstin, 500);
        savePayment(session, norrisOstin, 500);
        savePayment(session, norrisOstin, 500);

        savePayment(session, linaKostenko, 300);
        savePayment(session, linaKostenko, 300);
        savePayment(session, linaKostenko, 300);
    }

    private Company saveCompany(Session session, String name) {
        Company company = Company.builder()
                .name(name)
                .build();
        session.save(company);

        return company;
    }

    private User saveUser(Session session,
                          String firstName,
                          String lastName,
                          LocalDate birthday,
                          Company company) {
        User user = User.builder()
                .username(firstName + lastName)
                .personalInfo(PersonalInfo.builder()
                        .firstname(firstName)
                        .lastname(lastName)
                        .birthdate(birthday)
                        .build())
                .company(company)
                .build();
        session.save(user);

        return user;
    }

    private void savePayment(Session session, User user, Integer amount) {
        Payment payment = Payment.builder()
                .receiver(user)
                .amount(amount)
                .build();
        session.save(payment);
    }
}
