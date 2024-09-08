package org.moqui.idea.plugin.action.flowManagement.widget;

import javax.swing.*;
import java.awt.*;

public class FlowStage extends JScrollPane {
    public FlowStage(Component view) {
        super(view);
    }

    public FlowStage(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
    }

    public FlowStage(int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
    }

    public FlowStage() {
        super();
    }

}
