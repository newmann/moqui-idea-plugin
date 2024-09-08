package org.moqui.idea.plugin.action.flowManagement.widget;


import com.intellij.psi.PsiElement;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 流程中的节点
 * 1、负责内容绘制
 * 2、管理上游和下游关联节点
 */
public class FlowNodeModel {
    public static final String PROPERTY_NAME_LAYOUT_CHANGE = "layoutChange";
    public final static int TITLE_HEIGHT = 20;//如果设置标题，标题的高度
    public final static int MARGIN_ALL = 15;//针对SceneNode，空白边框的宽度

    public static final int DEFAULT_WIDTH = 200;
    public static final int DEFAULT_HEIGHT = 50;
    private final FlowNodeType type;
    private final String name;
    private String toolTips;
    private FlowNodeModel parentNode;
    private FlowNodeModel childNode;
    int width=DEFAULT_WIDTH;
    int height = DEFAULT_HEIGHT;
    int x = 0;
    int y = 0;

    protected Point inFlowPoint ;//上游流程入口点
    protected Point outFlowPoint ;//下游流程出口点

    protected PsiElement targetPsiElement;//所对应的PsiElement，双击时可以自动跳到对应位置
    private PropertyChangeSupport propertyChangeSupport = null;

    public FlowNodeModel(FlowNodeType type, String name ,PsiElement targetPsiElement) {
        this(type, name,targetPsiElement,"" );
    }
    public FlowNodeModel(FlowNodeType type, String name,PsiElement targetPsiElement,String toolTips ){
        this.type = type;
        this.name = name;
        this.targetPsiElement = targetPsiElement;
        this.toolTips = toolTips;
        this.inFlowPoint = new Point(width/2,0);
        this.outFlowPoint =new Point(width/2,height);
    }

    public FlowNodeModel getChildNode() {
        return childNode;
    }
    public FlowNodeModel getParentNode() {
        return parentNode;
    }

    public void setChildNode(FlowNodeModel childNode) {
        this.childNode = childNode;
    }

    public void setParentNode(FlowNodeModel parentNode) {
        this.parentNode = parentNode;
    }

    public FlowNodeType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Point getTopLeftPoint() {
        return new Point(x,y);
    }

    public PsiElement getTargetPsiElement(){return targetPsiElement;}

    public void setTopLeftPoint(Point topLeftPoint) {
        this.x = topLeftPoint.x;
        this.y = topLeftPoint.y;
    }

    public Point getInFlowPoint() {
        return inFlowPoint;
    }
    public void setInFlowPoint(Point inFlowPoint) {this.inFlowPoint = inFlowPoint;}

    public Point getOutFlowPoint() {
        return outFlowPoint;
    }

    public Point getAbsoluteInFlowPoint() {
        return new Point(x+inFlowPoint.x,y+inFlowPoint.y);
    }
    public Point getAbsoluteOutFlowPoint() {
        return new Point(x+outFlowPoint.x,y+outFlowPoint.y);
    }


    public void setOutFlowPoint(Point outFlowPoint) {
        this.outFlowPoint = outFlowPoint;
    }

    public boolean isEndFlowNodeModel(){
        return type.equals(FlowNodeType.END);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getToolTips() {
        return toolTips;
    }
    public void setToolTips(String toolTips) {
        this.toolTips = toolTips;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (this.propertyChangeSupport == null) {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }

        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (this.propertyChangeSupport != null) {
            this.propertyChangeSupport.removePropertyChangeListener(listener);
        }

    }
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (this.propertyChangeSupport != null) {
            if (newValue instanceof PropertyChangeEvent pce) {
                this.propertyChangeSupport.firePropertyChange(pce);
            } else {
                this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
            }
        }

    }
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (this.propertyChangeSupport != null) {
            this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }

    }
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if (this.propertyChangeSupport != null) {
            this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }

    }
    public void firePropertyChange(PropertyChangeEvent event) {
        if (this.propertyChangeSupport != null) {
            this.propertyChangeSupport.firePropertyChange(event);
        }
    }

    public static void setRelation(FlowNodeModel parentNode, FlowNodeModel childNode){
        parentNode.setChildNode(childNode);
        childNode.setParentNode(parentNode);
    }

    /**
     * 调整x，diff可以是负数
     * @param diff，正负数
     */
    public void adjustX(int diff){
        this.x += diff;
    }

    /**
     * 调整y，
     * @param diff，正负数
     */
    public void adjustY(int diff){
        this.y += diff;
    }

    /**
     * 将顶点设置到0,0
     */
    public void resetTop(){
        x = 0;
        y = 0;
    }

    public void travelParent(Consumer<FlowNodeModel> operation){
        ofStreamParent().forEach(operation);
    }
    public void travelChild(Consumer<FlowNodeModel> operation){
        ofStreamChild().forEach(operation);
    }



    public Stream<FlowNodeModel> ofStreamParent(){
        return Stream.concat(
                Stream.of(this),
                Stream.of(parentNode).filter(Objects::nonNull).flatMap(FlowNodeModel::ofStreamParent));
    }
    public Stream<FlowNodeModel> ofStreamChild(){
        return Stream.concat(
                Stream.of(this),
                Stream.of(childNode).filter(Objects::nonNull).flatMap(FlowNodeModel::ofStreamChild));
    }

    public static class ChildNodeSupplier implements Supplier<FlowNodeModel>{
        private FlowNodeModel currentNode;
        ChildNodeSupplier(FlowNodeModel root){
            this.currentNode = root;
        }
        @Override
        public FlowNodeModel get() {
            currentNode =  currentNode == null ? null : currentNode.childNode;
            return currentNode;
        }
    }
    public static class ParentNodeSupplier implements Supplier<FlowNodeModel>{
        private FlowNodeModel currentNode;
        ParentNodeSupplier(FlowNodeModel root){
            this.currentNode = root;
        }
        @Override
        public FlowNodeModel get() {
            if(this.currentNode == null) {
                return null;
            }else {
                this.currentNode = currentNode.parentNode;
                return currentNode;
            }
        }
    }
//
//    public static FlowNodeModel ofAssignmentNodeModel(String name, String toolTips){
//        return new FlowNodeModel(FlowNodeType.ASSIGNMENT,name, toolTips);
//    }
}
