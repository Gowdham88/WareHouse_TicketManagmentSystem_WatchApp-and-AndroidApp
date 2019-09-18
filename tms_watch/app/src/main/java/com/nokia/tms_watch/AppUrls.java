package com.nokia.tms_watch;

public class AppUrls {

    public static String BASE_TEST2="http://192.168.0.127:90/";
    public static String BASE_TEST="http://www.tmstrail.somee.com/";

    public static String BASE_TEST1="http://10.134.5.55:80/TMS/";

    public static String NOTIFICATION_URL=BASE_TEST+"api/TMS/GetNotify?TicketId=";
    public static String DETAIL_URL=BASE_TEST+"api/TMS/GetTicketDetails?TicketId=";
    public static String HOME_URL=BASE_TEST+"api/TMS/GetTable?LastHour=1000";
    public static String NOTIFICATION_CHECK=BASE_TEST+"api/TMS/GetLastTicketId";
    
}
