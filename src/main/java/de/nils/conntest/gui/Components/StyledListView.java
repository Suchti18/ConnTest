package de.nils.conntest.gui.Components;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;

import java.util.Set;

public class StyledListView<T> extends ListView<T>
{
    private final ScrollBar vBar = new ScrollBar();
    private final ScrollBar hBar = new ScrollBar();

    public StyledListView()
    {
        super();

        skinProperty().addListener(it ->
        {
            // first bind, then add new scrollbars, otherwise the new bars will be found
            bindScrollBars();
            getChildren().addAll(vBar, hBar);
        });

        getStyleClass().add("pretty-list-view");

        vBar.setManaged(false);
        vBar.setOrientation(Orientation.VERTICAL);
        vBar.getStyleClass().add("pretty-scroll-bar");
        vBar.visibleProperty().bind(vBar.visibleAmountProperty().isNotEqualTo(0));

        hBar.setManaged(false);
        hBar.setOrientation(Orientation.HORIZONTAL);
        hBar.getStyleClass().add("pretty-scroll-bar");
        hBar.visibleProperty().bind(hBar.visibleAmountProperty().isNotEqualTo(0));
    }

    private void bindScrollBars()
    {
        final Set<Node> nodes = lookupAll("VirtualScrollBar");

        for(Node node : nodes)
        {
            if(node instanceof ScrollBar bar)
            {
                if(bar.getOrientation().equals(Orientation.VERTICAL))
                {
                    bindScrollBars(vBar, bar);
                }
                else if(bar.getOrientation().equals(Orientation.HORIZONTAL))
                {
                    bindScrollBars(hBar, bar);
                }
            }
        }
    }

    private void bindScrollBars(ScrollBar scrollBarA, ScrollBar scrollBarB)
    {
        scrollBarA.valueProperty().bindBidirectional(scrollBarB.valueProperty());
        scrollBarA.minProperty().bindBidirectional(scrollBarB.minProperty());
        scrollBarA.maxProperty().bindBidirectional(scrollBarB.maxProperty());
        scrollBarA.visibleAmountProperty().bindBidirectional(scrollBarB.visibleAmountProperty());
        scrollBarA.unitIncrementProperty().bindBidirectional(scrollBarB.unitIncrementProperty());
        scrollBarA.blockIncrementProperty().bindBidirectional(scrollBarB.blockIncrementProperty());
    }

    @Override
    protected void layoutChildren()
    {
        super.layoutChildren();

        Insets insets = getInsets();
        double w = getWidth();
        double h = getHeight();
        final double prefWidth = vBar.prefWidth(-1);
        vBar.resizeRelocate(w - prefWidth - insets.getRight(), insets.getTop(), prefWidth, h - insets.getTop() - insets.getBottom());

        final double prefHeight = hBar.prefHeight(-1);
        hBar.resizeRelocate(insets.getLeft(), h - prefHeight - insets.getBottom(), w - insets.getLeft() - insets.getRight(), prefHeight);
    }
}
