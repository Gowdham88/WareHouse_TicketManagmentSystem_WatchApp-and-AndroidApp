package com.nokia.tms;

public class AppUrls {


    public static String TEST_URl="http://10.134.5.55:80/TMS/";
    public static String BASE_URL="http://www.tmstrail.somee.com/";

    public static String HOME_URL=TEST_URl+"api/TMS/GetTable?LastHour=";
    public static String NOTIFICATION_URL=TEST_URl+"api/TMS/GetNotify?TicketId=";
    public static String TREND_URL=TEST_URl+"api/TMS/GetTrend?LastHour=";
    public static String DETAILS_URL=TEST_URl+"api/TMS/GetTicketDetails?TicketId=";
    public static String NOTIFICATION_CHECK=TEST_URl+"api/TMS/GetLastTicketId";

    public static String URLCLOSE=TEST_URl+"api/TMS/GetCloseTicketSummary?dateshift=";
    public static String URLOPEN=TEST_URl+"api/TMS/GetOpenTicketSummary?dateshift=";
    public static String URLONGOING=TEST_URl+"api/TMS/GetOngoingTicketSummary?dateshift=";
}
