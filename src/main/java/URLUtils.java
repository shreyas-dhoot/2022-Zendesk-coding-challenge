public class URLUtils {
    public static final String BASE_URL = "https://zccstudents4228.zendesk.com";
    public static final String COUNT_TICKETS_URL = BASE_URL+"/api/v2/tickets/count";
    public static final String TICKETS_URL = BASE_URL+"/api/v2/tickets";


//    public  String countTicketURL() {
//        return BASE_URL+COUNT_TICKETS_URL;
//    }
//
    public static String fetchPerTicketURL(String id) {
        return TICKETS_URL+"/"+id;
    }
}
