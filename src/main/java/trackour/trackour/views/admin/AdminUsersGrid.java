package trackour.trackour.views.admin;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import trackour.trackour.model.user.User;

public class AdminUsersGrid extends VerticalLayout {

    Grid<User> grid;
    
    AdminUsersGrid(List<User> users){
        this.grid = new Grid<>(User.class, false);
        grid.addColumn(User::getUid).setHeader("Uid");
        grid.addColumn(User::getDisplayName).setHeader("Display name");
        grid.addColumn(User::getUsername).setHeader("Username");
        grid.addColumn(User::getEmail).setHeader("Email");
        grid.addColumn(User::getRoles).setHeader("Role");
        grid.addComponentColumn(x -> new Button("Delete"));
        grid.setItems(users);
    }

    public void setItems(List<User> users) {
        grid.setItems(users);
    }
}
