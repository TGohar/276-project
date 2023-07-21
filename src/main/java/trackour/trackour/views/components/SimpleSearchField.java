package trackour.trackour.views.components;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class SimpleSearchField extends HorizontalLayout {

    public SimpleSearchField() {}

    public HorizontalLayout generateComponent() {
        HorizontalLayout simpleSearchComponent = new HorizontalLayout();
        simpleSearchComponent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        simpleSearchComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        simpleSearchComponent.setWidthFull();
        // simpleSearchComponent.getStyle().set("background-color", "red");
        simpleSearchComponent.add(generateSearchField());
        return simpleSearchComponent;
    }

    private TextField generateSearchField() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search Songs, Albums, Artists");
        searchField.setPrefixComponent(new Icon("lumo", "search"));
        searchField.setMinWidth(60, Unit.PERCENTAGE);
        // searchField.setWidthFull();
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(e -> {
            
            String searchValue = e.getValue();

            if(searchValue!=null && searchValue.length()!=0){
                searchField.getUI().ifPresent(ui -> ui.navigate("searchResult"));
            }
            else{
                Notification.show("Please enter the name of the song, album or artist you want to search"); 
            }
        });
        return searchField;
    }
}
