package trackour.trackour.views.dashboard;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import trackour.trackour.model.Project;
import trackour.trackour.model.ProjectStatus;

public class ProjectCard extends VerticalLayout {
    H3 title;
    Span description;
    Span createdAt;
    Span status;

    public ProjectCard() {
        this.title = new H3("");
        this.description = new Span("");
        this.createdAt = new Span("");
        this.status = new Span("");
        styleCard();
        add(this.title, this.description, this.createdAt, this.status);
    }

    public ProjectCard(String title, String description, String createdAt, ProjectStatus status) {
        this.title = new H3(title);
        this.description = new Span(description);
        this.createdAt = new Span(createdAt);
        this.status = new Span(status.name());
        styleCard();
        add(this.title, this.description, this.createdAt, this.status);
    }

    public ProjectCard(Project project) {
        this.title = new H3(project.getTitle());
        this.description = new Span(project.getDescription());
        this.createdAt = new Span(project.getCreatedAt().toString());
        this.status = new Span(project.getStatus().name());
        styleCard();
        add(this.title, this.description, this.createdAt, this.status);
    }

    private void styleCard() {
        setWidth("300px");
        setHeight("400px");
        getStyle().set("border-radius", "5px");
        getStyle().set("box-shadow", "0px 0px 10px 0px rgba(0,0,0,0.5)");
        getStyle().set("padding", "10px");
        getStyle().set("margin", "10px");
        getStyle().setBackground("white");
        getStyle().setColor("black");
        createdAt.getStyle().setColor("grey");
        title.getStyle().setColor("black");
        description.getStyle().setColor("grey");
        this.status.getElement().getThemeList().add("badge");
    }

    public H3 getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = new H3(title);
    }

    public Span getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = new Span(description);
    }

    public Span getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = new Span(createdAt);
    }
}
