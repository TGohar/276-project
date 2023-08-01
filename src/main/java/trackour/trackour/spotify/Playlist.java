package trackour.trackour.spotify;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.browse.GetCategorysPlaylistsRequest;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Playlist {

    private ClientCred clientCred;
    private String accessToken;
    private String categoryId;
    private SpotifyApi spotifyApi;
    private GetCategorysPlaylistsRequest getCategoryRequest;

    public Playlist(String catID) {
        categoryId = catID;
        initialize();
    }

    private void initialize() {
        this.clientCred = new ClientCred();
        this.accessToken = clientCred.getAccessToken();

        if (accessToken != null) {
            this.spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken).build();
            this.getCategoryRequest = spotifyApi.getCategorysPlaylists(categoryId).limit(50).build();
        }
    }

    public void getCategorysPlaylists_Sync() {
        initialize();
        try {
            final Paging<PlaylistSimplified> playlistSimplifiedPaging = getCategoryRequest.execute();

            System.out.println("Total: " + playlistSimplifiedPaging.getTotal());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void getCategorysPlaylists_Async() {
        initialize();
        try {
            final CompletableFuture<Paging<PlaylistSimplified>> pagingFuture = getCategoryRequest.executeAsync();
            final Paging<PlaylistSimplified> playlistSimplifiedPaging = pagingFuture.join();
            playlistSimplifiedPaging.getClass().getName();

            System.out.println(
                    "Total: " + playlistSimplifiedPaging.getTotal() + " "
                            + playlistSimplifiedPaging.getClass().getName());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    public List<PlaylistSimplified> getPlaylists() {
        initialize();
        final CompletableFuture<Paging<PlaylistSimplified>> pagingFuture = getCategoryRequest.executeAsync();
        final Paging<PlaylistSimplified> playlistSimplifiedPaging = pagingFuture.join();
        return Arrays.asList(playlistSimplifiedPaging.getItems());
    }
}