package com.akul.dao;

import com.akul.entity.Payment;
import com.akul.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Returns all employees
     */
    public List<User> findAll(Session session) {
        return session.createQuery("select u from User u", User.class)
                .list();
    }

    /**
     * Returns all employees with the specified firstname
     */
    public List<User> findAllByFirstName(Session session, String firstName) {
        return session.createQuery("select u from User u where u.personalInfo.firstname = :firstName")
                .setParameter("firstName", firstName)
                .list();
    }

    /**
     * Returns the first {limit} employees, ordered by birthdate (in ascending order)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        return session.createQuery("select u from User u" +
                        " order by u.personalInfo.birthdate", User.class)
                .setMaxResults(limit)
                // .setFirstResult(offset)
                .list();
        }

        /**
         * Returns all employees of the company with the specified name
         */
        public List<User> findAllByCompanyName (Session session, String companyName){
            return session.createQuery("select u from Company c " +
                            "join c.users u " +
                            "where c.name = :companyName", User.class)
                    .setParameter("companyName", companyName)
                    .list();
        }

        /**
         * Returns all payments received by employees of the company with the specified name,
         * ordered by employee name and then by payment amount
         */
        public List<Payment> findAllPaymentsByCompanyName (Session session, String companyName){

            return session.createQuery("select p from Payment p " +
                            "join p.receiver r " +
                            "join r.company c " +
                            "where c.name = :companyName " +
                            "order by r.personalInfo.lastname, p.amount ", Payment.class)
                    .setParameter("companyName", companyName)
                    .list();
        }

        /**
         * Returns the average salary of an employee with the specified first and lastnames
         */
        public Double findAveragePaymentAmountByFirstAndLastNames (Session session, String firstName, String lastName){
            return session.createQuery("select avg(p.amount) from Payment p " +
                            "join p.receiver u  " +
                            "where u.personalInfo.firstname = :firstName " +
                            "and u.personalInfo.lastname = :lastName", Double.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .uniqueResult();
        }

        /**
         * Returns for each company: the name, the average salary of all its employees. Companies are ordered by name.
         */
        public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName (Session session){
            return session.createQuery("select c.name, avg(p.amount) from Company c " +
                            "join c.users u " +
                            "join u.payments p " +
                            "group by c.name " +
                            "order by c.name", Object[].class)
                    .list();
        }

        /**
         * Returns a list: employee (User object), average payment amount, but only for those employees whose average payment amount
         * is greater than the average payment amount of all employees.
         * Ordered by employee name
         */
        public List<Object[]> isItPossible (Session session){
            return session.createQuery(
                            " select u, AVG(p.amount)  from User u  join u.payments p " +
                                    "group by u " +
                                    "having AVG(p.amount) > (select AVG(pp.amount) from Payment pp) " +
                                    "order by u.personalInfo.firstname", Object[].class)
                    .list();
        }

        public static UserDao getInstance () {
            return INSTANCE;
        }
    }
