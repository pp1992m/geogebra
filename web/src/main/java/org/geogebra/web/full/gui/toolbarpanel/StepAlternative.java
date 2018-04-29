package org.geogebra.web.full.gui.toolbarpanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.web.html5.gui.util.StandardButton;

import java.util.List;

public class StepAlternative extends VerticalPanel {

    private WebStepGuiBuilder builder;

    private List<SolutionStep> substeps;

    private boolean needsRender = true;

    public StepAlternative(WebStepGuiBuilder builder, SolutionStep step) {
        this.builder = builder;
        substeps = step.getSubsteps();

        StandardButton showDetails = builder.detailsButton(true, this);

        FlowPanel row = builder.createRow(substeps.get(1), false);
        row.add(showDetails);
        add(row);

        add(builder.createRow(substeps.get(substeps.size() - 1), false));
        addStyleName("stepPanel");
    }

    public void swapStates() {
        for (int i = 0; i < getWidgetCount(); i++) {
            boolean visible = !getWidget(i).isVisible();
            getWidget(i).setVisible(visible);
            getWidget(i).getElement().getParentElement()
                    .getParentElement().getStyle()
                    .setDisplay(visible ? Style.Display.BLOCK
                            : Style.Display.NONE);
        }

        if (needsRender) {
            StandardButton hideDetails = builder.detailsButton(false, this);

            FlowPanel row = builder.createRow(substeps.get(0), true);
            row.add(hideDetails);
            add(row);

            for (int i = 1; i < substeps.size(); i++) {
                add(builder.createRow(substeps.get(i), true));
            }

            needsRender = false;
        }
    }
}
