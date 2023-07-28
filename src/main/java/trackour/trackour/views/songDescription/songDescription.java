package trackour.trackour.views.songDescription;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import trackour.trackour.views.api.APIController;

@Route("songDescription/:trackID?")
@PageTitle("Description")
@AnonymousAllowed
@Component
public class songDescription extends VerticalLayout implements BeforeEnterObserver {

    private String trackID;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        trackID = event.getRouteParameters().get("trackID").get();

        H1 songTitle = new H1(APIController.getTrackName(trackID));
        H2 songArtist = new H2("by " + APIController.getArtistFromTrack(trackID)[0].getName());
        Image albumCover = new Image(APIController.getAlbumImage(APIController.getAlbum(trackID).getId())[0].getUrl(), "Album Cover");
        albumCover.setWidth("300px");
        albumCover.setHeight("300px");

        VerticalLayout titleAndArtist = new VerticalLayout();
        titleAndArtist.add(songTitle, songArtist);

        HorizontalLayout pageHeading = new HorizontalLayout();
        pageHeading.setAlignItems(FlexComponent.Alignment.END);
        pageHeading.add(albumCover, titleAndArtist);

        H2 audioDetailsHeading = new H2("Audio Details");
        audioDetailsHeading.getStyle().setPadding("50px 0px 0px 10px");
        
        TextField acousticnessDetail = new TextField();
        acousticnessDetail.setReadOnly(true);
        acousticnessDetail.setLabel("Acousticness");
        acousticnessDetail.setValue(String.valueOf(APIController.getAcousticness(trackID)));

        TextField danceabilityDetail = new TextField();
        danceabilityDetail.setReadOnly(true);
        danceabilityDetail.setLabel("Danceability");
        danceabilityDetail.setValue(String.valueOf(APIController.getDanceability(trackID)));
        
        TextField energyDetail = new TextField();
        energyDetail.setReadOnly(true);
        energyDetail.setLabel("Energy");
        energyDetail.setValue(String.valueOf(APIController.getEnergy(trackID)));

        TextField instrumentalnessDetail = new TextField();
        instrumentalnessDetail.setReadOnly(true);
        instrumentalnessDetail.setLabel("Instrumentalness");
        instrumentalnessDetail.setValue(String.valueOf(APIController.getInstrumentalness(trackID)));

        TextField keyDetail = new TextField();
        keyDetail.setReadOnly(true);
        keyDetail.setLabel("Key");
        keyDetail.setValue(String.valueOf(APIController.getKey(trackID)));

        TextField livenessDetail = new TextField();
        livenessDetail.setReadOnly(true);
        livenessDetail.setLabel("Liveness");
        livenessDetail.setValue(String.valueOf(APIController.getLiveness(trackID)));

        TextField loudnessDetail = new TextField();
        loudnessDetail.setReadOnly(true);
        loudnessDetail.setLabel("Loudness");
        loudnessDetail.setValue(String.valueOf(APIController.getLoudness(trackID)));

        TextField modeDetail = new TextField();
        modeDetail.setReadOnly(true);
        modeDetail.setLabel("Mode");
        modeDetail.setValue(String.valueOf(APIController.getMode(trackID)));

        TextField tempoDetail = new TextField();
        tempoDetail.setReadOnly(true);
        tempoDetail.setLabel("Tempo");
        tempoDetail.setValue(String.valueOf(APIController.getTempo(trackID)));

        TextField timeSignatureDetail = new TextField();
        timeSignatureDetail.setReadOnly(true);
        timeSignatureDetail.setLabel("Time Signature");
        timeSignatureDetail.setValue(String.valueOf(APIController.getTimeSignature(trackID)));

        TextField valenceDetail = new TextField();
        valenceDetail.setReadOnly(true);
        valenceDetail.setLabel("Valence");
        valenceDetail.setValue(String.valueOf(APIController.getValence(trackID)));

        VerticalLayout column1 = new VerticalLayout();
        column1.add(acousticnessDetail, danceabilityDetail, energyDetail, instrumentalnessDetail);

        VerticalLayout column2 = new VerticalLayout();
        column2.add(keyDetail, livenessDetail, loudnessDetail, modeDetail);

        VerticalLayout column3 = new VerticalLayout();
        column3.add(tempoDetail, timeSignatureDetail, valenceDetail);

        HorizontalLayout audioDetailsBody = new HorizontalLayout();
        audioDetailsBody.add(column1, column2, column3);
        
        add(pageHeading, audioDetailsHeading, audioDetailsBody);
    }
}
