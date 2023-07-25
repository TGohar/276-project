package trackour.trackour.views.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import trackour.trackour.model.Project;
import trackour.trackour.model.ProjectStatus;

public class ProjectCard extends VerticalLayout {
    H3 title;
    TextArea description;
    Span createdAt;
    Span status;
    Button deleteButton;
    Project project;

    public ProjectCard(Project project) {
        this.project = project;
        this.title = new H3(project.getTitle());
        this.description = new TextArea();
        this.description.setLabel("Description");
        this.description.setValue(project.getDescription());
        description.setReadOnly(true);
        this.createdAt = new Span(project.getCreatedAt().toString());
        this.status = new Span(project.getStatus().name());
        this.deleteButton = new Button("Delete"); // initialize the button
        styleCard();
        addComponents();
        addClickListener();
    }

    // Extract common methods to avoid repeating code
    private void styleCard() {
        setWidth("300px");
        setHeight("400px");
        getStyle().set("border-radius", "5px");
        getStyle().set("box-shadow", "0px 0px 10px 0px rgba(0,0,0,0.5)");
        getStyle().set("padding", "10px");
        getStyle().set("margin", "10px");
        
        // Use a different background color or gradient for the card
        getStyle().setBackground("linear-gradient(to right bottom, #00b4db, #0083b0)");
        
        // Use a different font size or weight for the title, description, date, and status
        title.getStyle().set("font-size", "24px");
        title.getStyle().set("font-weight", "bold");
        description.getStyle().set("font-size", "16px");
        description.getStyle().set("font-weight", "normal");
        createdAt.getStyle().set("font-size", "14px");
        createdAt.getStyle().set("font-weight", "lighter");
        status.getStyle().set("font-size", "18px");
        status.getStyle().set("font-weight", "medium");

        // Add some icons to the title, description, date, and status
        Icon titleIcon = new Icon(VaadinIcon.PENCIL);
        Icon descriptionIcon = new Icon(VaadinIcon.TEXT_LABEL);
        Icon dateIcon = new Icon(VaadinIcon.CALENDAR);
        
        // Use a different icon for the status depending on its value
        Icon statusIcon;
        if (project.getStatus() == ProjectStatus.IN_PROGRESS) {
            statusIcon = new Icon(VaadinIcon.HOURGLASS);
            statusIcon.setColor("yellow");
            status.getStyle().setColor("yellow");
            
        } else if (project.getStatus() == ProjectStatus.COMPLETED) {
            statusIcon = new Icon(VaadinIcon.CHECK);
            statusIcon.setColor("green");
            status.getStyle().setColor("green");
            
        } else {
            statusIcon = new Icon(VaadinIcon.CLOSE);
            statusIcon.setColor("red");
            status.getStyle().setColor("red");
            
        }
        // Add some spacing between the icons and the texts
        titleIcon.getStyle().set("margin-right", "5px");
        descriptionIcon.getStyle().set("margin-right", "5px");
        dateIcon.getStyle().set("margin-right", "5px");
        statusIcon.getStyle().set("margin-right", "5px");
    }

    private void addComponents() {
        // Create a horizontal layout for the title and the button
        HorizontalLayout titleAndButton = new HorizontalLayout();
        titleAndButton.setWidthFull(); // set the width to full
        titleAndButton.setAlignItems(Alignment.CENTER); // align the items to the center
        titleAndButton.add(title); // add the title to the left
        titleAndButton.setFlexGrow(1, title); // make the title grow to fill the space
        titleAndButton.add(deleteButton); // add the button to the right

        VerticalLayout descriptionArea = new VerticalLayout();
        description.setMinHeight("100px");
        description.setMaxHeight("150px");
        // Set the width and height to 100%
        descriptionArea.setSizeFull();
        // Set the overflow style to auto or scroll
        descriptionArea.getElement().getStyle().set("overflow-y", "auto"); // enable vertical scroll bar
        descriptionArea.getElement().getStyle().set("overflow-x", "hidden"); // disable horizontal scroll bar
        descriptionArea.add(description);
        
        
        // Create a flex layout for the description and date
        FlexLayout descriptionAndDate = new FlexLayout();
        descriptionAndDate.setWidthFull(); // set the width to full
        descriptionAndDate.setFlexDirection(FlexDirection.COLUMN); // set the flex direction to column
        descriptionAndDate.add(descriptionArea, createdAt); // add the description and date

        // Add the horizontal layout and the flex layout to the vertical layout
        add(titleAndButton, descriptionAndDate, status);
    }

    private void addClickListener() {
        deleteButton.addClickListener(event -> { // add a click listener to the button
            Component parent = getParent().get(); // get the parent component of the card
            if (parent instanceof HasComponents) { // check if the parent has components
                // ((HasComponents) parent).remove(this); // remove the card from the parent
            }
        });
    }

    public H3 getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = new H3(title);
    }

    public TextArea getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = new TextArea(description);
    }

    public Span getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = new Span(createdAt);
    }
}

