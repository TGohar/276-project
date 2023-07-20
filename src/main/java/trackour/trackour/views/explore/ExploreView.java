package trackour.trackour.views.explore;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import se.michaelthelin.spotify.model_objects.specification.Category;

import trackour.trackour.spotify.Explore;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;

@Route("Explore")
@PageTitle("Login")
@AnonymousAllowed

public class ExploreView extends VerticalLayout {
    public ExploreView(SecurityViewService securityViewHandler,
            CustomUserDetailsService customUserDetailsService) {

        NavBar nav = new NavBar(customUserDetailsService, securityViewHandler);

        Explore xplore = new Explore();

        Optional<UserDetails> username = securityViewHandler.getSessionOptional();
        String sessionUsername = username.get().getUsername();
        String displayNameString = customUserDetailsService.getByUsername(sessionUsername).get().getDisplayName();

        Icon smile = new Icon(VaadinIcon.SMILEY_O);
        smile.setColor("Pink");
        H1 header = new H1(displayNameString + ", How are you feeling today");
        HorizontalLayout greetings = new HorizontalLayout();

        Span smileSpan = new Span(smile);
        smileSpan.getStyle().set("align-self", "center");

        greetings.setAlignItems(FlexComponent.Alignment.START);
        greetings.add(header, smileSpan);

        add(nav.generateComponent());
        add(greetings);
        List<Category> categories = xplore.getCategories();

        int columns = 5;
        VerticalLayout categoryLayout = new VerticalLayout();
        categoryLayout.setWidth("100%");

        HorizontalLayout rowLayout = new HorizontalLayout();
        rowLayout.setWidth("100%");

        int counter = 0;

        for (Category category : categories) {
            Image coverImage = new Image(category.getIcons()[0].getUrl(), "Category Cover");
            coverImage.setWidth("200px");
            coverImage.setHeight("200px");

            Button catButton = new Button(coverImage);
            catButton.getStyle().setWidth("200px");
            catButton.getStyle().setHeight("200px");
            catButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Div categoryInfo = new Div(new Text(category.getName()));
            categoryInfo.setWidth("200px");

            VerticalLayout catLayout = new VerticalLayout();
            catLayout.add(catButton, categoryInfo);

            if (counter % columns == 0 && counter > 0) {
                categoryLayout.add(rowLayout);
                rowLayout = new HorizontalLayout(); // Create a new rowLayout
                rowLayout.setWidth("100%");
            }

            rowLayout.add(catLayout);
            counter++;
        }

        if (rowLayout.getComponentCount() > 0) {
            categoryLayout.add(rowLayout);
        }
        add(categoryLayout);
    }
}
