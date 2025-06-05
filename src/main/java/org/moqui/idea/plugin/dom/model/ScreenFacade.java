package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ScreenFacade extends DomElement {
    
    public static final String TAG_NAME = "screen-facade";

    @NotNull GenericAttributeValue<String> getBoundaryComments();
    @NotNull GenericAttributeValue<String> getDefaultPaginateRows();
    @NotNull GenericAttributeValue<String> getDefaultAutocompleteRows();

    @NotNull
    @SubTagList(ScreenTextOutput.TAG_NAME)
    List<ScreenTextOutput> getScreenTextOutputList();

    @NotNull
    @SubTagList(ScreenOutput.TAG_NAME)
    List<ScreenOutput> getScreenOutputList();
    @NotNull
    @SubTagList(Screen.TAG_NAME)
    List<Screen> getScreenList();


}
