package edu.joe;


import com.mysql.jdbc.Driver;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;


/**
 * Created by Joe Wang, tbpwang@gmail.com
 * 2016/5/18.
 */
public class IP {
    private static final String URL = "jdbc:mysql://localhost:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "system";
    private static final String TXT_FILE_PATH = "resource/IP.txt";

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        IP ip = new IP();
        try {
            new Driver();
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "INSERT INTO ip.ip VALUES(NULL, ?, ?, ?, ?)";
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
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
//                    columns = line.split("[\\s+]");
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
        String[][]columnsValues = new String[data.size()][4];
        //(?, ?, ?, ?, ?);";
        // (id, fromIP, toIP, location, owner)
        for (int rowCount = 0; rowCount < data.size(); rowCount++) {
            row = data.get(rowCount);
            columns = row.split("\\s+");
            if (columns.length <= 4) {
                System.arraycopy(columns, 0, columnsValues[rowCount], 0, columns.length);
            } else {
                System.arraycopy(columns, 0, columnsValues[rowCount], 0, 4);
                String rearStr= " ";
                for (int i = 4; i < columns.length; i++) {
                    rearStr += columns[i] + " ";
                }
                columnsValues[rowCount][3] += rearStr;
            }

        }
        return columnsValues;
    }
}
