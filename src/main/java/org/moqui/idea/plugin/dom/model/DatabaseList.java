package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DatabaseList extends DomElement {
    
    public static final String TAG_NAME = "database-list";


    @NotNull
    @SubTagList(DictionaryType.TAG_NAME)
    List<DictionaryType> getDictionaryTypeList();

    @NotNull
    @SubTagList(Database.TAG_NAME)
    List<Database> getDatabaseList();



}
