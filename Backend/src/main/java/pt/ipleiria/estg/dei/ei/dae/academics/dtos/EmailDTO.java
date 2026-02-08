package pt.ipleiria.estg.dei.ei.dae.academics.dtos;

import java.io.Serializable;

public class EmailDTO implements Serializable {
    private String body;
    private String subject;

    public EmailDTO() {
    }

    public EmailDTO(String body, String subject) {
        this.body = body;
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
