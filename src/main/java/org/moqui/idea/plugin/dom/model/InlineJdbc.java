package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface InlineJdbc extends DomElement {

    public static final String TAG_NAME = "inline-jdbc";


    @NotNull GenericAttributeValue<String> getJdbcUri();

    @NotNull GenericAttributeValue<String> getJdbcUsername();
    @NotNull GenericAttributeValue<String> getJdbcPassword();
    @NotNull GenericAttributeValue<String> getJdbcDriver();
    @NotNull GenericAttributeValue<String> getXaDsClass();
    @NotNull GenericAttributeValue<String> getIsolationLevel();
    @NotNull GenericAttributeValue<String> getPoolMaxsize();
    @NotNull GenericAttributeValue<String> getPoolMinsize();
    @NotNull GenericAttributeValue<String> getPoolTimeIdle();
    @NotNull GenericAttributeValue<String> getPoolTimeReap();
    @NotNull GenericAttributeValue<String> getPoolTimeMaint();
    @NotNull GenericAttributeValue<String> getPoolTimeWait();
    @NotNull GenericAttributeValue<String> getPoolTestQuery();



    @NotNull
    @SubTag(XaProperties.TAG_NAME)
    XaProperties getXaProperties();


}
