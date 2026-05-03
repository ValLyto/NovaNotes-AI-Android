package uws.ac.uk.noteapp;

public class Note {
    private int id;
    private String content;
    private String createdAt;

    public Note(int id, String content, String createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }


    public String getDate() {
        if (createdAt != null && createdAt.contains(" ")) {
            return createdAt.split(" ")[0];
        }
        return "";
    }

    public String getTime() {
        if (createdAt != null && createdAt.contains(" ")) {
            return createdAt.split(" ")[1];
        }
        return "";
    }
}
