package org.geogebra.web.full.gui.toolbarpanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.view.algebra.OpenButton;
import org.geogebra.web.full.gui.view.algebra.TreeImages;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;

import java.util.ArrayList;

public class StepsTab extends ToolbarPanel.ToolbarTab {

    final AppW app;
    final ToolbarPanel toolbarPanel;
    final GeoNumeric gn;

    private ScrollPanel sp;
    private Tree tree;

    private StepGuiBuilder stepGuiBuilder = new StepGuiBuilder() {
        private TreeItem item;
        private TreeItem child = null;
        private boolean detailed = false;

        private Button showDetails;
        private Button hideDetails;

        private boolean addShowDetails;
        private boolean addHideDetails;

        private ArrayList<Widget> summary = new ArrayList<>();

        @Override
        public void addLatexRow(String equations) {
            Canvas c = DrawEquationW.paintOnCanvas(gn, equations, null,
                    app.getFontSizeWeb());

            Widget row;

            if (addShowDetails) {
                FlowPanel flowPanel = new FlowPanel();
                flowPanel.add(c);
                flowPanel.add(showDetails);

                addShowDetails = false;

                row = flowPanel;
            } else if (addHideDetails) {
                FlowPanel flowPanel = new FlowPanel();
                flowPanel.add(c);
                flowPanel.add(hideDetails);

                addHideDetails = false;

                row = flowPanel;
            } else {
                row = c;
            }

            if (detailed) {
                row.setVisible(false);
            }

            summary.add(row);
            addWidget(row);
        }

        private void addWidget(Widget c) {
            child = new TreeItem(c);
            if (item != null) {
                if (item.getChildCount() == 0) {
                    Widget oldWidget = item.getWidget();
                    FlowPanel header = new FlowPanel();
                    header.add(oldWidget);

                    OpenButton openButton = new OpenButton(
                            GuiResources.INSTANCE.algebra_tree_open().getSafeUri(),
                            GuiResources.INSTANCE.algebra_tree_closed().getSafeUri(),
                            item, "stepOpenButton");
                    openButton.setChecked(false);
                    header.add(openButton);

                    item.setWidget(header);
                    item.addStyleName("stepTreeItem");
                }

                item.addItem(child);
            } else {
                tree.addItem(child);
            }
            if (detailed) {
                child.getElement().getStyle().setDisplay(Style.Display.NONE);
            }
        }

        @Override
        public void addPlainRow(String equations) {
            addWidget(new Label(equations));
        }

        @Override
        public void startGroup() {
            if (child == null || tree.getItemCount() == 1) {
                return;
            }
            item = child;
        }

        @Override
        public void endGroup() {
            if (item != null) {
                item = item.getParentItem();
            }
        }

        @Override
        public void linebreak() {
            addWidget(new FlowPanel());
        }

        @Override
        public void startDefault() {
            showDetails = new Button("<img src=\""
                    + GuiResources.INSTANCE.algebra_tree_closed().getURL()
                    + "\"></img>");
            hideDetails = new Button("<img src=\""
                    + GuiResources.INSTANCE.algebra_tree_open().getURL()
                    + "\"></img>");

            showDetails.getElement().getStyle().setPadding(1, Style.Unit.PX);
            showDetails.getElement().getStyle().setFontSize(10, Style.Unit.PX);
            showDetails.setStyleName("stepOpenButton");

            hideDetails.getElement().getStyle().setPadding(1, Style.Unit.PX);
            hideDetails.getElement().getStyle().setFontSize(10, Style.Unit.PX);
            hideDetails.setStyleName("stepOpenButton");

            addShowDetails = true;
            summary = new ArrayList<>();
            detailed = false;
        }

        @Override
        public void switchToDetailed() {
            addHideDetails = true;

            detailed = true;
        }

        @Override
        public void endDetailed() {
            detailed = false;

            final ArrayList<Widget> swap = new ArrayList<>(summary);
            summary = new ArrayList<>();

            ClickHandler clickHandler = new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    event.getSource();
                    for (Widget line : swap) {
                        boolean visible = !line.isVisible();
                        line.setVisible(visible);
                        line.getElement().getParentElement()
                                .getParentElement().getStyle()
                                .setDisplay(visible ? Style.Display.BLOCK
                                        : Style.Display.NONE);
                    }
                }
            };

            showDetails.addClickHandler(clickHandler);
            hideDetails.addClickHandler(clickHandler);
        }
    };

    public StepsTab(ToolbarPanel toolbarPanel) {
        this.toolbarPanel = toolbarPanel;
        app = toolbarPanel.app;
        gn = new GeoNumeric(app.getKernel().getConstruction());

        sp = new ScrollPanel();
        add(sp);
    }

    public StepGuiBuilder getStepGuiBuilder() {
        tree = new Tree(new TreeImages());
        tree.addStyleName("stepTree");
        tree.setWidth(toolbarPanel.getTabWidth() + "px");

        sp.clear();
        sp.add(tree);

        return stepGuiBuilder;
    }

    @Override
    public void onResize() {
        super.onResize();
        sp.getWidget().setWidth(toolbarPanel.getTabWidth() + "px");
    }
}