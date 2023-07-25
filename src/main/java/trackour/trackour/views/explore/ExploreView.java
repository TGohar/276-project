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
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.RolesAllowed;
import se.michaelthelin.spotify.model_objects.specification.Category;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.spotify.Explore;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.views.components.responsive.MyBlockResponsiveLayout;

@Route("Explore")
@RouteAlias("explore")
@PageTitle("Login | Trackour")
@PreserveOnRefresh
@RolesAllowed({"USER", "ADMIN"})

public class ExploreView extends MyBlockResponsiveLayout {
    public ExploreView(SecurityViewService securityViewHandler,
    CustomUserDetailsService customUserDetailsService) {
        
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
        
        
        // greetings
        List<Category> categories = xplore.getCategories();
        
        // int columns = 5;
        FlexLayout categoryLayout = new FlexLayout();
        categoryLayout.setFlexGrow(1);
        categoryLayout.setWidthFull();
        // categoryLayout.setSizeFull();
        categoryLayout.getStyle().set("display", "flex");
        categoryLayout.getStyle().set("flex-wrap", "wrap");
        categoryLayout.setAlignItems(FlexLayout.Alignment.CENTER);
        categoryLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER);
        // categoryLayout.getStyle().setBackground("cyan");

        String categoryCardSize = "8.5rem";
        
        try {
            for (Category category : categories) {
                Image coverImage = new Image(category.getIcons()[0].getUrl(), "Category Cover");
                coverImage.setWidth(categoryCardSize);
                coverImage.setHeight(categoryCardSize);
                
                Button catButton = new Button(coverImage);
                catButton.getStyle().setWidth(categoryCardSize);
                catButton.getStyle().setHeight(categoryCardSize);
                catButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                
                Div categoryInfo = new Div(new Text(category.getName()));
                categoryInfo.setWidth(categoryCardSize);
                
                VerticalLayout catLayout = new VerticalLayout();
                catLayout.add(catButton, categoryInfo);
                catLayout.getStyle().setWidth("auto");
                catLayout.getStyle().setHeight("auto");
                
                // if (counter % columns == 0 && counter > 0) {
                //     categoryLayout.add(rowLayout);
                //     rowLayout = new HorizontalLayout(); // Create a new rowLayout
                //     rowLayout.setWidth("100%");
                // }
                
                // catLayout.getStyle().setBackground("cyan");
                // catLayout.getStyle().setMargin("1rem");
                // categoryLayout.getStyle().setBackground("red");
                categoryLayout.add(catLayout);
                // counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // if (rowLayout.getComponentCount() > 0) {
        //     categoryLayout.add(rowLayout);
        // }

        // container for the main contents of this page
        VerticalLayout contentContainer = new VerticalLayout();
        contentContainer.setSizeFull();
        contentContainer.add(
            greetings,
            categoryLayout
        );

        // generate responsive navbar
        NavBar nav = new NavBar(customUserDetailsService, securityViewHandler);
        nav.setContent(contentContainer);
        add(nav);
    }
}
