package com.test.demo.mediafacer.mediaHolders;

import android.net.Uri;

import java.util.ArrayList;

public class audioArtistContent {

    private String artistName;
    private ArrayList<audioAlbumContent> albums = new ArrayList<>();

    public audioArtistContent() {

    }

    public audioArtistContent(String artistName,Uri art_uri ,ArrayList<audioAlbumContent> albums) {
        this.artistName = artistName;
        this.albums = albums;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public ArrayList<audioAlbumContent> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<audioAlbumContent> albums) {
        this.albums = albums;
    }

    public int getNumOfSongs() {
        int numOfSongs = 0;
        for(int i = 0; i < albums.size();i++){
            numOfSongs = numOfSongs + albums.get(i).getNumberOfSongs();
        }
        return numOfSongs;
    }

}
