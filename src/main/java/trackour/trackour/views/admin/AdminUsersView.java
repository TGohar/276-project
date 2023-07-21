package trackour.trackour.views.admin;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

// import org.springframework.security.core.userdetails.UserDetailsService;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;
import trackour.trackour.model.User;
// import trackour.trackour.security.SecurityViewService;

@Route("admin/view-users")
// Admins are users but also have the "admin" special role so pages that can be
// viewed by
// both users and admins should have the admin role specified as well
@RolesAllowed({ "ADMIN" })
public class AdminUsersView extends VerticalLayout {

        @Autowired
        SecurityViewService securityViewHandler;

        @Autowired
        CustomUserDetailsService customUserDetailsService;

        public AdminUsersView(SecurityViewService securityViewHandler,
                        CustomUserDetailsService customUserDetailsService) {
                this.securityViewHandler = securityViewHandler;
                this.customUserDetailsService = customUserDetailsService;

                this.setHeightFull();
                AppLayout navigationComponent = generateNavBar();
                navigationComponent.setContent(generateGrid());
                add(navigationComponent);
        }

        private AppLayout generateNavBar() {
                NavBar nav = new NavBar(customUserDetailsService, securityViewHandler);
                return nav.generateNavComponent(false);
        }

        private Grid<User> generateGrid() {
                Grid<User> grid1 = new Grid<>(User.class, false);
        
                grid1.setSizeFull();
                grid1.setColumnRendering(ColumnRendering.LAZY);
                Grid.Column<User> uidColumn = grid1.addColumn(User::getUid).setHeader("Uid").setSortable(true);
                Grid.Column<User> displayNameColumn = grid1.addColumn(User::getDisplayName).setHeader("Display Name")
                                .setSortable(true);
                Grid.Column<User> usernameColumn = grid1.addColumn(User::getUsername).setHeader("Username")
                                .setSortable(true);
                Grid.Column<User> emailColumn = grid1.addColumn(User::getEmail).setHeader("Email").setSortable(true);

                Grid.Column<User> roleColumn = grid1.addColumn(User::getRoles).setHeader("Roles").setSortable(true);
                grid1.addColumn(User::getPasswordResetToken).setHeader("Password Reset Token");

                grid1.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
                // grid1.addColumn(null, null)
                List<User> allUsers = customUserDetailsService.getAll();
                grid1.setItems(allUsers);
                GridListDataView<User> dataView = grid1.setItems(allUsers);

                UserGridFilter userFilter = new UserGridFilter(dataView);

                grid1.getHeaderRows().clear();
                HeaderRow headerRow = grid1.appendHeaderRow();

                headerRow.getCell(uidColumn).setComponent(
                                createFilterHeader("Uid", userFilter::setUid));
                headerRow.getCell(displayNameColumn).setComponent(
                                createFilterHeader("Display Name", userFilter::setDisplayName));
                headerRow.getCell(emailColumn).setComponent(
                                createFilterHeader("Email", userFilter::setEmail));
                headerRow.getCell(usernameColumn).setComponent(
                                createFilterHeader("Username", userFilter::setUsername));
                headerRow.getCell(roleColumn).setComponent(
                                createFilterHeader("Roles", userFilter::setRole));

                // column to delete users
                grid1.addComponentColumn((ev) -> deleteUserButton(ev, dataView));

                return grid1;
        }

        // Button deleteUserButton(Grid<User> grid1, Column<User> uidColumn){
        Button deleteUserButton(User userRecord, GridListDataView<User> dataView) {
                Icon delBtnIcon = new Icon(VaadinIcon.TRASH);
                Button delButton = new Button(delBtnIcon, (ev) -> {
                        // delete that user record
                        this.customUserDetailsService.delete(userRecord.getUid());
                        boolean isAdminDeletingThemself = securityViewHandler.getSessionOptional().get().getUsername()
                                        .equals(userRecord.getUsername());

                        if (isAdminDeletingThemself) {
                                securityViewHandler.logOut();
                                this.getUI().get().getPage().setLocation("");
                                return;
                        }
                        // this.getUI().get().getPage().setLocation("admin/view-users");
                        this.getUI().get().getPage().reload();
                });
                return delButton;
        }

        private static Component createFilterHeader(String labelText,
                        Consumer<String> filterChangeStringConsumer) {
                Text label = new Text(labelText);
                TextField textField = new TextField();
                textField.setValueChangeMode(ValueChangeMode.EAGER);
                textField.setClearButtonVisible(true);
                textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
                textField.setWidthFull();
                textField.getStyle().set("max-width", "100%");
                textField.addValueChangeListener(
                                e -> filterChangeStringConsumer.accept(e.getValue()));
                VerticalLayout layout = new VerticalLayout(label, textField);
                layout.getThemeList().clear();
                layout.getThemeList().add("spacing-xs");

                return layout;
        }
}
