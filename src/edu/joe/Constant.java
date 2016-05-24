package edu.joe;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tbpwang@gmail.com
 * 2016/5/23.
 */
public class Constant {
    private static final String URL = "jdbc:mysql://localhost:3306/db_ip?user=root&password=system";//"jdbc:mysql://10.4.32.21:3306/db_ip?user=root&password=system";
    private static final String SELECT_SQL = "SELECT location, remark FROM DB_IP.IP WHERE INET_ATON( ? ) >= start AND INET_ATON( ? ) <= end";
    private static final String DUMP_FILE = "data/ip.txt";
    private static final String INSERT_SQL = "INSERT INTO db_IP.IP(start,end,location,remark)VALUES(?,?,?,?)";
    private static List<IP> ips;

    public static String getDumpFile() {
        return DUMP_FILE;
    }

    public static String getURL() {
        return URL;
    }

    public static String getSelectSql() {
        return SELECT_SQL;
    }

    public static String getInsertSql() {
        return INSERT_SQL;
    }

    public static List<IP> getIps() {
        return ips;
    }

    public static void setIps(List<IP> ips) {
        Constant.ips = ips;
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
