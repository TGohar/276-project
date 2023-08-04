package trackour.trackour.spotify;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hc.core5.http.ParseException;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;

import se.michaelthelin.spotify.SpotifyApi;
// import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Category;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.browse.GetCategorysPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.browse.GetListOfCategoriesRequest;
import se.michaelthelin.spotify.requests.data.browse.GetListOfNewReleasesRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import trackour.trackour.views.api.ClientKeys;


public class SpotifySessionService {

    // Declare the SpotifyApi object as a static field
    private static SpotifyApi spotifyApi;

    // Declare the AuthorizationCodeCredentials object as a session attribute name
    private static final String AUTHORIZATION_CODE_CREDENTIALS = "authorizationCodeCredentials";

    // Declare the ClientCredentials object as a session attribute name
    private static final String CLIENT_CREDENTIALS = "clientCredentials";

    // private static final String clientId = ClientKeys.CLIENT_ID.getKey();
    // private static final String clientSecret = ClientKeys.CLIENT_SECRET.getKey();

    // Initialize the SpotifyApi object with the application's properties
    static {
        // Get the application's properties from environment variables or other sources
        // String clientId = System.getenv("SPOTIFY_CLIENT_ID");
        // String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
        // String redirectUri = System.getenv("SPOTIFY_REDIRECT_URI");

        final String clientId = ClientKeys.CLIENT_ID.getKey();
        final String clientSecret = ClientKeys.CLIENT_SECRET.getKey();
        final String redirectUri = "https://spotifyweb-995bdcbaa3b4.herokuapp.com/spotify"; // https://spotifyweb-995bdcbaa3b4.herokuapp.com

        // Check if the redirectUri is not null and is a valid URI format
        if (redirectUri != null && redirectUri.matches("^https?://.+")) {
            // Initialize the SpotifyApi object with the application's properties
            spotifyApi = new SpotifyApi.Builder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    // .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
                    .build();
        } else {
            // Handle the invalid redirectUri
            System.out.println("The redirectUri is null or invalid");
        }
    }

