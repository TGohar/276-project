package trackour.trackour.views.components;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;

@CssImport("./styles/my-block__responsive-layout.css")
public class SimpleCarousel extends HorizontalLayout {

    Scroller thisScroller;
    Double scrollLeftValue;
    Double scrollTopValue;
    int currentChildIndex = 0;
    List<AlbumSimplified> itemsList;

    public SimpleCarousel(List<AlbumSimplified> itemsList) {
        this.thisScroller = new Scroller();
        thisScroller.setMinWidth(300, Unit.PIXELS);
        this.scrollLeftValue = 0.0;
        this.scrollTopValue = 0.0;
        this.itemsList = itemsList;
    }

    // the component that instantiates this class runs this to return the whole
    // carousel
    // effectively, this is the main method for this class
    private Scroller genCarouselInnerScroller() {
        // create a horizontal layout for the cards
        HorizontalLayout tLayout = new HorizontalLayout();

        // set the scroll direction and behavior of the scroller
        this.thisScroller.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);
        this.thisScroller.getElement().getStyle().set("scroll-behavior", "smooth");

        // loop through the items list and create a card for each album
        int count = 0;
        for (AlbumSimplified album : this.itemsList) {
            // check if the limit of 8 cards is reached
            if (count >= 8) {
                break;
            }

            // create an image from the album cover url
            Image coverImage = new Image(album.getImages()[0].getUrl(), "Album Cover");

            // set the height and width of the image to 200px
            coverImage.setHeight("250px");
            coverImage.setWidth("250px");

            // create a button with the image as the content
            Button albumButton = new Button(coverImage);
            albumButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            // center the button in its layout
            albumButton.getStyle().set("margin", "auto");
            // albumButton.setSizeFull();
            albumButton.setHeight("250px");
            albumButton.setWidth("250px");

            // create a div with the album name as the text
            String albumName = album.getName();
            ExternalUrl url = album.getExternalUrls();
            String URL = url.toString();
            Pattern pattern = Pattern.compile("https://[\\w./-]+");
            Matcher matcher = pattern.matcher(URL);
            String extractedUrl = "";
            if (matcher.find()) {
                extractedUrl = matcher.group();
            } else {
                System.out.println("URL not found or invalid format.");

            }
            System.out.println(extractedUrl);

            Anchor albumLink = new Anchor(extractedUrl, albumName);
            albumLink.setTarget("_blank");
            Div albumInfo = new Div(new H2(albumLink));

            // create a vertical layout for the card and add the button and the info
            VerticalLayout albumCard = new VerticalLayout();
            // albumCard.getStyle().setBorder("solid");
            // albumCard.add(albumButton);
            albumCard.add(albumButton, albumInfo);

            // set the fixed height and width of the card
            albumCard.setHeight("350px");
            albumCard.setWidth("350px");

            // center the card in its layout
            albumCard.getStyle().set("margin", "auto");

            // add the card to the horizontal layout
            tLayout.add(albumCard);

            // increment the count
            count++;
        }

        // set the content of the scroller to the horizontal layout
        this.thisScroller.setContent(tLayout);
        this.thisScroller.getContent().getElement().setAttribute("id", "my-scroller-horizontal");

        // return the scroller
        return this.thisScroller;
    }

    public HorizontalLayout generateComponent() {
        HorizontalLayout carouselContainer = new HorizontalLayout();
        carouselContainer.setWidthFull();

        // carouselContainer.getStyle().setOverflow(Overflow.HIDDEN);
        carouselContainer.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        // carouselContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
        carouselContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Icon swipeLeft = new Icon("lumo", "angle-left");
        Icon swipeRight = new Icon("lumo", "angle-right");
        Scroller scroller = genCarouselInnerScroller();

        Button leftCarouselButton = new Button(swipeLeft);
        leftCarouselButton.addClickListener(ev -> {
            // Call the scrollLeft function from the JavaScript file
            leftCarouselButton.getElement().executeJs(
                    "function scrollL() {" +
                            "   const element = document.getElementById('my-scroller-horizontal');" +
                            "   if (element.offsetWidth <= 300) {" +
                            "   element.scrollLeft -= 300;" +
                            "   return;" +
                            "}" +
                            "   element.scrollLeft -= element.offsetWidth;" +
                            "}" +
                            "scrollL();");
        });
        leftCarouselButton.setHeightFull();

        Button rightCarouselButton = new Button(swipeRight);
        rightCarouselButton.addClickListener(ev -> {
            // Call the scrollRight function from the JavaScript file
            rightCarouselButton.getElement().executeJs(
                    "function scrollR() {" +
                            "   const element = document.getElementById('my-scroller-horizontal');" +
                            "   if (element.offsetWidth <= 300) {" +
                            "   element.scrollLeft += 300;" +
                            "   return;" +
                            "   }" +
                            "   element.scrollLeft += element.offsetWidth;" +
                            "}" +
                            "scrollR();");
        });
        rightCarouselButton.setHeightFull();

        // attachScrollEventListener(this.thisScroller);
        carouselContainer.add(
                leftCarouselButton,
                scroller,
                rightCarouselButton);
        return carouselContainer;
    }
}
