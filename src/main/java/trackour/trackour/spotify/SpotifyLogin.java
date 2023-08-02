package trackour.trackour.spotify;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

@Route("spotify")
@PageTitle("Spotify Login | Trackour")
@PermitAll
public class SpotifyLogin extends Div implements BeforeEnterObserver {

    // Declare the SpotifyApi object as a static field
    private static SpotifyApi spotifyApi;

    // Declare the login button as an instance field
    private Button loginButton;

    // Create a constructor that initializes the SpotifyApi object and the login button
    public SpotifyLogin() {
        // Get the application's properties from environment variables or other sources
        // String clientId = System.getenv("SPOTIFY_CLIENT_ID");
        // String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
        // String redirectUri = System.getenv("SPOTIFY_REDIRECT_URI");

        final String clientId = "ec8ab71e77cc4198a75b03ff1cf88e3a"; //ClientCred.getClientId();
        final String clientSecret = "cac2b901c0df459d9bdc15b4975151c8"; //ClientCred.getClientSecret();
        final String redirectUri = "https://spotifyweb-995bdcbaa3b4.herokuapp.com/spotify"; // https://spotifyweb-995bdcbaa3b4.herokuapp.com

        // Check if the redirectUri is not null and is a valid URI format
        if (redirectUri != null && redirectUri.matches("^https?://.+")) {
            // Initialize the SpotifyApi object with the application's properties
            spotifyApi = new SpotifyApi.Builder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
                    .build();
        } else {
            // Handle the invalid redirectUri
            System.out.println("The redirectUri is null or invalid");
        }
        // Store the SpotifyApi object in the session
        // UI.getCurrent().getSession().setAttribute(SpotifyApi.class, spotifyApi);

        // Initialize the login button with a click listener that redirects to the authorization URI
        loginButton = new Button("Login with Spotify", e -> {
            // Get the authorization URI with the required parameters
            String authorizationUri = spotifyApi.authorizationCodeUri()
                    .scope("streaming,user-read-email,playlist-read-private") // Set optional scopes here
                    .show_dialog(true) // Set optional parameters here
                    .build()
                    .execute()
                    .toString();

            // Redirect the user to the authorization URI
            getUI().ifPresent(ui -> ui.getPage().setLocation(authorizationUri));
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Get the query parameters from the request
        QueryParameters queryParams = event.getLocation().getQueryParameters();

        var qParamsMap = queryParams.getParameters();
        // Check if there is an authorization code or an error
        if (qParamsMap.containsKey("code")) {
            // Get the authorization code from the query parameters
            String code = qParamsMap.get("code").get(0);

            // Call the setSpotifySession method with the authorization code
            SpotifySessionService.setSpotifySession(code);

            // Redirect the user to the home view or any other view
            // UI.getCurrent().navigate("home");
            UI.getCurrent().getPage().getHistory().back();
        } else if (qParamsMap.containsKey("error")) {
            // Get the error from the query parameters
            String error = qParamsMap.get("error").get(0);

            // Display an error message to the user or handle it accordingly
            Notification.show("There was an error: " + error);

            // Redirect the user to the home view or any other view
            // UI.getCurrent().navigate("home");
            UI.getCurrent().getPage().getHistory().back();
        } else {
            // Check if the user has already authorized the application with Spotify
            if (SpotifySessionService.getAuthorizationCodeCredentials() == null) {
                // Display a message to the user explaining why they need to login with Spotify and what benefits they will get from doing so
                add(new Paragraph("You can login with Spotify to access more features, such as getting your profile, playing music, and more."));

                // Add the login button to the view
                add(loginButton);
            } else {
                // Display a message to the user confirming that they have already logged in with Spotify
                add(new Paragraph("You have already logged in with Spotify!"));

                // String songUrl = "https://open.spotify.com/track/7sWRlDoTDX8geTR8zzr2vt?go=1&sp_cid=15a98180ffe5f8eef0455ae534d473b7&utm_source=embed_player_p&utm_medium=desktop&nd=1";
                // // Add a button or a link to the home view or any other view
                // // MusicPlayer player = new MusicPlayer(SpotifySessionService.checkAuthorization(songUrl));

                // String spotifyUrl = "https://open.spotify.com/track/1MDEvpaFk2Ins7N8hGfFlA";

                // Add a button or a link to the home view or any other view
                // add(player, new Button("Return to home view", e -> UI.getCurrent().navigate("home")));
                add(new Button("Return to previous page", e -> UI.getCurrent().getPage().getHistory().back()));
            }
        }
    }

}
