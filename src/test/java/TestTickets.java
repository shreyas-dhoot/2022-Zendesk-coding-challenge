import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TestTickets {

    private final HTTPConnections httpConnections = Mockito.mock(HTTPConnections.class);
    public static final String TEST_URL = "https://zccstudents4228.zendesk.com/api/v2/tickets.json?page=2";

    @Before
    public void init()  throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        JSONParser parser = new JSONParser();

        // Initialize get tickets URL
        InputStream inputStream = classLoader.getResourceAsStream("tickets-payload.json");
        String data = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        JSONObject js = (JSONObject) parser.parse(data);
        Mockito.when(httpConnections.httpGet(URLUtils.TICKETS_URL)).thenReturn(js);

        // Initialize count tickets URL
        inputStream = classLoader.getResourceAsStream("count-tickets.json");
        data = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        js = (JSONObject) parser.parse(data);
        Mockito.when(httpConnections.httpGet(URLUtils.COUNT_TICKETS_URL)).thenReturn(js);

        // Initialize second tickets URL
        inputStream = classLoader.getResourceAsStream("tickets-payload-2.json");
        data = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        js = (JSONObject) parser.parse(data);
        Mockito.when(httpConnections.httpGet(TEST_URL)).thenReturn(js);

        // Initialize second tickets URL
        inputStream = classLoader.getResourceAsStream("tickets-payload-101.json");
        data = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        js = (JSONObject) parser.parse(data);
        Mockito.when(httpConnections.httpGet(URLUtils.fetchPerTicketURL("101"))).thenReturn(js);
    }

    @Test
    public void testFetchTicketAndCount() throws Exception {
        TicketUtils tu = new TicketUtils(httpConnections);
        tu.populateTickets();
        Map<Long, Ticket> tickets = tu.getTickets();
        Assert.assertEquals(101, tickets.size());
        Assert.assertEquals(101, tu.getTicketCount());
        Ticket ticket = tickets.get(101L);
        checkTicket(ticket);
    }

    @Test
    public void testPerTicket() throws Exception {
        TicketUtils tu = new TicketUtils(httpConnections);
        Ticket ticket = tu.fetchPerTicket("101");
        Assert.assertEquals(showFullInfo, ticket.showFullInfo());
        checkTicket(ticket);
    }

    private void checkTicket(final Ticket ticket) {
        Assert.assertEquals("in nostrud occaecat consectetur aliquip", ticket.getSubject());
        Assert.assertEquals("open", ticket.getStatus());
        Assert.assertEquals(421865154992L, (long) ticket.getSubmitter_id());
        Assert.assertEquals(421865154992L, (long) ticket.getAssignee_id());

    }

    final String showFullInfo = "id                  101\n" +
            "created_at          Fri Nov 19 15:50:40 CST 2021\n" +
            "subject             in nostrud occaecat consectetur aliquip\n" +
            "description         Esse esse quis ut esse nisi tempor sunt. Proident officia incididunt cupidatat laborum ipsum duis. Labore qui labore elit consequat.\n" +
            "\n" +
            "Do id nisi qui et fugiat culpa veniam consequat ad amet ut nisi ipsum. Culpa exercitation consectetur adipisicing sunt reprehenderit. Deserunt consequat aliquip tempor anim officia elit proident commodo consequat aute. Magna enim esse tempor incididunt ipsum dolore Lorem cupidatat incididunt.\n" +
            "status              open\n" +
            "assignee_id         421865154992\n" +
            "submitter_id        421865154992\n";

}
