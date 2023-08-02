package trackour.trackour.spotify;

import se.michaelthelin.spotify.SpotifyApi;

// interface to represent any Spotify API request
@FunctionalInterface
public interface SpotifyApiRequest<T> {
    // Define an abstract method that takes a SpotifyApi object as a parameter and returns a generic type
    T execute(SpotifyApi spotifyApi) throws Exception;
}
