package trackour.trackour.spotify;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Category;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.data.browse.GetListOfCategoriesRequest;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.Arrays;
import java.util.List;

public class Explore {
    
    private ClientCred clientCred;
    private String accessToken;
    private SpotifyApi spotifyApi;
    private GetListOfCategoriesRequest getListOfCategoriesRequest;

    public Explore() {
        initialize();
    }

    private void initialize() {
        this.clientCred = new ClientCred();
        this.accessToken = clientCred.getAccessToken();
        this.spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();
        this.getListOfCategoriesRequest = spotifyApi.getListOfCategories().build();
                // .country(CountryCode.SE)
                // .limit(10)
                // .offset(0)
                // .build();
    }

    public void getListOfCategories_Sync() {
        initialize();
        try {
            final Paging<Category> categoryPaging = getListOfCategoriesRequest.execute();

            System.out.println("Total: " + categoryPaging.getTotal());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void getListOfCategories_Async() {
        try {
            final CompletableFuture<Paging<Category>> pagingFuture = getListOfCategoriesRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final Paging<Category> categoryPaging = pagingFuture.join();
            System.out.println("Total: " + categoryPaging.getTotal());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    public List<Category> getCategories() {
        final CompletableFuture<Paging<Category>> pagingFuture = getListOfCategoriesRequest.executeAsync();
        final Paging<Category> categoryPaging = pagingFuture.join();
        return Arrays.asList(categoryPaging.getItems());
    }

    // public static void main(String[] args) {
    // // getListOfCategories_Sync();
    // // getListOfCategories_Async();
    // getCategories();
    // }
}