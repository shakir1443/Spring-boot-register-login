/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.login.demo.dao;

/**
 *
 * @author shakir.it
 */
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
 
import com.login.demo.entity.AppUser;
import com.login.demo.validator.AppUserForm;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
 
@Repository
@Transactional
public class AppUserDAO {
 
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
 
 
    public AppUser findUserAccount(String userName) {
        try {
            String sql = "Select e from " + AppUser.class.getName() + " e " //
                    + " Where e.userName = :userName ";
 
            Query query = entityManager.createQuery(sql, AppUser.class);
            query.setParameter("userName", userName);
           
            return (AppUser) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    private static final Map<Long, AppUser> USERS_MAP = new HashMap<>();
//    public AppUser findAppUserByUserName(String userName) {
//        Collection<AppUser> appUsers = USERS_MAP.values();
//        for (AppUser u : appUsers) {
//            if (u.getUserName().equals(userName)) {
//                return u;
//            }
//        }
//        return null;
//    }
    public AppUser findAppUserByUserName(String userName) {
        try {
            String sql = "Select e from " + AppUser.class.getName() + " e " //
                    + " Where e.userName = :userName ";
 
            Query query = entityManager.createQuery(sql, AppUser.class);
            query.setParameter("userName", userName);
           
            return (AppUser) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
//     public Long getMaxUserId() {
//        long max = 0;
//        for (Long id : USERS_MAP.keySet()) {
//            if (id > max) {
//                max = id;
//            }
//        }
//        return max;
//    }
    public BigDecimal getMaxUserId() {
        Query query=entityManager.createNativeQuery("Select max(USER_ID) from APP_USER");
        return (BigDecimal) query.getSingleResult();
    }
    
//    public AppUser createAppUser(AppUserForm form) {
//        Long userId = this.getMaxUserId() + 1;
//        String encrytedPassword = this.passwordEncoder.encode(form.getPassword());
//        System.out.println(encrytedPassword);
//        AppUser user = new AppUser(userId, form.getUserName(),encrytedPassword);
//   
//        USERS_MAP.put(userId, user);
//        System.out.println(USERS_MAP);
//        return user;
//    }
    
//    @Transactional
    public AppUser createAppUser(AppUserForm form)
    {
//         String sql = "insert into APP_USER values(((Select max(USER_ID) from APP_USER)+1),:uname,:pass)";
//         
//         Query query = entityManager.createQuery(sql, AppUser.class);
//         String encrytedPassword = this.passwordEncoder.encode(form.getPassword());
//         query.setParameter("uname", form.getUserName());
//         query.setParameter("pass", encrytedPassword);
//         return (AppUser) query.getSingleResult();
        BigDecimal uid = this.getMaxUserId();
        long userId = uid.longValue() + 1;
        String encrytedPassword = this.passwordEncoder.encode(form.getPassword());
        entityManager.createNativeQuery("INSERT INTO APP_USER (USER_ID, USER_NAME, ENCRYTED_PASSWORD) VALUES (((Select max(USER_ID) from APP_USER)+1),?,?)")
        .setParameter(1, form.getUserName())
        .setParameter(2, encrytedPassword).executeUpdate();
        AppUser user = new AppUser(userId, form.getUserName(),encrytedPassword);
        entityManager.createNativeQuery("INSERT INTO USER_ROLE (ID, USER_ID, ROLE_ID) VALUES (((Select max(ID) from USER_ROLE)+1),?,1)")
        .setParameter(1, userId).executeUpdate();
        return user;
        }
    }
 
