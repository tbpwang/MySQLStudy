package edu.joe;

import com.mysql.jdbc.Driver;

import java.sql.*;
import java.util.Scanner;

/**
 * Created by tbpwang@gmail.com
 * 2016/5/23.
 */
public class QueryIP {

    public static void main(String[] args) {
        System.out.println("Input Your IP[0.0.0.0]: ");
        Scanner scanner = new Scanner(System.in);
        String ip = scanner.nextLine();
        if (!Constant.isIP(ip)) {
            System.out.println("IP is not correct!");
            System.exit(0);
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            new Driver();
            connection = DriverManager.getConnection(Constant.getURL());
            preparedStatement = connection.prepareStatement(Constant.getSelectSql());
            preparedStatement.setString(1, ip);
            preparedStatement.setString(2, ip);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            System.out.println("IP address is belonged to: " + resultSet.getString(1) + " " + resultSet.getString(2));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
