package trackour.trackour.spotify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.hc.core5.http.ParseException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

public class SearchTrack {
  
  // static String q = NavBar.getSearchValue();
  //          .market(CountryCode.SE)
  //          .limit(10)
  //          .offset(0)
  //          .includeExternal("audio")

  private ClientCred clientCred;
  private String accessToken;
  private SpotifyApi spotifyApi;
  private SearchTracksRequest searchTracksRequest;
  private String searchQuery;

  public SearchTrack() {
    initialize();
  }

  private void initialize() {
    this.clientCred = new ClientCred();
    this.accessToken = clientCred.getAccessToken();
    this.searchQuery = "";
    this.spotifyApi = new SpotifyApi.Builder()
            .setAccessToken(accessToken)
            .build();
    this.searchTracksRequest = null;
  }

  public void searchTracks_Sync() {
    initialize();
    try {
      searchTracksRequest = spotifyApi.searchTracks(this.searchQuery).build();
      final Paging<Track> trackPaging = searchTracksRequest.execute();
      System.out.println(searchTracksRequest.execute().getItems());

      System.out.println("Total: " + trackPaging.getTotal());
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public void searchTracks_Async() {
    initialize();
    try {
      searchTracksRequest = spotifyApi.searchTracks(this.searchQuery).build();
      final CompletableFuture<Paging<Track>> pagingFuture = searchTracksRequest.executeAsync();

      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      final Paging<Track> trackPaging = pagingFuture.join();

      System.out.println("Total: " + trackPaging.getTotal());
    } catch (CompletionException e) {
      System.out.println("Error: " + e.getCause().getMessage());
    } catch (CancellationException e) {
      System.out.println("Async operation cancelled.");
    }
  }

 public List<Track> getTrackList(String trackQueryString) {
  
  System.out.println("trackQueryString:" + trackQueryString);
  initialize();

    SearchTracksRequest tracksRequest = spotifyApi.searchTracks(trackQueryString).build();

    List<Track> songs = new ArrayList<>();

    try {
      final Paging<Track> trackPaging = tracksRequest.execute();
      songs = Arrays.asList(trackPaging.getItems());

      System.out.println("Total: " + trackPaging.getTotal());
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
        return songs;
    }
}
