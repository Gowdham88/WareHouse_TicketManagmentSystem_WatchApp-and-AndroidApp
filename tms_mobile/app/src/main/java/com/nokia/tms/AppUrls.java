package com.nokia.tms;

public class AppUrls {


    public static String BASE_URL="http://10.134.5.55:80/TMS/";
    public static String TEST_URl="http://www.tmstrail.somee.com/";

    public static String HOME_URL=TEST_URl+"api/TMS/GetTable?LastHour=";
    public static String NOTIFICATION_URL=TEST_URl+"api/TMS/GetNotify?TicketId=";
    public static String TREND_URL=TEST_URl+"api/TMS/GetTrend?LastHour=";
    public static String DETAILS_URL=TEST_URl+"api/TMS/GetTicketDetails?TicketId=";
    public static String NOTIFICATION_CHECK=TEST_URl+"api/TMS/GetLastTicketId";

}
