package edu.joe;

import com.mysql.jdbc.Driver;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author tbpwang@gmail.com
 *         2016/5/23.
 */
public class DumpIP {

    public static void main(String[] args) {
        parse();
        dump();
    }

    //read txt(start ip, end ip, location, remark)
    public static void parse() {
        BufferedReader reader = null;
        FileReader file = null;
        String line;
        long start = 0;
        long end = 0;
        String remark;
//        String[] startIPSegments;
//        String[] endIPSegments;
        Constant.setIps(new ArrayList<>());
        try {
            file = new FileReader(Constant.getDumpFile());

            reader = new BufferedReader(file);
            while ((line = reader.readLine()) != null) {
                if(line.equals("")){
                    break;
                }
//                String[] Segments = line.split("\\s+");
//                startIPSegments = line.split("\\s+")[0].split("\\.");
//                endIPSegments = line.split("\\s+")[1].split("\\.");
//                location = line.split("\\s+")[2];
//                remark = line.replaceAll(line.split("\\s+")[0] + "\\s+" + line.split("\\s+")[1] + "\\s+" + line.split("\\s+")[2], "").trim();
//
//                for (int i = 0; i < startIPSegments.length; i++) {
//                    start += (long) (Long.parseLong(startIPSegments[i]) * Math.pow(256, (3 - i)));
//                }
//                for (int i = 0; i < endIPSegments.length; i++) {
//                    end += (long) (Long.parseLong(endIPSegments[i]) * Math.pow(256, (3 - i)));
//                }
                remark = line.replaceAll(line.split("\\s+")[0] + "\\s+" + line.split("\\s+")[1] + "\\s+" + line.split("\\s+")[2], "").trim();
                Constant.getIps().add(new IP(line.split("\\s+")[0], line.split("\\s+")[1], line.split("\\s+")[2], remark));
            }
        } catch (IOException e) {
            e.printStackTrace();
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
    }


    public static void dump() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            new Driver();
            connection = DriverManager.getConnection(Constant.getURL());
            preparedStatement = connection.prepareStatement(Constant.getInsertSql());
            for (IP ip : Constant.getIps()) {
                preparedStatement.setString(1, ip.getStart());
                preparedStatement.setString(2, ip.getEnd());
                preparedStatement.setString(3, ip.getLocation());
                preparedStatement.setString(4, ip.getRemark());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
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

//IP table(id, start bigint, end bigint, location varchar, remark varchar)
class IP {
    private String start;
    private String end;
    private String location;
    private String remark;

    public IP(String start, String end, String location, String remark) {
        this.start = start;
        this.end = end;
        this.location = location;
        this.remark = remark;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getLocation() {
        return location;
    }

    public String getRemark() {
        return remark;
    }
}
