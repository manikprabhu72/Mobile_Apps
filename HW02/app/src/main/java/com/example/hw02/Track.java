package com.example.hw02;

import java.io.Serializable;
import java.util.Date;

public class Track implements Serializable {
    String title;
    String artist;
    String album;

    @Override
    public String toString() {
        return "Track{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", genre='" + genre + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", trackPrice=" + trackPrice +
                ", albumPrice=" + albumPrice +
                ", releaseDate=" + releaseDate +
                '}';
    }

    String genre;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    String imageURL;
    double trackPrice,albumPrice;
    String releaseDate;

    /*public Track(String title, String artist, String album, String genre, double trackPrice, double albumPrice, Date releaseDate) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.trackPrice = trackPrice;
        this.albumPrice = albumPrice;
        this.releaseDate = releaseDate;
    }*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getTrackPrice() {
        return trackPrice;
    }

    public void setTrackPrice(double trackPrice) {
        this.trackPrice = trackPrice;
    }

    public double getAlbumPrice() {
        return albumPrice;
    }

    public void setAlbumPrice(double albumPrice) {
        this.albumPrice = albumPrice;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
