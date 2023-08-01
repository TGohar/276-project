package trackour.trackour.views.components.responsive;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

@CssImport("./styles/my-block__responsive-layout.css")
public class MyBlockResponsiveLayout extends VerticalLayout  {

    // Define an interface for the callback
    public interface UpdateCallback {
        // The method to be implemented by the callback
        void onUpdate(Integer browserWidth);
    }

    // Make the browserWidth field private to hide it from other classes
    protected Integer browserWidth;
    // Make the listener field private to hide it from other classes
    private Registration listener;

    public MyBlockResponsiveLayout() {
        setWidthFull();
        setHeightFull();
    }

    // Make the update method protected to allow subclasses to override or call it
    protected void update(int browserWidth) {
        this.browserWidth = browserWidth;
        System.out.println("browserWidth: " + this.browserWidth);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        // shooting out as many async requests for the damn width int to try to make sure the client's responsive components update asap
        super.onAttach(attachEvent);
        // browser window dimensions change listener to observe width change
        getUI().ifPresent(ui -> this.listener = ui.getPage().addBrowserWindowResizeListener(event -> {
                update(event.getWidth());
        }));
        // retrieve client details async
        getUI().ifPresent(ui -> ui.getPage().retrieveExtendedClientDetails(receiver -> {
            int browserWidth = receiver.getBodyClientWidth();
            update(browserWidth);
        }));
        // direct async jscode
        getUI().ifPresent(ui -> ui.getPage().executeJs("return window.innerWidth").then(width -> {
            int w = (int) width.asNumber();
            update(w);
        }));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Listener needs to be eventually removed in order to avoid resource leak
        listener.remove();
        super.onDetach(detachEvent);
    }

    // Add a method that takes a callback as a parameter and invokes it when the browser width changes
    public void addUpdateListener(UpdateCallback callback) {
        // Register a property change listener for the browser width property
        getElement().addPropertyChangeListener("browserWidth", e -> {
            // Get the browser width from the event
            Integer browserWidth = 0;
            if (e.getValue() instanceof Integer) { // Check if e.getValue() is an Integer object
                // Get the browser width from the property change event
                browserWidth = (Integer) e.getValue(); // This is a valid cast
                // Invoke the callback with the browser width as an argument
                callback.onUpdate(browserWidth);
            }
        });
    }

    // helper method to make a given component hidden or not(animated)
    // overload to set default boolean to false
    public void hideComponent(Component component, int hideBelowWidth) {
        hideComponent(component, hideBelowWidth, false);
      }
    // reverse reverses the if condition
    public void hideComponent(Component component, int hideBelowWidth, boolean reverse) {
    boolean doHide = reverse ? 
    browserWidth >= hideBelowWidth : 
    browserWidth < hideBelowWidth;
    if (doHide) {
        // hide the component visually using CSS
        component.addClassName("responsive-layout-hidden");
    } else {
        // show the component visually using CSS
        component.removeClassName("responsive-layout-hidden");
    }
    }
      
}
