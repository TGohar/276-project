package trackour.trackour.views.components;

import java.util.List;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.Style.Overflow;

import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;

public class SimpleCarousel extends HorizontalLayout {

    Scroller thisScroller;
    Double scrollLeftValue;
    Double scrollTopValue;
    DomEvent scrollEvent;
    private Double maxScrollLeft;
    private Double maxVisibleScrollLeft;
    List<AlbumSimplified> itemsList;
    public SimpleCarousel(List<AlbumSimplified> itemsList) {
        this.thisScroller = new Scroller();
        this.scrollLeftValue = 0.0;
        this.scrollTopValue = 0.0;
        this.scrollEvent = null;
        this.maxScrollLeft = 250.0;
        this.maxVisibleScrollLeft = 0.0;
        this.itemsList = itemsList;
        this.attachScrollValuesUpdate();
    }

    private Scroller genCarouselInnerScroller() {
        HorizontalLayout tLayout = new HorizontalLayout();
        tLayout.setWidthFull();
        tLayout.setHeightFull();
        // this.thisScroller.getStyle().setOverflow(Overflow.HIDDEN);
        attachScrollValuesUpdate();
        // set the width of the scroller area to 100% to not overflow over the side of the page
        this.thisScroller.setWidthFull();
        this.thisScroller.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);
        this.thisScroller.getElement().getStyle().set("scroll-behavior", "smooth");
        try {
            for (AlbumSimplified album : this.itemsList) {
            Image coverImage = new Image(album.getImages()[0].getUrl(), "Album Cover");
            coverImage.setWidth("200px");
            coverImage.setHeight("200px");
            Icon playIcon = new Icon(VaadinIcon.PLAY_CIRCLE);
            playIcon.setColor("Green");
            playIcon.setClassName("playicon");

            Button albumButton = new Button(coverImage);
            // albumButton.setIcon(playIcon);
            albumButton.getStyle().setWidth("200px");
            albumButton.getStyle().setHeight("200px");
            albumButton.addClassName("albumB");
            albumButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Div albumInfo = new Div(new Text(album.getName()));
            albumInfo.setWidth("200px");

            VerticalLayout albumLayout = new VerticalLayout();
            albumLayout.add(albumButton, albumInfo);

            tLayout.add(albumLayout);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.thisScroller.setContent(tLayout);
        return this.thisScroller;
    }

    public HorizontalLayout generateComponent() {
        HorizontalLayout carouselContainer = new HorizontalLayout();
        carouselContainer.setWidthFull();
        carouselContainer.getStyle().setOverflow(Overflow.HIDDEN);
        // carouselContainer.getStyle().setBackground("red");
        carouselContainer.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        // carouselContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
        carouselContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Icon swipeLeft = new Icon("lumo", "angle-left");
        // swipeLeft.setSize();
        Icon swipeRight = new Icon("lumo", "angle-right");
        // swipeRight.setSize("xl");
        Scroller scroller = genCarouselInnerScroller();
        // attachScrollValuesUpdate(scroller);
        Button leftCarouselButton = new Button(swipeLeft, ev -> scrollLeft(scroller));
        leftCarouselButton.setHeightFull();
        Button rightCarouselButton = new Button(swipeRight, ev -> scrollRight(scroller));
        rightCarouselButton.setHeightFull();
        leftCarouselButton.getStyle().setBackground("red");
        rightCarouselButton.getStyle().setBackground("red");

        
        carouselContainer.add(
            leftCarouselButton,
            scroller,
            rightCarouselButton
            );
        return carouselContainer;
    }

    private void attachScrollValuesUpdate() {

        // updater
        this.thisScroller.getElement().addEventListener("scroll", ev -> {
            this.scrollEvent = ev;
            // Get the current scrollLeft property value from the browser
            Double scrollLeft = ev.getEventData().getNumber("element.scrollLeft");
            Double maxScrollLeft = ev.getEventData().getNumber("element.scrollWidth");
            Double maxVisibleScrollLeft = ev.getEventData().getNumber("element.clientWidth");

            this.scrollLeftValue = scrollLeft;
            this.maxScrollLeft = maxScrollLeft;
            this.maxVisibleScrollLeft = maxVisibleScrollLeft;

            this.thisScroller.getElement().setProperty("scrollLeft", this.scrollLeftValue);
            this.thisScroller.getElement().setProperty("scrollWidth", this.maxScrollLeft);
            this.thisScroller.getElement().setProperty("clientWidth", this.maxVisibleScrollLeft);

            // System.out.println("[upd]scrollLeft: " + scrollLeftValue);
        }).addEventData("element.scrollLeft")
        .addEventData("element.scrollWidth")
        .addEventData("element.clientWidth");
    }

    // this.thisScroller.scrollIntoView(null);
    private void scrollLeft(Scroller scroller) {
        // Decrement the scrollLeft property value by 10
        this.scrollLeftValue = this.scrollLeftValue - 250;

        // prevent scrolling from going below 0
        if (this.scrollLeftValue < 0.0) {
            this.scrollLeftValue = 0.0;
        }

        // System.out.println("scrolled to: " + scrollLeftValue);
        
        // Set the new scrollLeft property value
        scroller.getElement().setProperty("scrollLeft", this.scrollLeftValue);
        attachScrollValuesUpdate();
    }
    private void scrollRight(Scroller scroller) {
        // Calculate the maximum possible value for scrollLeft/ max scrollable value
        Double maxScrollableLeft = this.maxScrollLeft - this.maxVisibleScrollLeft;

        // System.out.println("maxScrollableLeft:" + maxScrollableLeft);
        
        // Increment the scrollLeft property value by 10
        this.scrollLeftValue = this.scrollLeftValue + 250;

        if (this.scrollLeftValue > maxScrollableLeft  - 250) {
            this.scrollLeftValue = maxScrollableLeft;
        }
        
        // Set the new scrollLeft property value
        scroller.getElement().setProperty("scrollLeft", this.scrollLeftValue);
        attachScrollValuesUpdate();
    }
}
