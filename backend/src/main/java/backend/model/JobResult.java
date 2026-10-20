package backend.model;

public class JobResult {

    private String title;
    private String company;
    private String location;
    private String url;
    private String description;
    private String source;
    private boolean remote;

    public JobResult() {
    }

    public JobResult(
            String title,
            String company,
            String location,
            String url,
            String description,
            String source,
            boolean remote
    ) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.url = url;
        this.description = description;
        this.source = source;
        this.remote = remote;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }
}