    // Create a static method that sets the spotifyApi and clientCredentials as session attributes using the client credentials flow
    public static void setSpotifySessionWithoutAuthorization() {
        try {
            // Exchange the client credentials for an access token
            ClientCredentialsRequest request = spotifyApi.clientCredentials().build();

            // Execute the request and get the ClientCredentials object
            ClientCredentials clientCredentials = request.execute();

            // Set the access token to the SpotifyApi object
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            // Display a success message to the user
            Notification.show("You have successfully connected to Spotify!");
            System.out.println("You have successfully connected to Spotify!");

            // The user session has the credentials needed for non-authorized requests
            // Store the SpotifyApi object and the ClientCredentials object as session attributes for later use
            UI.getCurrent().getSession().setAttribute(SpotifyApi.class, spotifyApi);
            UI.getCurrent().getSession().setAttribute(CLIENT_CREDENTIALS, clientCredentials);

        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    // Create a static method that checks if the user has a valid access token and redirects them to authorization if not
    public static String checkAuthorization(String url) {
        String returnUrl = null;
        try {
            // Check if the user has a valid access token
            String accessToken = SpotifySessionService.getAccessToken();
            if (accessToken == null) {
                // The user has not authorized the application or has an expired access token
                // Redirect them to the authorization code URI with the desired scopes
                redirectToAuthorization();
            } else {
                // The user has a valid access token
                // Create a MusicPlayer component with the Spotify URL and add it to the view
                returnUrl = url;
            }
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
        return returnUrl;
    }

    // Create a static method that sets the spotifyApi and authorizationCodeCredentials as session attributes using the authorization code flow
    public static void setSpotifySession(String code) {
        try {
            // Exchange the authorization code for an access token and a refresh token
            AuthorizationCodeRequest request = spotifyApi.authorizationCode(code)
                .build();

            // Execute the request and get the AuthorizationCodeCredentials object
            AuthorizationCodeCredentials authorizationCodeCredentials = request.execute();

            System.out.println("refresh token | setSpotifySession: " + authorizationCodeCredentials.getRefreshToken());

            // Set the access token and refresh token to the SpotifyApi object
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            // Display a success message to the user
            Notification.show("You have successfully logged in with Spotify!");

            // The user session has the credentials needed for authorized requests
            // Store the SpotifyApi object and the AuthorizationCodeCredentials object as session attributes for later use
            UI.getCurrent().getSession().setAttribute(SpotifyApi.class, spotifyApi);
            UI.getCurrent().getSession().setAttribute(AUTHORIZATION_CODE_CREDENTIALS, authorizationCodeCredentials);

        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    } 

    // Create a static method that redirects the user to the authorization code URI with the desired scopes
    public static void redirectToAuthorization() {
        try {
            // Create a URI request with the optional parameter "scope"
            final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("streaming,user-read-email,playlist-read-private")
                .build();

            // Get the URI
            final URI uri = authorizationCodeUriRequest.execute();

            // Redirect the user to the URI
            System.out.println("URI: " + uri.toString());
            UI.getCurrent().getPage().setLocation(uri.toString());

        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    // Create a static method that returns the SpotifyApi object from the session or null if not found
    public static SpotifyApi getSpotifyApi() {
        return UI.getCurrent().getSession().getAttribute(SpotifyApi.class);
    }

    // Create a static method that returns the AuthorizationCodeCredentials object from the session or null if not found
    public static AuthorizationCodeCredentials getAuthorizationCodeCredentials() {
        // Getting the attribute with a class key
        return (AuthorizationCodeCredentials) UI.getCurrent().getSession().getAttribute(AUTHORIZATION_CODE_CREDENTIALS);
    }

    // Create a static method that gets the clientCredentials from the session attribute
    public static ClientCredentials getClientCredentials() {
        return (ClientCredentials) UI.getCurrent().getSession().getAttribute(CLIENT_CREDENTIALS);
    }

    // Create a static method that refreshes the access token using the authorization code credentials and updates the session attribute
    public static String refreshAccessTokenWithAuthorizationCode() {
        // Get the SpotifyApi object from the session
        SpotifyApi spotifyApi = getSpotifyApi();

        // Check if the spotifyApi is null
        if (spotifyApi == null) {
            // Return null
            return null;
        }

        // Try to refresh the access token using the authorization code credentials
        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCodeRefresh().build().execute();

            // Update the session attribute with the new credentials
            UI.getCurrent().getSession().setAttribute(AUTHORIZATION_CODE_CREDENTIALS, authorizationCodeCredentials);

            // Return the new access token
            return authorizationCodeCredentials.getAccessToken();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // Handle exception, return null
            e.printStackTrace();
            return null;
        }
    }
    
    // Create a static method that refreshes the access token using the client credentials and updates the session attribute
    public static String refreshAccessTokenWithClientCredentials() {
        // Get the SpotifyApi object from the session
        SpotifyApi spotifyApi = getSpotifyApi();

        // Check if the spotifyApi is null
        if (spotifyApi == null) {
            // Return null
            return null;
        }

        // Try to refresh the access token using the client credentials
        try {
            ClientCredentials clientCredentials = spotifyApi.clientCredentials().build().execute();

            // Update the session attribute with the new credentials
            UI.getCurrent().getSession().setAttribute(CLIENT_CREDENTIALS, clientCredentials);

            // Return the new access token
            return clientCredentials.getAccessToken();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // Handle exception, return null
            e.printStackTrace();
            return null;
        }
    }

    // Create a static method that checks if the access token is expired by comparing its expiration time with the current time
    public static boolean isAccessTokenExpired() {
        // Get the current time in milliseconds
        long currentTime = System.currentTimeMillis();

        // Get the expiration time of either the authorizationCodeCredentials or the clientCredentials, depending on which one is available
        long expirationTime;
        if (getAuthorizationCodeCredentials() != null) {
            expirationTime = getAuthorizationCodeCredentials().getExpiresIn();
        } else if (getClientCredentials() != null) {
            expirationTime = getClientCredentials().getExpiresIn();
        } else {
            // No credentials found, return true
            return true;
        }

        // Check if the current time is greater than or equal to the expiration time minus a buffer of 10 seconds
        return currentTime >= expirationTime - 10000;
    }

    // Create a static method that refreshes the access token if it is expired and returns it or null if not found
    public static String getRefreshedAccessToken() {
        // Check if the access token is expired
        if (isAccessTokenExpired()) {
            // Try to refresh the access token using either the authorizationCodeCredentials or the clientCredentials, depending on which one is available
            if (getAuthorizationCodeCredentials() != null) {
                // Refresh the access token using the authorization code credentials and return it
                return refreshAccessTokenWithAuthorizationCode();
            } else if (getClientCredentials() != null) {
                // Refresh the access token using the client credentials and return it
                return refreshAccessTokenWithClientCredentials();
            } else {
                // No credentials found, return null
                return null;
            }
        } else {
            // The access token is not expired, return it from either the authorizationCodeCredentials or the clientCredentials, depending on which one is available
            if (getAuthorizationCodeCredentials() != null) {
                return getAuthorizationCodeCredentials().getAccessToken();
            } else if (getClientCredentials() != null) {
                return getClientCredentials().getAccessToken();
            } else {
                // No credentials found, return null
                return null;
            }
        }
    }

    // Create a static method that refreshes the access token if it is expired and returns it or null if not found
    public static String getAccessToken() {
        if (getAuthorizationCodeCredentials() != null) {
            return getAuthorizationCodeCredentials().getAccessToken();
        } else if (getClientCredentials() != null) {
            return getClientCredentials().getAccessToken();
        } else {
            return null;
        }
    }

    // Create a static method that executes a SpotifyApiRequest with the spotifyApi and accessToken, if they are not null
    public static <T> T executeRequest(SpotifyApiRequest<T> request) {
        SpotifyApi spotifyApi = getSpotifyApi();
        // Use the getRefreshedAccessToken method instead of the getAccessToken method
        String accessToken = getRefreshedAccessToken();

        System.out.println("spotifyApi | executeRequest: " + spotifyApi);
        System.out.println("accessToken | executeRequest: " + accessToken);
        System.out.println("getAuthorizationCodeCredentials | executeRequest: " + getAuthorizationCodeCredentials());

        if (spotifyApi != null && accessToken != null) {
            try {
                // Set the access token to the SpotifyApi object
                spotifyApi.setAccessToken(accessToken);

                // Execute the request and return the result
                return request.execute(spotifyApi);
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        }
        return null;
    }


    // Create a static method that returns a list of new releases or null if not found
    // getNewReleases
    public static List<AlbumSimplified> getNewReleases() {
        List<AlbumSimplified> albums = new ArrayList<AlbumSimplified>();
        SpotifyApiRequest<Paging<AlbumSimplified>> request = (
            spotifyApi -> {
            // Build the request for new releases
            GetListOfNewReleasesRequest getListOfNewReleasesRequest = spotifyApi.getListOfNewReleases()
                    .limit(20) // Set optional parameters here
                    .build();
    
            // Execute the request and return the Paging object containing the albums
            return getListOfNewReleasesRequest.execute();
            }
        );
    
        // Use the helper method to execute the request with the spotifyApi and accessToken, if they are not null
        Paging<AlbumSimplified> paging = executeRequest(request);
    
        // Check if the paging object is not null
        if (paging != null) {
            // Get the list of albums from the Paging object
            albums = Arrays.asList(paging.getItems());
    
            // Return the list of albums
            return albums;
        } else {
            // Return null
            return albums;
        }
    }

    // SearchTrack
     public static List<Track> getTrackList(String trackQueryString) {

        List<Track> songs = new ArrayList<>();
        System.out.println("trackQueryString: " + trackQueryString);
         SpotifyApiRequest <Paging<Track>> request = spotifyApi -> {
            System.out.println("spotifyApi: " + spotifyApi);
            SearchTracksRequest tracksRequest = spotifyApi.searchTracks(trackQueryString).build();
            return tracksRequest.execute();
        };
            
            // Use the helper method to execute the request with the spotifyApi and accessToken, if they are not null
            Paging<Track> paging = executeRequest(request);

            System.out.println("request: " + request);
            
            if (paging != null) {
                try {
                    songs = Arrays.asList(paging.getItems());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return songs;
    }

    // getCategories
    public static List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        SpotifyApiRequest<Paging<Category>> request = spotifyApi -> {
            // SearchTracksRequest tracksRequest = spotifyApi.searchTracks(trackQueryString).build();
            GetListOfCategoriesRequest tracksRequest = spotifyApi.getListOfCategories().limit(50).build();
            return tracksRequest.execute();
        };
        
        // Use the helper method to execute the request with the spotifyApi and accessToken, if they are not null
        Paging<Category> paging = executeRequest(request);
        
        // Check if the pagingFuture object is not null
        if (paging != null) {
            try {
                // Get the list of categories from the Paging object
                categories = Arrays.asList(paging.getItems());

                // Return the list of categories
                return categories;
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        }
        // Return null
        return categories;
    }

    // getPlaylists
    public static List<PlaylistSimplified> getPlaylists(String categoryId) {
        List<PlaylistSimplified> categories = new ArrayList<>();
        SpotifyApiRequest<Paging<PlaylistSimplified>> request = spotifyApi -> {
            // SearchTracksRequest tracksRequest = spotifyApi.searchTracks(trackQueryString).build();
            GetCategorysPlaylistsRequest catRequest = spotifyApi.getCategorysPlaylists(categoryId).limit(50).build();
            return catRequest.execute();
        };
        
        // Use the helper method to execute the request with the spotifyApi and accessToken, if they are not null
        Paging<PlaylistSimplified> paging = executeRequest(request);
        
        // Check if the pagingFuture object is not null
        if (paging != null) {
            try {
                // Get the list of categories from the Paging object
                categories = Arrays.asList(paging.getItems());

                // Return the list of categories
                return categories;
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        }
        // Return null
        return categories;
    }

    // getItemsInPlaylist
    public static List<PlaylistTrack> getItemsInPlaylist(String playlistId) {
        List<PlaylistTrack> playlistTracks = new ArrayList<>();
        SpotifyApiRequest<Paging<PlaylistTrack>> request = spotifyApi -> {
            // SearchTracksRequest tracksRequest = spotifyApi.searchTracks(trackQueryString).build();
            GetPlaylistsItemsRequest playlistTracksRequest = spotifyApi.getPlaylistsItems(playlistId).limit(50).build();
            return playlistTracksRequest.execute();
        };
        
        // Use the helper method to execute the request with the spotifyApi and accessToken, if they are not null
        Paging<PlaylistTrack> paging = executeRequest(request);
        
        // Check if the pagingFuture object is not null
        if (paging != null) {
            try {
                // Get the list of categories from the Paging object
                playlistTracks = Arrays.asList(paging.getItems());

                // Return the list of categories
                return playlistTracks;
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        }
        // Return null
        return playlistTracks;
    }

    // Create a static method that returns the user's profile or null if not found
    public static User getProfile() {
        SpotifyApi spotifyApi = getSpotifyApi();
        String accessToken = getAccessToken();

        if (spotifyApi != null && accessToken != null) {
            try {
                // Set the access token to the SpotifyApi object
                spotifyApi.setAccessToken(accessToken);

                // Build and execute the request for the user's profile
                GetCurrentUsersProfileRequest request = spotifyApi.getCurrentUsersProfile()
                        .build();

                // Get the User object from the request
                User user = request.execute();

                // Return the User object
                return user;
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        }
        return null;
    }

}

