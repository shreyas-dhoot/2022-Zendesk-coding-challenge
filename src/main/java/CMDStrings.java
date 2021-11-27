import java.util.List;
import java.util.Scanner;

public class CMDStrings {

    public static final int PAGE_SIZE = 25;
    public static final String VIEW_ALL_TICKET = "1";
    public static final String VIEW_A_TICKET = "2";
    public static final String DISPAY_NEXT_OPTION = "n";
    public static final String DISPAY_PREV_OPTION = "p";

    public static final String EXIT = "exit";
    public static final String QUIT = "quit";

    public static void StartScreen() {
        StringBuilder s = new StringBuilder();
        s.append("Welcome to Zendesk Ticket Viewer\n");
        System.out.println(s.toString());
    }

    public static void displayOptions() {
        StringBuilder s = new StringBuilder();
        s.append("Select an option\n")
                .append("\t\t* Press " + VIEW_ALL_TICKET + " to view all tickets\n")
                .append("\t\t* Press " + VIEW_A_TICKET + " to view a ticket\n")
                .append("\t\t* Type '" + QUIT + "' to close the application\n");
        System.out.println(s);
    }

    public static void displayPageOptions(boolean displayNextOption, boolean displayPrevOption) {
        StringBuilder s = new StringBuilder();
        s.append("Select an option\n");
        if(displayNextOption) {
                s.append("\t\t* Press " + DISPAY_NEXT_OPTION + " to view next page\n");
        }
        if (displayPrevOption) {
                s.append("\t\t* Press " + DISPAY_PREV_OPTION + " to view previous page\n");
        }
        s.append("\t\t* Type '" + EXIT + "' to go to the main menu\n")
                .append("\t\t* Type '" + QUIT + "' to close the application\n");
        System.out.println(s);
    }

    /**
     * Display tickets page wise. To go to the next page press, the user has to input 'n', for prev 'p', to go the main menu 'exit'
     * and to quit from application 'quit'
     * @param results - list of all tickets
     */
    public static void displayTickets(List<Ticket> results) {
        if (results.size() > 0) {
            Scanner scan = new Scanner(System.in).useDelimiter("\n");
            String option = CMDStrings.DISPAY_NEXT_OPTION;
            int start = -1 * PAGE_SIZE;
            int end = 0;
            boolean checkNextPossible;
            boolean checkPrevPossible;
            System.out.println("Total records: " + results.size() + "\n");
            do {
                switch (option) {
                    case CMDStrings.DISPAY_NEXT_OPTION:
                        start += PAGE_SIZE;
                        end += PAGE_SIZE;
                        CMDStrings.displaySubsetOfTickets(results, start, end, false);
                        break;
                    case CMDStrings.DISPAY_PREV_OPTION:
                        start -= PAGE_SIZE;
                        end -= PAGE_SIZE;
                        CMDStrings.displaySubsetOfTickets(results, start, end, false);
                        break;
                    case CMDStrings.QUIT:
                        System.exit(0);
                        break;
                }
                System.out.println("Records shown from " + (start + 1) + " to " + end);
                checkNextPossible = (start + PAGE_SIZE) < results.size();
                checkPrevPossible = (end - PAGE_SIZE) > 0;
                displayPageOptions(checkNextPossible, checkPrevPossible);
                option = scan.next();
                while(true) {
                    if(option.equalsIgnoreCase(DISPAY_NEXT_OPTION)) {
                        if (checkNextPossible) {
                            break;
                        }
                        else {
                            System.out.println("Reached end of ticket list. No next page found.\n");
                        }
                    }
                    else if(option.equalsIgnoreCase(DISPAY_PREV_OPTION)) {
                        if (checkPrevPossible) {
                            break;
                        }
                        else {
                            System.out.println("At start of ticket list. No previous page found.\n");
                        }
                    }
                    else if (option.equalsIgnoreCase(CMDStrings.EXIT) || option.equalsIgnoreCase(CMDStrings.QUIT)) {
                        break;
                    }
                    System.out.println("Wrong Input\n");
                    displayPageOptions(checkNextPossible, checkPrevPossible);
                    option = scan.next();
                }

            } while (!option.equals(CMDStrings.EXIT));
        } else {
            System.out.println("No results found");
        }
    }

    /**
     * Displays information related to tickets in a user-friendly way
     * @param results - Contains all the tickets.
     * @param start - start index
     * @param end - end index
     * @param showFullInfo - showFullInfo is true, all the fields present in the ticket class is shown. If false, only
     *                     the important ones are shown.
     */
    public static void displaySubsetOfTickets(final List<Ticket> results,
                                              final int start,
                                              final int end,
                                              final boolean showFullInfo) {
        for(int index = Math.max(start, 0); index < Math.min(end, results.size()); index++) {
            if (!showFullInfo) {
                System.out.println(results.get(index) + "\n-----------------------------------------\n");
            }
            else {
                System.out.println(results.get(index).showFullInfo() + "\n-----------------------------------------\n");
            }
        }

    }

}
