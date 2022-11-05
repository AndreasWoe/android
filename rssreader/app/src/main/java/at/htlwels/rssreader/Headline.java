package at.htlwels.rssreader;

public class Headline {
    private String id;
    private String title;
    private String subject;
    private String date;

    @Override
    public String toString() {
        return  "ID: " + id + "\n" +
                "Titel: " + title + "\n" +
                "Kategorie: " + subject + "\n" +
                "Datum: " + date + "\n";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
