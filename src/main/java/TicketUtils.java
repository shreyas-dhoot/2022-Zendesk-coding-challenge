import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/***
 * Contains the state of the application. All the information fetched from the Zendesk server is stored in memory in this class.
 */
@Getter
@Setter
public class TicketUtils {

    private final Map<Long, Ticket> tickets;
    private long ticketCount;
    private HTTPConnections httpConnections;


    public TicketUtils(final String username,
                       final String password) {
        tickets = new HashMap<>();
        httpConnections = new HTTPConnections(username, password);
    }

    @VisibleForTesting
    public TicketUtils(final HTTPConnections httpConnections) {
        tickets = new HashMap<>();
        this.httpConnections = httpConnections;
    }

    public void populateTickets() throws Exception {
        tickets.clear();
        //Fetch tickets and populate the map
        fetchTickets();
    }

    /**
     * Used to fetch all the tickets. As it is already paginated (100 records per API Call), we keep firing the URL,
     * till next_page is not present.
     * @throws APIFailedException - Instead of stack trace, a user-friendly message should be shown
     */
    public void fetchTickets() throws Exception {
        String url = URLUtils.TICKETS_URL;
        do {
            JSONObject fullPayload = httpConnections.httpGet(url);
            if (fullPayload == null) {
                throw new APIFailedException("No payload received");
            }
            JSONArray jsonArray = (JSONArray) fullPayload.get("tickets");
            Gson gson = new Gson();
            for (Object obj : jsonArray) {
                if (obj instanceof JSONObject) {
                    Ticket ticket = gson.fromJson(obj.toString(), Ticket.class);
                    tickets.put(ticket.getId(), ticket);
                } else {
                    //Ignore this object
                }
            }
            url = (String) fullPayload.get("next_page");
            ticketCount = (long) fullPayload.get("count");
        } while (url != null);
    }

    /**
     * Used to fetch only the count of the tickets.
     * @throws Exception
     */
    public void fetchCountOfTickets() throws Exception {
        JSONObject jsonCount = (JSONObject) httpConnections.httpGet(URLUtils.COUNT_TICKETS_URL).get("count");
        ticketCount = (long) jsonCount.get("value");
    }

    /**
     * Fetch a particular ticket using it's id.
     * @param id - Fetch information for a particular ticket id
     * @return - Parse all the information from json to ticket
     * @throws Exception
     */
    public Ticket fetchPerTicket(String id) throws Exception {
        JSONObject jsonObj = (JSONObject) httpConnections.httpGet(URLUtils.fetchPerTicketURL(id)).get("ticket");
        if (jsonObj == null) {
            throw new APIFailedException("No payload found");
        }
        Gson gson= new Gson();
        return gson.fromJson(jsonObj.toString(), Ticket.class);
    }

    public void displayPerTicket(final Ticket ticket) {
        CMDStrings.displaySubsetOfTickets(Collections.singletonList(ticket), 0, 1, true);
    }

    public void displayTickets() {
        CMDStrings.displayTickets(new ArrayList<>(tickets.values()));
    }

}
