package com.akul.util;

import com.akul.entity.Profile;
import com.akul.entity.User;
import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.akul.util.HibernateUtil.buildConfiguration;

@UtilityClass
public class HibernateTestUtil {

    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    static {
        postgres.start();
    }

    public static SessionFactory buildSessionFactory() {
        Configuration configuration = buildConfiguration();
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Profile.class);
        configuration.configure(getHibernateConfigFileName());

        return configuration.buildSessionFactory();
    }

    private static String getHibernateConfigFileName() {
        return System.getProperty("hibernate.config", "hibernate-test.cfg.xml");
    }

}
