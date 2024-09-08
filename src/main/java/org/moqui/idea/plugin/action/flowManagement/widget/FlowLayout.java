package org.moqui.idea.plugin.action.flowManagement.widget;

import java.awt.*;

/**
 * 布局算法
 */
public interface FlowLayout {
    void calcLayout();
    int getWidth();
    int getHeight();
    Point getInFlowPoint();
    Point getOutFlowPoint();

    FlowNodeModel getLastFlowNodeModel();
}
