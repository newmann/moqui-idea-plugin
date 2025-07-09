package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.FormListPresentationProvider;

import java.util.List;
@Presentation(provider = FormListPresentationProvider.class)
public interface FormList extends AbstractForm {
    
    String TAG_NAME = "form-list";

//    String ATTR_NAME = "name";
//    String ATTR_EXTENDS = "extends";
//    String ATTR_TRANSITION = "transition";

    
    String ATTR_TRANSITION_FIRST_ROW = "transition-first-row";
    
    String ATTR_TRANSITION_SECOND_ROW = "transition-second-row";
    
    String ATTR_TRANSITION_LAST_ROW = "transition-last-row";
    
    String ATTR_MAP_FIRST_ROW = "map-first-row";
    
    String ATTR_MAP_SECOND_ROW = "map-second-row";
    
    String ATTR_MAP_LAST_ROW = "map-last-row";
    
    String ATTR_MULTI = "multi";
    String ATTR_LIST = "list";
    
    String ATTR_LIST_ENTRY = "list-entry";
    String ATTR_STYLE = "style";
    
    String ATTR_PAGINATE = "paginate";
    
    String ATTR_PAGINATE_ALWAYS_SHOW = "paginate-always-show";
    
    String ATTR_FOCUS_FIELD = "focus-field";

    
    String ATTR_SKIP_START = "skip-start";
    
    String ATTR_SKIP_END = "skip-end";
    
    String ATTR_SKIP_FORM = "skip-form";
    
    String ATTR_SKIP_HEADER = "skip-header";
    
    String ATTR_SKIP_DIALOG = "skip-dialog";
    
    String ATTR_SELECT_COLUMNS = "select-columns";
    
    String ATTR_SAVED_FINDS = "saved-finds";
    
    String ATTR_SHOW_CSV_BUTTON = "show-csv-button";
    
    String ATTR_SHOW_XLSX_BUTTON = "show-xlsx-button";
    
    String ATTR_SHOW_TEXT_BUTTON = "show-text-button";
    
    String ATTR_SHOW_PDF_BUTTON = "show-pdf-button";
    
    String ATTR_SHOW_ALL_BUTTON = "show-all-button";
    
    String ATTR_SHOW_PAGE_SIZE = "show-page-size";


//    @NotNull
//    @Attribute(ATTR_NAME)
//    GenericAttributeValue<String> getName();
//    @NotNull
//    @Attribute(ATTR_EXTENDS)
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getExtends();
//    @NotNull
//    @Attribute(ATTR_TRANSITION)
//    @Convert(TransitionConverter.class)
//    GenericAttributeValue<String> getTransition();

    @NotNull
    @Attribute(ATTR_TRANSITION_FIRST_ROW)
    GenericAttributeValue<String> getTransitionFirstRow();
    @NotNull
    @Attribute(ATTR_TRANSITION_SECOND_ROW)
    GenericAttributeValue<String> getTransitionSecondRow();
    @NotNull
    @Attribute(ATTR_TRANSITION_LAST_ROW)
    GenericAttributeValue<String> getTransitionLastRow();
    @NotNull
    @Attribute(ATTR_MAP_FIRST_ROW)
    GenericAttributeValue<String> getMapFirstRow();
    @NotNull
    @Attribute(ATTR_MAP_SECOND_ROW)
    GenericAttributeValue<String> getMapSecondRow();
    @NotNull
    @Attribute(ATTR_MAP_LAST_ROW)
    GenericAttributeValue<String> getMapLastRow();
    @NotNull
    @Attribute(ATTR_MULTI)
    GenericAttributeValue<String> getMulti();
    @NotNull
    @Attribute(ATTR_LIST)
    GenericAttributeValue<String> getList();
    @NotNull
    @Attribute(ATTR_LIST_ENTRY)
    GenericAttributeValue<String> getListEntry();
    @NotNull
    @Attribute(ATTR_STYLE)
    GenericAttributeValue<String> getStyle();
    @NotNull
    @Attribute(ATTR_PAGINATE)
    GenericAttributeValue<String> getPaginate();
    @NotNull
    @Attribute(ATTR_PAGINATE_ALWAYS_SHOW)
    GenericAttributeValue<String> getPaginateAlwaysShow();

    @NotNull
    @Attribute(ATTR_FOCUS_FIELD)
    GenericAttributeValue<String> getFocusField();
    @NotNull
    @Attribute(ATTR_SKIP_START)
    GenericAttributeValue<String> getSkipStart();
    @NotNull
    @Attribute(ATTR_SKIP_END)
    GenericAttributeValue<String> getSkipEnd();
    @NotNull
    @Attribute(ATTR_SKIP_FORM)
    GenericAttributeValue<String> getSkipForm();
    @NotNull
    @Attribute(ATTR_SKIP_HEADER)
    GenericAttributeValue<String> getSkipHeader();
    @NotNull
    @Attribute(ATTR_SKIP_DIALOG)
    GenericAttributeValue<String> getSkipDialog();
    @NotNull
    @Attribute(ATTR_SELECT_COLUMNS)
    GenericAttributeValue<String> getSelectColumns();
    @NotNull
    @Attribute(ATTR_SAVED_FINDS)
    GenericAttributeValue<String> getSavedFinds();
    @NotNull
    @Attribute(ATTR_SHOW_CSV_BUTTON)
    GenericAttributeValue<String> getShowCsvButton();
    @NotNull
    @Attribute(ATTR_SHOW_XLSX_BUTTON)
    GenericAttributeValue<String> getShowXlsxButton();
    @NotNull
    @Attribute(ATTR_SHOW_TEXT_BUTTON)
    GenericAttributeValue<String> getShowTextButton();
    @NotNull
    @Attribute(ATTR_SHOW_PDF_BUTTON)
    GenericAttributeValue<String> getShowPdfButton();
    @NotNull
    @Attribute(ATTR_SHOW_ALL_BUTTON)
    GenericAttributeValue<String> getShowAllButton();
    @NotNull
    @Attribute(ATTR_SHOW_PAGE_SIZE)
    GenericAttributeValue<String> getShowPageSize();

    @NotNull
    @SubTag(EntityFind.TAG_NAME)
    EntityFind getEntityFind();
    @NotNull
    @SubTag(RowActions.TAG_NAME)
    RowActions getRowActions();
    //看定义，只能有一个RowActions
//    @SubTagList(RowActions.TAG_NAME)
//    List<RowActions> getRowActionsList();
    @NotNull
    @SubTag(RowSelection.TAG_NAME)
    RowSelection getRowSelection();

    @NotNull
    @SubTag(HideParameters.TAG_NAME)
    HideParameters getHideParameters();

    @NotNull
    @SubTagList(AutoFieldsService.TAG_NAME)
    List<AutoFieldsService> getAutoFieldsServiceList();
    @NotNull
    @SubTagList(AutoFieldsEntity.TAG_NAME)
    List<AutoFieldsEntity> getAutoFieldsEntityList();
//    @NotNull
//    @SubTagList(Field.TAG_NAME)
//    List<Field> getFieldList();

    @NotNull
    @SubTagList(FormListColumn.TAG_NAME)
    List<FormListColumn> getFormListColumnList();

    @NotNull
    @SubTagList(Columns.TAG_NAME)
    List<Columns> getColumnsList();






}
