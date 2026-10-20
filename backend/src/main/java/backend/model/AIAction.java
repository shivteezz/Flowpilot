package backend.model;

public class AIAction {

    private String action;

    // General AI
    private String answer;

    // Email
    private String recipient;
    private String subject;
    private String body;
    private String scheduledAt;

    public AIAction() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}