package trackour.trackour.spotify;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.data.browse.GetListOfNewReleasesRequest;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class NewReleases {
    
    private ClientCred clientCred;
    private String accessToken;
    private SpotifyApi spotifyApi;
    private GetListOfNewReleasesRequest getListOfNewReleasesRequest;
    public NewReleases () {
        initialize();
    }

    private void initialize() {
        this.clientCred = new ClientCred();
        this.accessToken = clientCred.getAccessToken();
        this.spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();
        this.getListOfNewReleasesRequest = spotifyApi.getListOfNewReleases()
                // .country(CountryCode.SE)
                // .limit(10)
                // .offset(0)
                .build();
    }
    public void getListOfNewReleases_Sync() {
        initialize();
        try {
            final Paging<AlbumSimplified> albumSimplifiedPaging = getListOfNewReleasesRequest.execute();

            System.out.println("Total: " + albumSimplifiedPaging.getTotal());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void getListOfNewReleases_Async() {
        initialize();
        try {
            final CompletableFuture<Paging<AlbumSimplified>> pagingFuture = getListOfNewReleasesRequest.executeAsync();
            final Paging<AlbumSimplified> albumSimplifiedPaging = pagingFuture.join();
            AlbumSimplified[] albumsArray = albumSimplifiedPaging.getItems();
            List<AlbumSimplified> albums = Arrays.asList(albumsArray);

            System.out.println("Total: " + albumSimplifiedPaging.getTotal());
            for (AlbumSimplified album : albums) {
                System.out.println("Album Name: " + album.getName());
            }
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    public List<AlbumSimplified> getNewReleases() {

        initialize();

        final CompletableFuture<Paging<AlbumSimplified>> pagingFuture = getListOfNewReleasesRequest.executeAsync();
        final Paging<AlbumSimplified> albumSimplifiedPaging = pagingFuture.join();

        return Arrays.asList(albumSimplifiedPaging.getItems());
    }

    // public static void main(String[] args) {
    // getNewReleases();
    // }
}
