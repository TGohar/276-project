package trackour.trackour.spotify;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class PlaylistsItems {

  private ClientCred clientCred;
  private String accessToken;
  private String playlistId;
  private SpotifyApi spotifyApi;
  private GetPlaylistsItemsRequest getPlaylistsItemsRequest;

  public PlaylistsItems(String playlistID) {
    playlistId = playlistID;
    initialize();
  }

  private void initialize() {
    this.clientCred = new ClientCred();
    this.accessToken = clientCred.getAccessToken();
    if (accessToken != null) {
      this.spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken).build();
      this.getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistId).limit(50).build();
    }
  }

  public void getPlaylistsItems_Sync() {
    initialize();
    try {
      final Paging<PlaylistTrack> playlistTrackPaging = getPlaylistsItemsRequest.execute();

      System.out.println("Total: " + playlistTrackPaging.getTotal());
      System.out
          .println("Track's first artist: " + ((Track) playlistTrackPaging.getItems()[0].getTrack()).getArtists()[0]);
      System.out.println("Episode's show: " + ((Episode) playlistTrackPaging.getItems()[0].getTrack()).getShow());
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public void getPlaylistsItems_Async() {
    initialize();
    try {
      final CompletableFuture<Paging<PlaylistTrack>> pagingFuture = getPlaylistsItemsRequest.executeAsync();

      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      final Paging<PlaylistTrack> playlistTrackPaging = pagingFuture.join();

      System.out.println("Total: " + playlistTrackPaging.getTotal());
      System.out
          .println("Track's first artist: " + ((Track) playlistTrackPaging.getItems()[0].getTrack()).getArtists()[0]);
      System.out.println("Episode's show: " + ((Episode) playlistTrackPaging.getItems()[0].getTrack()).getShow());
    } catch (CompletionException e) {
      System.out.println("Error: " + e.getCause().getMessage());
    } catch (CancellationException e) {
      System.out.println("Async operation cancelled.");
    }
  }

  public List<PlaylistTrack> getItemsInPlaylist() {
    initialize();
    final CompletableFuture<Paging<PlaylistTrack>> pagingFuture = getPlaylistsItemsRequest.executeAsync();
    final Paging<PlaylistTrack> playlistTrackPaging = pagingFuture.join();
    return Arrays.asList(playlistTrackPaging.getItems());
  }

}
