package trackour.trackour.views.components;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyUpEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

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
        searchField.focus();
        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.setWidthFull();
        // simpleSearchComponent.getStyle().set("background-color", "red");
        generateSearchField();
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
}
