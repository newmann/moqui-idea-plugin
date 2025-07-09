package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RepositoryList extends DomElement {

    String TAG_NAME = "repository-list";

    @NotNull
    @SubTagList(Repository.TAG_NAME)
    List<Repository> getRepositorieList();

}
