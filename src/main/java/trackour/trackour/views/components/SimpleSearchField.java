package trackour.trackour.views.components;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyUpEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.QueryParameters;

import trackour.trackour.views.searchResult.SearchResultView;

// import trackour.trackour.security.SecurityViewService;
// import trackour.trackour.views.searchResult.SearchResultView;

/**
 * This Class requires you pass "this" (the view instantiating/calling it) into it
 * @param sourceView
 * @return
 */
public class SimpleSearchField extends HorizontalLayout {

    private TextField searchField;

    public SimpleSearchField() {
        this.searchField = new TextField();
        this.searchField.focus();
        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setWidthFull();
        // simpleSearchComponent.getStyle().set("background-color", "red");
        generateSearchField();
        // add a key up listener to the search field
        searchField.addKeyUpListener(Key.ENTER, event -> {
        // get the current value of the search field
        String searchValue = searchField.getValue();
        // navigate to the search view with the search query as a query parameter
        getUI().ifPresent(ui -> {
            QueryParameters queryParameters = QueryParameters.simple(Map.of("query", searchValue));
            ui.navigate("search", queryParameters);
        });
        });
        this.add(this.searchField);
    }

    public void onEnterKeyUp(ComponentEventListener<KeyUpEvent> listener) {
        searchField.addKeyUpListener(Key.ENTER, listener);
    }

    private void generateSearchField() {
        searchField.setPlaceholder("Search Songs, Albums, Artists");
        searchField.setPrefixComponent(new Icon("lumo", "search"));
        searchField.setMinWidth(80, Unit.PERCENTAGE);
        // searchField.setWidth("80%");
        searchField.setClearButtonVisible(true);
    }

    public Boolean isInSearchResultView(Component navigationTarget) {
        return navigationTarget.getClass().isInstance(SearchResultView.class);
    }

    public String getSearchValue() {
        return this.searchField.getValue();
    }

    public TextField getTextField() {
        return this.searchField;
    }

    /** 
     * 
     public SimpleSearchField() {
     this.searchField = new TextField();
     this.searchField.focus();
     this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
     this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
     this.setWidthFull();
     generateSearchField();
     this.add(this.searchField);
     
     // add a key up listener to the search field
     searchField.addKeyUpListener(Key.ENTER, event -> {
       // get the current value of the search field
       String searchValue = searchField.getValue();
       // navigate to the search view with the search query as a query parameter
       getUI().ifPresent(ui -> {
         QueryParameters queryParameters = QueryParameters.simple(Map.of("query", searchValue));
         ui.navigate("search");
       });
     });
     }
     
     private void generateSearchField() {
     searchField.setPlaceholder("Search Songs, Albums, Artists");
     searchField.setPrefixComponent(new Icon("lumo", "search"));
     searchField.setMinWidth(80, Unit.PERCENTAGE);
     searchField.setClearButtonVisible(true);
     }
    */
}
