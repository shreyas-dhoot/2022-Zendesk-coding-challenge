import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Ticket {

    private Long id;
    private Date created_at;
    private String type;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private Long submitter_id;
    private Long assignee_id;

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



