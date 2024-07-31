package com.akul;

import com.akul.entity.Chat;
import com.akul.entity.Company;
import com.akul.entity.Language;
import com.akul.entity.LocaleInfo;
import com.akul.entity.Manager;
import com.akul.entity.PersonalInfo;
import com.akul.entity.Profile;
import com.akul.entity.Programmer;
import com.akul.entity.User;
import com.akul.entity.UsersChat;
import com.akul.util.HibernateTestUtil;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

@Slf4j
class HiberRunTest {

    @Test
    public void checkHQL() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            checkH2();
            String name = "Sveta";
            session.flush();
            var users = session.createQuery(
                            "select u from User u  where u.personalInfo.firstname=?1", User.class)
                    .setParameter(1, name)
                    .list();

            System.out.println(users);


            session.getTransaction().commit();
        }
    }

    @Test
    public void checkH2() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var google = Company.builder()
                    .name("Google")
                    .build();
            session.save(google);

            Programmer programmer = Programmer.builder()
                    .username("ivan@gmail.com")
                    .language(Language.Java)
                    .company(google)
                    .build();
            session.save(programmer);

            Manager manager = Manager.builder()
                    .username("sveta@gmail.com")
                    .project_name("JuniorProjectManager")
                    .personalInfo(PersonalInfo.builder()
                            .firstname("Sveta")
                            .lastname("Harrison")
                            .build())
                    .company(google)
                    .build();
            session.save(manager);
            session.flush();
            session.clear();
            var programmer1 = session.get(Programmer.class, 1L);
            var manager1 = session.get(User.class, 2L);
            System.out.printf("Calling programmer1: %s and manager: %s",
                    programmer1.toString(), manager.toString());
            System.out.println();
            session.getTransaction().commit();
        }
    }

    @Disabled
    @Test
    void addUserToNewCompany() {
        @Cleanup var sessionFactory = HibernateTestUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();
        var company = Company.builder()
                .name("Amazon")
                .build();
        var user1 = Programmer.builder()
                .username("vika@gmail.com")
                .build();
        var user2 = Manager.builder()
                .username("vlad@gmail.com")
                .build();
        company.addUser(user1);
        company.addUser(user2);
        session.save(company);
        session.getTransaction().commit();
    }

    @Test
    void localeInfo() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            var company = Company.builder()
                    .name("Amazon")
                    .build();

            //   var locale1 = LocaleInfo.of("ukr", "Description by ukraine");
            //   var locale2 = LocaleInfo.of("eng", "Description by english");

            company.getLocales().put("ukr", "Description by ukraine");
            company.getLocales().put("eng", "Description by english");
            session.save(company);
            System.out.println();
            session.getTransaction().commit();
        }
    }

    @Disabled
    @Test
    void deleteCompany() {
        @Cleanup var sessionFactory = HibernateTestUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var user = session.get(User.class, 5L);
        session.delete(user);
        session.getTransaction().commit();
    }


    @Test
    void checkOneToMany() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession();) {
            session.beginTransaction();
            var company = session.get(Company.class, 1);
            System.out.println(company);
            session.getTransaction().commit();
        }

    }

    @Test
    public void checkManyToMany() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession();) {
            session.beginTransaction();
            var user = session.get(User.class, 1L);
            var chat = session.get(Chat.class, 1L);

            var usersChat = UsersChat.builder()
//                    .createdAt(Instant.now())
//                    .createdBy(user.getUsername())
                    .build();
            usersChat.setCreatedAt(Instant.now());
            usersChat.setCreatedBy(user.getUsername());
            usersChat.setUser(user);
            usersChat.setChat(chat);
            usersChat.setUser(user);
            usersChat.setChat(chat);
            session.save(usersChat);
            session.getTransaction().commit();
        }
    }

    @Disabled
    @Test
    public void checkDeleteChat() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession();) {
            session.beginTransaction();
            var user = session.get(User.class, 3L);
            user.getUsersChats().clear();
            session.getTransaction().commit();
        }
    }

    @Disabled
    @Test
    void checkOrhanRemoval_DeleteUserFromCompany() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession();
        ) {
            session.beginTransaction();
            Company company = session.get(Company.class, 2);
            company.getUsers().clear();
            session.getTransaction().commit();
        }
    }

    @Disabled
    @Test
    void checkGetReflection_() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.getString("username");

        Class<User> clazz = User.class;

        Constructor<User> constructor = clazz.getConstructor();
        User user = constructor.newInstance();
        Field usernameField = clazz.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(user, resultSet.getString("username"));

    }

    @Disabled
    @Test
    void checkGetReflection() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        SessionFactory sessionFactory = HibernateTestUtil.buildSessionFactory();
        Session session = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            session = sessionFactory.openSession();
            connection = session.doReturningWork(s -> s.unwrap(Connection.class));
            String sql = "SELECT username FROM users WHERE id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, 1L);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Class<User> clazz = User.class;
                Constructor<User> constructor = clazz.getConstructor();
                User user = constructor.newInstance();
                Field usernameField = clazz.getDeclaredField("username");
                usernameField.setAccessible(true);
                usernameField.set(user, resultSet.getString("username"));

                System.out.println("User's username: " + user.getUsername());
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
            if (session != null) session.close();
            sessionFactory.close();
        }
    }

    @Disabled
    @Test
    void checkReflectionApi() throws SQLException, IllegalAccessException {
        User user = Manager.builder()
                .build();

        String sql = """
                insert
                into
                %s
                (%s)
                values
                (%s)
                """;
        String tableName = ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
                .orElse(user.getClass().getName());

        Field[] declaredFields = user.getClass().getDeclaredFields();

        String columnNames = Arrays.stream(declaredFields)
                .map(field -> ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .collect(joining(", "));

        String columnValues = Arrays.stream(declaredFields)
                .map(field -> "?")
                .collect(joining(", "));

        System.out.println(sql.formatted(tableName, columnNames, columnValues));

        Connection connection = null;
        PreparedStatement preparedStatement = connection.prepareStatement(sql.formatted(tableName, columnNames, columnValues));
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            preparedStatement.setObject(1, declaredField.get(user));
        }
    }

    @Disabled
    @Test
    void checkOneToOne() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()
        ) {
            session.beginTransaction();

            var company = Company.builder()
                    .name("Google")
                    .build();

            var profile = Profile.builder()
                    .language("ukr")
                    .street("Voli 54")
                    .build();

            var user = Programmer.builder()
                    .username("vasilij@gmail.com")
                    .info("""
                            {
                               "name": "Vasilij",
                               "age": 18
                            }
                            """)
                    .build();
            profile.setUser(user);
            company.addUser(user);
            session.save(company);
            session.getTransaction().commit();
        }
    }
}