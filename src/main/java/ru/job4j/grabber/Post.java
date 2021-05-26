package ru.job4j.grabber;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
    private int idPrimaryKey;
    private String heading;
    private String details;
    private String link;
    private LocalDateTime created;

    public int getIdPrimaryKey() {
        return idPrimaryKey;
    }

    public void setIdPrimaryKey(int idPrimaryKey) {
        this.idPrimaryKey = idPrimaryKey;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return idPrimaryKey == post.idPrimaryKey && Objects.equals(link, post.link) && created.equals(post.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPrimaryKey, link, created);
    }
}
