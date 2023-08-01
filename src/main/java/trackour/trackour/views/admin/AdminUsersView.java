package trackour.trackour.views.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.klaudeta.PaginatedGrid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.RolesAllowed;
import trackour.trackour.model.CustomUserDetailsService;
import trackour.trackour.model.User;
// import trackour.trackour.security.SecurityViewService;
import trackour.trackour.security.SecurityViewService;
import trackour.trackour.views.components.NavBar;

@Route("admin/view-users")
@RouteAlias("view-users")
@PreserveOnRefresh
@PageTitle("Admin Page | Trackour")
// Admins are users but also have the "admin" special role so pages that can be
// viewed by
// both users and admins should have the admin role specified as well
@RolesAllowed({ "ADMIN" })
public class AdminUsersView extends VerticalLayout {

        @Autowired
        SecurityViewService securityViewHandler;

        @Autowired
        CustomUserDetailsService customUserDetailsService;

        private NavBar navigationComponent;

        public AdminUsersView(SecurityViewService securityViewHandler,
                        CustomUserDetailsService customUserDetailsService) {
                this.securityViewHandler = securityViewHandler;
                this.customUserDetailsService = customUserDetailsService;

                this.setHeightFull();

                // generate responsive navbar
                this.navigationComponent = generateNavBar();
                navigationComponent.setContent(generatePaginationGridLayout());
                add(navigationComponent);
        }
        
        private NavBar generateNavBar() {
                return new NavBar(customUserDetailsService, securityViewHandler);
        }

        private VerticalLayout generatePaginationGridLayout() {
                VerticalLayout gridLayout = new VerticalLayout();
                List<User> allUsers = customUserDetailsService.getAll();
                PaginatedGrid<User, ?> grid = new PaginatedGrid<>();
                grid.setSizeFull();
                grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
                
                grid.addColumn(User::getUid).setHeader("Uid").setSortable(true);
                grid.addColumn(User::getDisplayName).setHeader("Display Name").setSortable(true);
                grid.addColumn(User::getEmail).setHeader("Email").setSortable(true);
                grid.addColumn(User::getUsername).setHeader("Username").setSortable(true);
                grid.addColumn(User::getRoles).setHeader("Roles").setSortable(true);
                
                grid.setItems(allUsers);

                GridListDataView<User> dataView = grid.setItems(allUsers);
                
                // UserGridFilter userFilter = new UserGridFilter(dataView);
                
                grid.getHeaderRows().clear(); 
                // HeaderRow headerRow = grid.appendHeaderRow();

                // headerRow.getCell(uid).setComponent(createFilterHeader("Uid", userFilter::setUid));
                // headerRow.getCell(displayName).setComponent(createFilterHeader("Display Name", userFilter::setDisplayName));
                // headerRow.getCell(email).setComponent(createFilterHeader("Email", userFilter::setEmail));
                // headerRow.getCell(username).setComponent(createFilterHeader("Username", userFilter::setUsername));
                // headerRow.getCell(roles).setComponent(createFilterHeader("Roles", userFilter::setRole));
                
                // Sets the max number of items to be rendered on the grid for each page
                grid.setPageSize(7);
                
                // Sets how many pages should be visible on the pagination before and/or after the current selected page
                grid.setPaginatorSize(3);
                
                grid.addComponentColumn((ev) -> deleteUserButton(ev, dataView));

                grid.setPaginationLocation(PaginatedGrid.PaginationLocation.BOTTOM);
                grid.setPaginationVisibility(true);
                gridLayout.setSizeFull();
                
                gridLayout.add(grid);
                gridLayout.getStyle().setPadding("0%");
                gridLayout.getStyle().setMargin("0%");
                return gridLayout;
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

        
        /**
         private static Component createFilterHeader(String labelText, Consumer<String> filterChangeStringConsumer) {
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
         private VerticalLayout generateGrid() {
                 VerticalLayout gridLayout = new VerticalLayout();
                 List<User> allUsers = customUserDetailsService.getAll();
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
                 grid1.setItems(allUsers);
                 
                 GridListDataView<User> dataView1 = grid1.setItems(allUsers);
 
                 UserGridFilter userFilter1 = new UserGridFilter(dataView1);
 
                 grid1.getHeaderRows().clear();
                 HeaderRow headerRow1 = grid1.appendHeaderRow();
 
                 headerRow1.getCell(uidColumn).setComponent(
                                 createFilterHeader("Uid", userFilter1::setUid));
                 headerRow1.getCell(displayNameColumn).setComponent(
                                 createFilterHeader("Display Name", userFilter1::setDisplayName));
                 headerRow1.getCell(emailColumn).setComponent(
                                 createFilterHeader("Email", userFilter1::setEmail));
                 headerRow1.getCell(usernameColumn).setComponent(
                                 createFilterHeader("Username", userFilter1::setUsername));
                 headerRow1.getCell(roleColumn).setComponent(
                                 createFilterHeader("Roles", userFilter1::setRole));
 
                 // column to delete users
                 grid1.addComponentColumn((ev) -> deleteUserButton(ev, dataView1));
         }
         * 
         * @return
         */
}
