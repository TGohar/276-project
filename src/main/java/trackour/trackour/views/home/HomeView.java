package trackour.trackour.views.home;

//import com.vaadin.flow.component.Text;
//import com.vaadin.flow.component.UI;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.html.H1;
//import com.vaadin.flow.component.orderedlayout.FlexComponent;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityService;
import trackour.trackour.security.SecurityViewHandler;

@Route("")
// Admins are users but also have the "admin" special role so pages that can be viewed by
// both users and admins should have the admin role specified as well
@RolesAllowed({"ADMIN", "USER"})
public class HomeView extends VerticalLayout {
    public HomeView(SecurityViewHandler securityViewHandler, SecurityService securityService, CustomUserDetailsService customUserDetailsService) {

        NavBar navigation = new NavBar(securityService,customUserDetailsService,securityViewHandler);

        add(navigation.generateNavBar());
    }
}
