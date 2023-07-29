package trackour.trackour.views.api;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.Modality;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

// Class to control the Spotify API and access its endpoints by using the Spotify Web API Java package
// Each method is public and static to easily use them throughout the application.
// ex. APIController.getAcousticness("ID GOES HERE");
public class APIController {

    // create a SpotifyApi Builder object and set the Client ID and Client Secret
    private static final SpotifyApi spotifyAPI = new SpotifyApi.Builder()
        .setClientId(ClientKeys.CLIENT_ID.getKey())
        .setClientSecret(ClientKeys.CLIENT_SECRET.getKey())
        .build();

    // create a ClientCredentialsRequest object which allows access to access tokens
    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyAPI.clientCredentials()
        .build();

    // Get the acousticness of the track as a value between 0.0 and 1.0. 
    // The higher the value, the higher the chance the track is acoustic.
    public static float getAcousticness(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            return audioFeatures.getAcousticness();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // Get the danceability of the track as a value between 0.0 and 1.0.
    // The danceability depends on factors like tempo and rhythm stability. Higher is better.
    public static float getDanceability(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            return audioFeatures.getDanceability();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // Get the energy of the track as a value between 0.0 and 1.0. 
    // The energetic value of the track depends on factors like speed and loudness. 
    // Fast and loud tracks feel more energetic than slow and quiet tracks.
    public static float getEnergy(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            return audioFeatures.getEnergy();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // Get the instrumentalness of the track as a value between 0.0 and 1.0. 
    // The higher the value, the higher the chance the track contains no vocals.
    public static float getInstrumentalness(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            return audioFeatures.getInstrumentalness();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // The key the track is in. Integers map to pitches using standard Pitch Class notation. 
    // E.g. 0 = C, 1 = C♯/D♭, 2 = D, and so on. If no key was detected, the value is -1.
    public static int getKey(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            return audioFeatures.getKey();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // Get the liveness of the track as a value between 0.0 and 1.0. 
    // The liveness depends on ambient sounds like the presence of an audience. 
    // The higher the value, the higher the chance the track was performed live.
    public static float getLiveness(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            return audioFeatures.getLiveness();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // Get the average loudness of the track. 
    // These values have mostly a range between -60 and 0 decibels.
    public static float getLoudness(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            return audioFeatures.getLoudness();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return 1;
        }
    }

    // Mode indicates the modality (major or minor) of a track, the type of scale from which its melodic content is derived. 
    // Major is represented by 1 and minor is 0.
    public static int getMode(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            if (audioFeatures.getMode() == Modality.MAJOR) {
                return 1;
            } else {
                return 0;
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // Get the estimated tempo of the track in beats per minute (BPM).
    // Tempo is the speed or pace of a given piece, derived from the average beat duration.
    public static float getTempo(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            return audioFeatures.getTempo();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // Get the estimated overall time signature of the track. The time signature (or meter) is the number of beats in a bar. 
    // Example: A Viennese waltz has a three-quarters beat, so this method would return the value 3 in this case.
    public static int getTimeSignature(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            return audioFeatures.getTimeSignature();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // Get the valence of the track as a value between 0.0 and 1.0. 
    // A track with a high valence sounds more positive (happy, cheerful, euphoric) than a track with a low valence (sad, depressed, angry).
    public static float getValence(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyAPI
                .getAudioFeaturesForTrack(id)
                .build();

            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            return audioFeatures.getValence();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // Get the name of the track by the Spotify Track ID
    public static String getTrackName(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetTrackRequest getTrackRequest = spotifyAPI.getTrack(id)
                .build();
            
            final Track track = getTrackRequest.execute();

            return track.getName();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            return e.getMessage();
        }
    }

    // Get the Album of the specified Track by the Spotify Track ID
    public static AlbumSimplified getAlbum(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetTrackRequest getTrackRequest = spotifyAPI.getTrack(id)
                .build();
            
            final Track track = getTrackRequest.execute();

            return track.getAlbum();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Get the Artist name by the Spotify Artist ID
    public static String getArtistName(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetArtistRequest getArtistRequest = spotifyAPI.getArtist(id)
                .build();
            
            final Artist artist = getArtistRequest.execute();

            return artist.getName();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Get the name of the album using the Spotify Album ID
    public static String getAlbumName(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAlbumRequest getAlbumRequest = spotifyAPI.getAlbum(id)
                .build();

            final Album album = getAlbumRequest.execute();

            return album.getName();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            return e.getMessage();
        }
    }

    // Get an array of Album Cover images in different sizes by the Spotify Album ID
    public static Image[] getAlbumImage(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetAlbumRequest getAlbumRequest = spotifyAPI.getAlbum(id)
                .build();

            final Album album = getAlbumRequest.execute();

            return album.getImages();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Get an array of Artists from the Spotify Track ID
    public static ArtistSimplified[] getArtistFromTrack(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetTrackRequest getTrackRequest = spotifyAPI.getTrack(id)
                .build();

            final Track track = getTrackRequest.execute();

            return track.getArtists();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Get an Album from the Spotify Track ID
    public static AlbumSimplified getAlbumFromTrack(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetTrackRequest getTrackRequest = spotifyAPI.getTrack(id)
                .build();

            final Track track = getTrackRequest.execute();

            return track.getAlbum();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Get tracks based on specifc audio features
    public static TrackSimplified[] getRecommendations(float acousticness, float danceability, float energy, float instrumentalness,
                                                        int key, float loudness, int mode, float tempo, int timeSignature, float valence) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetRecommendationsRequest getRecommendationsRequest = spotifyAPI.getRecommendations()
                .limit(10)
                .target_acousticness(acousticness)
                .target_danceability(danceability)
                .target_energy(energy)
                .target_instrumentalness(instrumentalness)
                .target_key(key)
                .target_loudness(loudness)
                .target_mode(mode)
                .target_tempo(tempo)
                .target_time_signature(timeSignature)
                .target_valence(valence)
                .build();

            final Recommendations recommendations = getRecommendationsRequest.execute();

            return recommendations.getTracks();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Search tracks based on a String and how many results you want returned
    public static Paging<Track> searchTracks(String title, int num) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final SearchTracksRequest searchTracksRequest = spotifyAPI.searchTracks(title)
                .limit(num)
                .offset(0)
                .build();

            final Paging<Track> trackPaging = searchTracksRequest.execute();

            return trackPaging;
            
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Get the Spotify URL using the Track ID
    public static String spotifyURL(String id) {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            spotifyAPI.setAccessToken(clientCredentials.getAccessToken());

            final GetTrackRequest getTrackRequest = spotifyAPI.getTrack(id)
                .build();
            
            final Track track = getTrackRequest.execute();

            return track.getExternalUrls().get("spotify");
            
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            return e.getMessage();
        }
    }
}