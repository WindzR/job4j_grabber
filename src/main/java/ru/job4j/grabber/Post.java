package ru.job4j.grabber;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
    private String url;
    private String theme;
    private String description;
    private String author;
    private int answers;
    private int views;
    private LocalDateTime date;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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
        return url.equals(post.url) && Objects.equals(theme, post.theme)
                && author.equals(post.author) && date.equals(post.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, theme, author, date);
    }

    @Override
    public String toString() {
        return "Post{"
                + "url='" + url + '\''
                + ", theme='" + theme + '\''
                + ", description='" + description + '\''
                + ", author='" + author + '\''
                + ", answers=" + answers
                + ", views=" + views
                + ", date=" + date
                + '}';
    }
}
