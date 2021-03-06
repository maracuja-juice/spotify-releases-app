package com.maracuja_juice.spotifynotifications.services;

import android.os.AsyncTask;
import android.os.Build;

import com.annimon.stream.Stream;
import com.maracuja_juice.spotifynotifications.di.DaggerSpotifyApiComponent;
import com.maracuja_juice.spotifynotifications.interfaces.DownloadCompleted;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;

public class SpotifyCrawlerTask extends AsyncTask<Void, Void, List<Album>> {

    @Inject
    SpotifyApi api;
    private DownloadCompleted listener;

    private ArtistCrawler artistCrawler;
    private AlbumCrawler albumCrawler;

    public SpotifyCrawlerTask(String token, DownloadCompleted listener) {
        DaggerSpotifyApiComponent.create().inject(this);

        api.setAccessToken(token);
        SpotifyService spotify = api.getService();
        artistCrawler = new ArtistCrawler(spotify);
        albumCrawler = new AlbumCrawler(spotify);

        this.listener = listener;
    }

    public List<Artist> getFollowedArtists() {
        return artistCrawler.getFollowedArtists();
    }

    public List<Album> getAlbumsOfAllArtists(List<Artist> artists) {
        ArrayList<Album> albums = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            artists.parallelStream()
                    .forEach(artist -> albums.addAll(getAlbumsOfSingleArtist(artist)));
        } else {
            // backwards compatible streams api via library
            Stream.of(artists)
                    .forEach((artist) -> albums.addAll(getAlbumsOfSingleArtist(artist)));
        }
        return albums;
    }

    public List<Album> getAlbumsOfSingleArtist(Artist artist) {
        return albumCrawler.getAlbumsOfArtist(artist);
    }

    @Override
    protected List<Album> doInBackground(Void... voids) {
        try {
            List<Artist> artists = getFollowedArtists();
            List<Album> albums = getAlbumsOfAllArtists(artists);
            return albums;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Album> s) {
        super.onPostExecute(s);
        listener.downloadComplete(s);
    }
}
