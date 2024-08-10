package com.akul.util;

import com.akul.converter.BirthdayConverter;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy;
import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@UtilityClass
public class HibernateUtil {
    public static SessionFactory buildSessionFactory() {
        Configuration configuration = buildConfiguration();
        configuration.configure(getHibernateConfigFileName());

        return configuration.buildSessionFactory();
    }

    private static String getHibernateConfigFileName() {
        return System.getProperty("hibernate.config", "hibernate-prod.cfg.xml");
    }

    public static Configuration buildConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setPhysicalNamingStrategy(new CamelCaseToSnakeCaseNamingStrategy());
        configuration.addAttributeConverter(new BirthdayConverter(), true);
        configuration.registerTypeOverride(new JsonBinaryType());
        return configuration;
    }
}
