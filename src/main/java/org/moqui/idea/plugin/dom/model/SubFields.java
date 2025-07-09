package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface SubFields extends DomElement {
//    String TAG_NAME = "SubFields";

    @NotNull
    @SubTag(AutoWidgetService.TAG_NAME)
    AutoFieldsService getAutoFieldsService();

    @NotNull
    @SubTag(AutoWidgetEntity.TAG_NAME)
    AutoFieldsEntity getAutoFieldsEntity();

    @NotNull
    @SubTag(WidgetTemplateInclude.TAG_NAME)
    WidgetTemplateInclude getWidgetTemplateInclude();

    @NotNull
    @SubTag(Check.TAG_NAME)
    Check getCheck();

    @NotNull
    @SubTag(DateFind.TAG_NAME)
    DateFind getDateFind();

    @NotNull
    @SubTag(DatePeriod.TAG_NAME)
    DatePeriod getDatePeriod();

    @NotNull
    @SubTag(DateTime.TAG_NAME)
    DateTime getDateTime();

    @NotNull
    @SubTag(Display.TAG_NAME)
    Display getDisplay();

    @NotNull
    @SubTag(DisplayEntity.TAG_NAME)
    DisplayEntity getDisplayEntity();

    @NotNull
    @SubTag(DropDown.TAG_NAME)
    DropDown getDropDown();


    @NotNull
    @SubTag(File.TAG_NAME)
    File getFile();

    @NotNull
    @SubTag(Hidden.TAG_NAME)
    Hidden getHidden();

    @NotNull
    @SubTag(Ignored.TAG_NAME)
    Ignored getIgnored();

    @NotNull
    @SubTag(Password.TAG_NAME)
    Password getPassword();

    @NotNull
    @SubTag(Radio.TAG_NAME)
    Radio getRadio();
    @NotNull
    @SubTag(RangeFind.TAG_NAME)
    RangeFind getRangeFind();
    @NotNull
    @SubTag(Reset.TAG_NAME)
    Reset getReset();

    @NotNull
    @SubTag(Submit.TAG_NAME)
    Submit getSubmit();

    @NotNull
    @SubTag(TextLine.TAG_NAME)
    TextLine getTextLine();

    @NotNull
    @SubTag(TextArea.TAG_NAME)
    TextArea getTextArea();

    @NotNull
    @SubTag(TextFind.TAG_NAME)
    TextFind getTextFind();


}
