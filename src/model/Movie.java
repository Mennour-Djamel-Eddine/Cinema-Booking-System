package model;

public class Movie {
    private int ID;
    private String title;
    private String genre;
    private int duration; // in minutes
    private String synopsis;

    public Movie(int ID, String title, String genre, int duration, String synopsis) {
        this.ID = ID;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.synopsis = synopsis;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public String getSynopsis() {
        return synopsis;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", synopsis='" + synopsis + '\'' +
                '}';
    }
}
