package com.example.restservice.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.example.restservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbcDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    class UserRowMapper implements RowMapper<User>{
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(rs.getInt("userId"));
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            user.setMail(rs.getString("mail"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            return user;
        }

    }

    public List<User> findAll() {
        return jdbcTemplate.query("select * from user", new UserRowMapper());
    }

    public User findById(int userId) {
        return jdbcTemplate.queryForObject(
                "select * from user where userId=?",
                new Object[] { userId },
                new BeanPropertyRowMapper<User>(User.class));
    }

    public User login(String mail, String password) {
        return jdbcTemplate.queryForObject(
                "select * from user where mail=? AND password=?",
                new Object[] { mail,password },
                new BeanPropertyRowMapper<User>(User.class));
    }

    public int deleteById(int userId) {
        return jdbcTemplate.update("delete from user where userId=?", userId);
    }

    public int insert(User user) {
        return jdbcTemplate.update(
                "insert into user (userId, firstName, lastName, password, mail, role) "
                        + "values(?, ?, ?, ?, ?, ?)",
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPassword(),
                user.getMail(),
                user.getRole());
    }

    public int update(User user) {
        return jdbcTemplate.update("update user " + " set firstName = ?, lastName = ?, password = ?, mail = ?, role = ? " + " where userId = ?",
                user.getFirstName(),
                user.getLastName(),
                user.getPassword(),
                user.getMail(),
                user.getMail());
    }

}