import java.net.UnknownHostException;
import java.util.Scanner;

public class Start {

    public static void main(String[] args) throws Exception{ //Remove Exception
            String username = SystemConfiguration.getConfig("config.properties", "username");
            String password = SystemConfiguration.getConfig("config.properties", "password");

            if(Utils.isBlank(username) || Utils.isBlank(password)) {
                throw new SystemPropertyNotPresentException("Credentials not set");
            }

            Scanner scan = new Scanner(System.in).useDelimiter("\n");
            String option;
            TicketUtils tu = new TicketUtils(username, password);
            CMDStrings.StartScreen();

            do {
                CMDStrings.displayOptions();
                option = scan.next();
                try {
                    switch (option) {
                        case CMDStrings.VIEW_ALL_TICKET:
                            tu.populateTickets();
                            tu.displayTickets();
                            break;
                        case CMDStrings.VIEW_A_TICKET:
                            System.out.println("Enter ticket number");
                            String num = scan.next();
                            Ticket ticket = tu.fetchPerTicket(num);
                            tu.displayPerTicket(ticket);
                            break;
                        case CMDStrings.QUIT:
                            break;
                        default:
                            System.out.println("Wrong Option. Please try again.");
                    }
                } catch (APIFailedException e) {
                    System.out.println("Failed to execute command.");
                    System.out.println("Error : " + e.getLocalizedMessage());
                    System.out.println("Please try again\n");
                } catch (UnknownHostException e) {
                    System.out.println("Server is not reachable!");
                    System.out.println("Please try again.\n");
                } catch (Exception e) {
                    System.out.println("Some error occurred!");
                    System.out.println("Please try again.\n");
                }
            } while (!option.equals(CMDStrings.QUIT));
    }

}
