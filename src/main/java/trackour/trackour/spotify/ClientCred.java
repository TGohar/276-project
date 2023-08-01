package trackour.trackour.spotify;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import trackour.trackour.views.api.ClientKeys;

import org.apache.hc.core5.http.ParseException;
// import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Web.Client;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// import io.github.cdimascio.dotenv.Dotenv;

public class ClientCred {
    
    // private static final Dotenv dotenv = Dotenv.configure().load();
    private static final String clientId = ClientKeys.CLIENT_ID.getKey();
    private static final String clientSecret = ClientKeys.CLIENT_SECRET.getKey();
    private SpotifyApi spotifyApi;
    
    // to refresh the access token
    private ClientCredentialsRequest clientCredentialsRequest;
    private AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest;

    public ClientCred() {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
        this.clientCredentialsRequest = spotifyApi.clientCredentials().build();
        this.authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
    }
 
    public void clientCredentials_Sync() {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            // Set access token for further "spotifyApi" object usage and the expiry time and date
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());


            System.out.println(
                    "Expires in: " + clientCredentials.getExpiresIn());
                    System.out.println("access: " + clientCredentials.getAccessToken());
                    System.out.println("refresh: " + clientCredentials.getTokenType());
                    spotifyApi.getRefreshToken();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void clientCredentials_Async() {
        try {
            final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest
                    .executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final ClientCredentials clientCredentials = clientCredentialsFuture.join();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            // System.out.println("Expires in: " + clientCredentials.getExpiresIn() +
            // clientCredentials.getAccessToken());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }
    
    public Boolean isAccessTokenExpired() {
        final String refreshToken = spotifyApi.getRefreshToken();
        return !(refreshToken == null);
    }

    /**
     * This private method is to be called always before the access token is requested and before the request is made to always have the updated access token
     */
    private void refreshAccessToken() {
        if (!isAccessTokenExpired()) {
            System.out.println("Access token is still valid.");
            System.out.println("access token: " + spotifyApi.getAccessToken());
            return;
        }
        
        // else it's expired
        try {
            // Execute the token refresh request and get the response
            AuthorizationCodeCredentials clientCredentials = authorizationCodeRefreshRequest.execute();
            
            // Set the refreshed access token and expiration dates
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            
            // Print the new access token
            System.out.println("access token: " + spotifyApi.getAccessToken());
            
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Call this method to get an updated version of the access token for any api call yoy make
     * @return
     */
    public String getAccessToken() {
        // call before each call
        this.refreshAccessToken();
        clientCredentials_Async(); // Call the synchronous method to obtain the access token
        // System.out.println(spotifyApi.getAccessToken());
        System.out.println("access tok:"+spotifyApi.getAccessToken());
        return spotifyApi.getAccessToken();
    }
    // public static void   (String[] args) {
        //     // clientCredentials_Sync();
        //     // clientCredentials_Async();
        
        //     String accessToken = getAccessToken();
    //     if (accessToken != null) {
    //         System.out.println("Access Token: " + accessToken);
    //     }
    // }
}