package com.geekbrains.server.authorization;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//новый класс для работы с psql
public class JdbcApp {
    private static Connection connection;
    private static Statement statement;
    public static List<UserData> logins;

    public static void main() {
        try {
            connect();
            logins = findAll();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/java3", "postgres", "postgres");
        statement = connection.createStatement();
    }

    private static void createTableEx() throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXIST auth(\n+" +
                "login varchar(64) PRIMARY KEY UNIQUE, \n+" +
                "password varchar(64),\n+" +
                "nickname varchar(64) UNIQUE\n+ " +
                ");");
    }

    private static void dropeTable() throws SQLException {
        statement.executeUpdate("DROP TABLE IF EXIST auth;");
    }

    private static void readEx() throws SQLException {
        try (ResultSet rs = statement.executeQuery("select * from auth ORDER BY login ASC ;")) {
            while (rs.next()) {
                System.out.println(rs.getString("login") + " " + rs.getString("password") + " " +
                        rs.getString("nickname"));
            }
        }
    }


    private static void clearTableEx() throws SQLException {
        statement.executeUpdate("DELETE FROM auth;");
    }

    private static void deleteEx(String nickName) throws SQLException {
        statement.executeUpdate("DELETE FROM auth WHERE nickname = '" + nickName + "' ;");
    }

    public static void updateEx(String lastName, String newName) throws SQLException {
        try {
            connect();
            statement.executeUpdate("UPDATE auth SET nickname = '" + newName + "' WHERE nickname = '" + lastName + "';");
            readEx();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }


    }


    private static void insertEx(String login, String password, String nickName) throws SQLException {
        statement.executeUpdate("INSERT INTO auth (login,password,nickName) \n" +
                "VALUES ('" + login + "','" + password + "','" + nickName + "'); ");
    }

    private static List<UserData> findAll() throws SQLException {
        logins = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery("select * from auth ORDER BY login ASC ;")) {
            while (rs.next()) {
                UserData userData = new UserData(rs.getString(1), rs.getString(2), rs.getString(3));
                userData.info();
                logins.add(userData);
            }
        }

        return logins;
    }

}
