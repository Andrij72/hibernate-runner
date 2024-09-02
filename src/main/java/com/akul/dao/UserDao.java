package com.akul.dao;

import com.akul.dto.CompanyDto;
import com.akul.entity.Company;
import com.akul.entity.Company_;
import com.akul.entity.Payment;
import com.akul.entity.Payment_;
import com.akul.entity.PersonalInfo_;
import com.akul.entity.User;
import com.akul.entity.User_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import javax.persistence.Tuple;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Returns all employees
     */
    public List<User> findAll(Session session) {
        /*return session.createQuery("select u from User u", User.class)
                .list();*/
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);
        criteria.select(user);
        return session.createQuery(criteria).list();
    }

    /**
     * Returns all employees with the specified firstname
     */
    public List<User> findAllByFirstName(Session session, String firstName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);
        criteria.select(user).where(
                cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.firstname), firstName));
        return session.createQuery(criteria).list();

       /* return session.createQuery("select u from User u where u.personalInfo.firstname = :firstName")
                .setParameter("firstName", firstName)
                .list();*/
    }

    /**
     * Returns the first {limit} employees, ordered by birthdate (in ascending order)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);
        criteria.select(user).orderBy(
                cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.birthdate)));
        return session.createQuery(criteria)
                .setMaxResults(limit)
                .list();

        //    return session.createQuery("select u from User u" +
        //    " order by u.personalInfo.birthdate", User.class)
        //   .setMaxResults(limit)
        //   .setFirstResult(offset)
        //       .list();


    }

    /**
     * Returns all employees of the company with the specified name
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var company = criteria.from(Company.class);

        var users = company.join(Company_.users);

        criteria.select(users).where(
                cb.equal(company.get(Company_.name), companyName)
        );
        return session.createQuery(criteria)
                .list();


        //   return session.createQuery("select u from Company c " +
        //                    "join c.users u " +
        //                    "where c.name = :companyName", User.class)
        //            .setParameter("companyName", companyName)
        //            .list();
    }

    /**
     * Returns all payments received by employees of the company with the specified name,
     * ordered by employee name and then by payment amount
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Payment.class);
        var payment = criteria.from(Payment.class);
        var user = payment.join(Payment_.receiver);
        var company = user.join(User_.company);
        criteria.select(payment).where(
                        cb.equal(company.get(Company_.name), companyName))
                .orderBy(
                        cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.lastname)),
                        cb.asc(payment.get(Payment_.amount)));

        return session.createQuery(criteria)
                .list();
        //    return session.createQuery("select p from Payment p " +
        //                    "join p.receiver r " +
        //                    "join r.company c " +
        //                   "where c.name = :companyName " +
        //                    "order by r.personalInfo.lastname, p.amount ", Payment.class)
        //            .setParameter("companyName", companyName)
        //            .list();
    }

    /**
     * Returns the average salary of an employee with the specified first and lastnames
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(Double.class);

        var payment = criteria.from(Payment.class);
        var user = payment.join(Payment_.receiver);

        List<Predicate> predicates = new ArrayList<>();
        if (firstName != null) {
            predicates.add(cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.firstname), firstName));
        }
        if (lastName != null) {
            predicates.add(cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.lastname), lastName));
        }

        criteria.select(cb.avg(payment.get(Payment_.amount))).where(
                predicates.toArray(Predicate[]::new)
        );

        return session.createQuery(criteria)
                .uniqueResult();


        //   return session.createQuery("select avg(p.amount) from Payment p " +
        //                   "join p.receiver u  " +
        //                   "where u.personalInfo.firstname = :firstName " +
        //                   "and u.personalInfo.lastname = :lastName", Double.class)
        //           .setParameter("firstName", firstName)
        //          .setParameter("lastName", lastName)
    }

    /**
     * Returns for each company: the name, the average salary of all its employees. Companies are ordered by name.
     */
    public List<CompanyDto> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(CompanyDto.class);
        var company = criteria.from(Company.class);
        var user = company.join(Company_.users, JoinType.INNER);
        var payment = user.join(User_.payments);

        criteria.select(
                        cb.construct(CompanyDto.class,
                                company.get(Company_.name),
                                cb.avg(payment.get(Payment_.amount)))
                )
                .groupBy(company.get(Company_.name))
                .orderBy(cb.asc(company.get(Company_.name)));

        return session.createQuery(criteria)
                .list();

        // return session.createQuery("select c.name, avg(p.amount) from Company c " +
        //               "join c.users u " +
        //                "join u.payments p " +
        //               "group by c.name " +
        //                "order by c.name", Object[].class)
        //        .list();
    }

    /**
     * Returns a list: employee (User object), average payment amount, but only for those employees whose average payment amount
     * is greater than the average payment amount of all employees.
     * Ordered by employee name
     */
    public List<Tuple> isItPossible(Session session) {
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(Tuple.class);
        var user = criteria.from(User.class);
        var payment = user.join(User_.payments);
        var subquery = criteria.subquery(Double.class);
        var paymentSubquery = subquery.from(Payment.class);
        criteria.select(
                        cb.tuple(
                                user,
                                cb.avg(payment.get(Payment_.amount))
                        )
                )
                .groupBy(user.get(User_.id))
                .having(cb.gt(
                        cb.avg(payment.get(Payment_.amount)),
                        subquery.select(cb.avg(paymentSubquery.get(Payment_.amount)))
                ))
                .orderBy(cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.firstname)));

        return session.createQuery(criteria)
                .list();
        //     return session.createQuery(
        //                   " select u, AVG(p.amount)  from User u  join u.payments p " +//                         "group by u " +
        //                       "having AVG(p.amount) > (select AVG(pp.amount) from Payment pp) " +
        //                     "order by u.personalInfo.firstname", Object[].class)
        //   .list();
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}
