import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Ticket {

    private Long id;
    private Date created_at;
    private String type;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private Long assignee_id;
    private Long submitter_id;


    public String showFullInfo() {
        StringBuffer s = new StringBuffer();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            try {
                if(f.get(this) != null) {
                    s.append(String.format("%-20s%s%n", f.getName(), f.get(this)));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return s.toString();
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("Ticket with id ")
                .append(id)
                .append(" subject \"")
                .append(subject)
                .append("\" submitted by \"")
                .append(submitter_id)
                .append("\" on \"")
                .append(created_at)
                .append("\"");

        return s.toString();
    }
}



