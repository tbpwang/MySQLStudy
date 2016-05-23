package edu.joe;

import com.mysql.jdbc.Driver;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tbpwang@gmail.com
 *         2016/5/23.
 */
public class DumpIP {
    private static final String URL = "jdbc://mysql://10.4.32.21//db_ip?user=root&password=system";
    private static final String FILE = "data/ip.txt";
    private static final String SQL = "INSERT INTO db_IP.IP(start,end,location,remark)VALUES(?,?,?,?);";
    private static List<IP> ips;

    public static void main(String[] args) {
        parse();
        dump();
    }

    //read txt(start ip, end ip, location, remark)
    static void parse() {
        BufferedReader reader = null;
        FileReader file = null;
        String line;
        long start = 0;
        long end = 0;
        String location, remark;
        String[] startIPSegments, endIPSegments;
        ips = new ArrayList<>();
        try {
            file = new FileReader(FILE);

            reader = new BufferedReader(file);
            while ((line = reader.readLine()) != null) {
                String[] Segments = line.split("\\s+");
                startIPSegments = line.split("\\s+")[0].split("\\.");
                endIPSegments = line.split("\\s+")[1].split("\\.");
                location = line.split("\\s+")[2];
                remark = line.replaceAll(line.split("\\s+")[0] + "\\s+" + line.split("\\s+")[1] + "\\s+" + line.split("\\s+")[2], "").trim();
                for (int i = 0; i < startIPSegments.length; i++) {
                    start += (long) (Long.parseLong(Segments[i]) * Math.pow(256, (3 - i)));
                }
                for (int i = 0; i < endIPSegments.length; i++) {
                    end += (long) (Long.parseLong(Segments[i]) * Math.pow(256, (3 - i)));
                }
                ips.add(new IP(start, end, location, remark));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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


    static void dump() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            new Driver();
            connection = DriverManager.getConnection(URL);
            preparedStatement = connection.prepareStatement(SQL);
            for (IP ip : ips) {
                preparedStatement.setLong(1, ip.getStart());
                preparedStatement.setLong(2, ip.getEnd());
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

    static boolean isIP(String ip) {
        //255.255.255.255-0.0.0.1
        String pattIP = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(pattIP);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}

//IP table(id, start bigint, end bigint, location varchar, remark varchar)
class IP {
    private long start;
    private long end;
    private String location;
    private String remark;

    public IP(long start, long end, String location, String remark) {
        this.start = start;
        this.end = end;
        this.location = location;
        this.remark = remark;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
