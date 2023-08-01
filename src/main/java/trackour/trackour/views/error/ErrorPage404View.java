package trackour.trackour.views.error;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

@Route(value = "error")
@PageTitle("Error 404 - Page Not Found | Trackour")
@AnonymousAllowed
public class ErrorPage404View extends VerticalLayout implements BeforeLeaveObserver {

    public ErrorPage404View() {
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        String emoji1 = "🤗";

        H1 header1 = new H1("Lost? ");
        H4 header4 = new H4("You know, the magic thing about ");
        Anchor homeLink = new Anchor("", "Home"+emoji1);
        Paragraph p1 = new Paragraph(" is that it feels good to leave, and it feels EVEN better to come back.");
        header1.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        header4.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        header1.add(header4);
        header4.add(homeLink);
        header4.add(p1);
        add(header1);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        // reroute to error page
        if (event.hasUnknownReroute()){
            System.out.println("Rerouting to Error Page!");
        }
    }

}