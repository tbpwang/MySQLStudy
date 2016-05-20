package edu.joe;

import com.mysql.jdbc.Driver;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Joe Wang, tbpwang@gmail.com
 * 2016/5/18.
 */
public class IP {
    private static final String URL = "jdbc:mysql://10.4.32.21:3306";//jdbc:mysql://localhost:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "system";
    private static final String TXT_FILE_PATH = "resource/IP.txt";
    protected static boolean haveInputData;

    public static boolean isHaveInputData() {
        return haveInputData;
    }

    public static void setHaveInputData(boolean haveInputData) {
        IP.haveInputData = haveInputData;
    }

    public static void main(String[] args) {
//        if (!IP.isHaveInputData()) {
//            DataFromTxt.insert();
//        }

        System.out.println("Input IPAdress(*.*.*.*): ");
        Scanner scanner = new Scanner(System.in);
        String in = scanner.nextLine();
        if (!isIPAddress(in)) {
            System.out.println("IP Address is Error! Please Check....");
            System.exit(0);
        }

        DataFromDatabase.query();

        long ipToNumber = ipAddressToNumber(in);

        //(id, fromIP, toIP, position, description)
        String ipSQL = "SELECT position, description FROM ip.ip p,(SELECT id, inet_aton(fromIP) AS start, inet_aton(toIP) AS end FROM ip.ip)t "
                + "WHERE t.id = p.id AND t.start <= ? AND t.end >= ?";
        //"SELECT position, description FROM ip.ip WHERE ? BETWEEN (SELECT inet_aton(fromIP)FROM ip.ip) AND (SELECT inet_aton(toIP)FROM ip.ip )";
        //"SELECT position, description  FROM IP.IP WHERE ? >= fromIP AND ? <= toIP";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            new Driver();
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            preparedStatement = connection.prepareStatement(ipSQL);
            preparedStatement.setLong(1, ipToNumber);
            preparedStatement.setLong(2, ipToNumber);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println("IP is：" + resultSet.getString("position") + " " + resultSet.getString("description"));
            }
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
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private static long ipAddressToNumber(String in) {
        long ip = 0L;
        String[] strs = in.split("\\.");
        for (int i = 0; i < strs.length; i++) {
            System.out.println(strs[i]);
            ip += (long) (Long.parseLong(strs[i]) * Math.pow(256, (3 - i)));
        }
        return ip;
    }

    private static boolean isIPAddress(String ipAddress) {
        //255.255.255.255-0.0.0.1
        String pattIP = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(pattIP);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    private ArrayList<String> readFileTxt(String filePath) {

        BufferedReader reader = null;
        FileReader file = null;
        String line;
        ArrayList<String> list = new ArrayList<>();
        try {
            file = new FileReader(filePath);
            reader = new BufferedReader(file);
            while ((line = reader.readLine()) != null && reader.ready()) {
                if (!line.equals("")) {
                    list.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found: FileReader(TXT_FILE_PATH).");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("readLine() Error!");
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    private String[][] getColumnsValues() {
        ArrayList<String> data = readFileTxt(TXT_FILE_PATH);
        String row;
        String[] columns;
        String[][] columnsValues = new String[data.size()][4];
        //(?, ?, ?, ?, ?);";
        // (id, fromIP, toIP, location, owner)
        for (int rowCount = 0; rowCount < data.size(); rowCount++) {
            row = data.get(rowCount);
            columns = row.split("\\s+");
            if (columns.length <= 4) {
                System.arraycopy(columns, 0, columnsValues[rowCount], 0, columns.length);
            } else {
                System.arraycopy(columns, 0, columnsValues[rowCount], 0, 4);
                String rearStr = " ";
                for (int i = 4; i < columns.length; i++) {
                    rearStr += columns[i] + " ";
                }
                columnsValues[rowCount][3] += rearStr;
            }

        }
        return columnsValues;
    }

    private static class DataFromTxt {
        public static void insert() {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            IP ip = new IP();
            try {
                new Driver();
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                String sql = "INSERT INTO ip.ip VALUES(NULL, ?, ?, ?, ?);";
                //(?, ?, ?, ?, ?);";
                // (id, fromIP, toIP, location, owner)
                String[][] columnsValues = ip.getColumnsValues();
                System.out.println("Total lines are: " + columnsValues.length);

                preparedStatement = connection.prepareStatement(sql);
                for (String[] rows : columnsValues) {
                    for (int colCount = 0; colCount < 4; colCount++) {
                        preparedStatement.setString(colCount + 1, rows[colCount]);
                        //System.out.print(rows[colCount]+ " ");
                    }
                    //System.out.println("");
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                IP.setHaveInputData(true);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
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

    private static class DataFromDatabase {
        public static void query() {

        }
    }
}
