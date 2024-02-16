package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SubFieldsList extends DomElement {
//    public static final String TAG_NAME = "SubFields";

    @NotNull
    @SubTagList(AutoWidgetService.TAG_NAME)
    List<AutoFieldsService> getAutoFieldsServiceList();

    @NotNull
    @SubTagList(AutoWidgetEntity.TAG_NAME)
    List<AutoFieldsEntity> getAutoFieldsEntityList();

    @NotNull
    @SubTagList(WidgetTemplateInclude.TAG_NAME)
    List<WidgetTemplateInclude> getWidgetTemplateIncludeList();

    @NotNull
    @SubTagList(Check.TAG_NAME)
    List<Check> getCheckList();

    @NotNull
    @SubTagList(DateFind.TAG_NAME)
    List<DateFind> getDateFindList();

    @NotNull
    @SubTagList(DatePeriod.TAG_NAME)
    List<DatePeriod> getDatePeriodList();

    @NotNull
    @SubTagList(DateTime.TAG_NAME)
    List<DateTime> getDateTimeList();

    @NotNull
    @SubTagList(Display.TAG_NAME)
    List<Display> getDisplayList();

    @NotNull
    @SubTagList(DisplayEntity.TAG_NAME)
    List<DisplayEntity> getDisplayEntityList();

    @NotNull
    @SubTagList(DropDown.TAG_NAME)
    List<DropDown> getDropDownList();


    @NotNull
    @SubTagList(File.TAG_NAME)
    List<File> getFileList();

    @NotNull
    @SubTagList(Hidden.TAG_NAME)
    List<Hidden> getHiddenList();

    @NotNull
    @SubTagList(Ignored.TAG_NAME)
    List<Ignored> getIgnoredList();

    @NotNull
    @SubTagList(Password.TAG_NAME)
    List<Password> getPasswordList();

    @NotNull
    @SubTagList(Radio.TAG_NAME)
    List<Radio> getRadioList();
    @NotNull
    @SubTagList(RangeFind.TAG_NAME)
    List<RangeFind> getRangeFindList();
    @NotNull
    @SubTagList(Reset.TAG_NAME)
    List<Reset> getResetList();

    @NotNull
    @SubTagList(Submit.TAG_NAME)
    List<Submit> getSubmitList();

    @NotNull
    @SubTagList(TextLine.TAG_NAME)
    List<TextLine> getTextLineList();

    @NotNull
    @SubTagList(TextArea.TAG_NAME)
    List<TextArea> getTextAreaList();

    @NotNull
    @SubTagList(TextFind.TAG_NAME)
    List<TextFind> getTextFindList();



}
