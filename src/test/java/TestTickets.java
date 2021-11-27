import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TestTickets {

    private final HTTPConnections httpConnections = Mockito.mock(HTTPConnections.class);
    public static final String TEST_URL = "https://zccstudents4228.zendesk.com/api/v2/tickets.json?page=2";

    // Used to check command line outputs
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream sysInBackup = System.in;


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

        //Check command line strings
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testFetchTicketAndCount() throws Exception {
        //Simulate user input. When it is shown page wise, the user inputs n and then exit. Therefore 2 pages are shown
        ByteArrayInputStream in = new ByteArrayInputStream("n\nexit\n".getBytes());
        System.setIn(in);
        TicketUtils tu = new TicketUtils(httpConnections);
        tu.populateTickets();
        tu.displayTickets();
        Assert.assertEquals("Check output after all tickets are printed", allTicketsWithOptions, outContent.toString());
        Map<Long, Ticket> tickets = tu.getTickets();
        Assert.assertEquals("Check count of tickets",101, tickets.size());
        Assert.assertEquals("Check count of tickets", 101, tu.getTicketCount());
        Ticket ticket = tickets.get(101L);
        //Check random ticket
        checkTicket(ticket);
    }

    @Test
    public void testWrongInput() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream("abcd\nexit\n".getBytes());
        System.setIn(in);
        TicketUtils tu = new TicketUtils(httpConnections);
        tu.populateTickets();
        tu.displayTickets();
        Assert.assertEquals("Check wrong input", wrongInput, outContent.toString());
    }

    @Test
    public void testPerTicket() throws Exception {
        TicketUtils tu = new TicketUtils(httpConnections);
        Ticket ticket = tu.fetchPerTicket("101");
        Assert.assertEquals(showFullInfo, ticket.showFullInfo());
        tu.displayPerTicket(ticket);
        Assert.assertEquals(ticketPerUserCMD, outContent.toString());
        checkTicket(ticket);
    }

    private void checkTicket(final Ticket ticket) {
        Assert.assertEquals("in nostrud occaecat consectetur aliquip", ticket.getSubject());
        Assert.assertEquals("open", ticket.getStatus());
        Assert.assertEquals(421865154992L, (long) ticket.getSubmitter_id());
        Assert.assertEquals(421865154992L, (long) ticket.getAssignee_id());

    }

    @After
    public void exit() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(sysInBackup);
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

    final String ticketPerUserCMD = "id                  101\n" +
            "created_at          Fri Nov 19 15:50:40 CST 2021\n" +
            "subject             in nostrud occaecat consectetur aliquip\n" +
            "description         Esse esse quis ut esse nisi tempor sunt. Proident officia incididunt cupidatat laborum ipsum duis. Labore qui labore elit consequat.\n" +
            "\n" +
            "Do id nisi qui et fugiat culpa veniam consequat ad amet ut nisi ipsum. Culpa exercitation consectetur adipisicing sunt reprehenderit. Deserunt consequat aliquip tempor anim officia elit proident commodo consequat aute. Magna enim esse tempor incididunt ipsum dolore Lorem cupidatat incididunt.\n" +
            "status              open\n" +
            "assignee_id         421865154992\n" +
            "submitter_id        421865154992\n" +
            "\n" +
            "-----------------------------------------\n" +
            "\n";

    final String wrongInput = "Total records: 101\n" +
            "\n" +
            "Ticket with id 1 subject \"Sample ticket: Meet the ticket\" submitted by \"421865154992\" on \"Fri Nov 19 15:12:53 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 2 subject \"velit eiusmod reprehenderit officia cupidatat\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:44 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 3 subject \"excepteur laborum ex occaecat Lorem\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:44 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 4 subject \"ad sunt qui aute ullamco\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:45 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 5 subject \"aliquip mollit quis laborum incididunt\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:45 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 6 subject \"nisi aliquip ipsum nostrud amet\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:46 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 7 subject \"cillum quis nostrud labore amet\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:47 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 8 subject \"proident est nisi non irure\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:47 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 9 subject \"veniam ea eu minim aute\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:48 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 10 subject \"magna reprehenderit nisi est cillum\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:48 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 11 subject \"quis veniam ad sunt non\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:49 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 12 subject \"tempor aliquip sint dolore incididunt\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:49 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 13 subject \"labore pariatur ut laboris laboris\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:50 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 14 subject \"officia mollit aliqua eu nostrud\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:50 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 15 subject \"do incididunt incididunt quis anim\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:51 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 16 subject \"tempor magna anim ea id\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:51 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 17 subject \"exercitation sit incididunt magna laboris\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:52 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 18 subject \"laborum ea ut in cupidatat\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:52 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 19 subject \"est fugiat labore pariatur esse\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:53 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 20 subject \"commodo sint laboris est et\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:53 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 21 subject \"laboris sint Lorem ex Lorem\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:54 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 22 subject \"esse adipisicing consectetur sunt tempor\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:55 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 23 subject \"sunt enim pariatur id id\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:55 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 24 subject \"et ad ut enim labore\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:56 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 25 subject \"voluptate dolor deserunt ea deserunt\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:56 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Records shown from 1 to 25\n" +
            "Select an option\n" +
            "\t\t* Press n to view next page\n" +
            "\t\t* Type 'exit' to go to the main menu\n" +
            "\t\t* Type 'quit' to close the application\n" +
            "\n" +
            "Wrong Input\n" +
            "\n" +
            "Select an option\n" +
            "\t\t* Press n to view next page\n" +
            "\t\t* Type 'exit' to go to the main menu\n" +
            "\t\t* Type 'quit' to close the application\n" +
            "\n";

    final String allTicketsWithOptions = "Total records: 101\n" +
            "\n" +
            "Ticket with id 1 subject \"Sample ticket: Meet the ticket\" submitted by \"421865154992\" on \"Fri Nov 19 15:12:53 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 2 subject \"velit eiusmod reprehenderit officia cupidatat\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:44 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 3 subject \"excepteur laborum ex occaecat Lorem\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:44 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 4 subject \"ad sunt qui aute ullamco\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:45 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 5 subject \"aliquip mollit quis laborum incididunt\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:45 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 6 subject \"nisi aliquip ipsum nostrud amet\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:46 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 7 subject \"cillum quis nostrud labore amet\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:47 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 8 subject \"proident est nisi non irure\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:47 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 9 subject \"veniam ea eu minim aute\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:48 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 10 subject \"magna reprehenderit nisi est cillum\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:48 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 11 subject \"quis veniam ad sunt non\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:49 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 12 subject \"tempor aliquip sint dolore incididunt\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:49 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 13 subject \"labore pariatur ut laboris laboris\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:50 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 14 subject \"officia mollit aliqua eu nostrud\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:50 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 15 subject \"do incididunt incididunt quis anim\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:51 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 16 subject \"tempor magna anim ea id\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:51 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 17 subject \"exercitation sit incididunt magna laboris\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:52 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 18 subject \"laborum ea ut in cupidatat\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:52 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 19 subject \"est fugiat labore pariatur esse\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:53 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 20 subject \"commodo sint laboris est et\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:53 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 21 subject \"laboris sint Lorem ex Lorem\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:54 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 22 subject \"esse adipisicing consectetur sunt tempor\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:55 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 23 subject \"sunt enim pariatur id id\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:55 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 24 subject \"et ad ut enim labore\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:56 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 25 subject \"voluptate dolor deserunt ea deserunt\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:56 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Records shown from 1 to 25\n" +
            "Select an option\n" +
            "\t\t* Press n to view next page\n" +
            "\t\t* Type 'exit' to go to the main menu\n" +
            "\t\t* Type 'quit' to close the application\n" +
            "\n" +
            "Ticket with id 26 subject \"in labore quis mollit mollit\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:57 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 27 subject \"ut magna eiusmod magna nostrud\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:57 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 28 subject \"magna consequat ut ullamco magna\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:58 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 29 subject \"irure pariatur aliquip dolore esse\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:58 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 30 subject \"officia esse nostrud est exercitation\" submitted by \"421865154992\" on \"Fri Nov 19 15:49:59 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 31 subject \"aute ipsum sint exercitation labore\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:00 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 32 subject \"velit irure elit incididunt non\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:00 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 33 subject \"fugiat non aliqua irure aliquip\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:01 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 34 subject \"proident esse ut velit labore\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:01 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 35 subject \"laboris et proident qui enim\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:02 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 36 subject \"in id consequat dolore enim\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:02 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 37 subject \"enim duis deserunt ipsum ad\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:03 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 38 subject \"ipsum ex id minim eu\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:03 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 39 subject \"incididunt mollit pariatur esse esse\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:04 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 40 subject \"sit pariatur nisi reprehenderit sit\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:05 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 41 subject \"amet ipsum amet laborum sit\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:06 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 42 subject \"ut anim tempor voluptate deserunt\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:07 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 43 subject \"eu id magna aute occaecat\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:07 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 44 subject \"velit in sit deserunt id\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:08 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 45 subject \"proident esse laboris officia pariatur\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:08 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 46 subject \"irure esse irure qui dolore\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:09 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 47 subject \"officia voluptate sit sunt pariatur\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:09 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 48 subject \"sunt dolore excepteur laborum magna\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:10 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 49 subject \"qui voluptate culpa do tempor\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:10 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Ticket with id 50 subject \"officia magna velit nostrud ullamco\" submitted by \"421865154992\" on \"Fri Nov 19 15:50:11 CST 2021\"\n" +
            "-----------------------------------------\n" +
            "\n" +
            "Records shown from 26 to 50\n" +
            "Select an option\n" +
            "\t\t* Press n to view next page\n" +
            "\t\t* Press p to view previous page\n" +
            "\t\t* Type 'exit' to go to the main menu\n" +
            "\t\t* Type 'quit' to close the application\n" +
            "\n";

}
