package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Action下面包含很多处理过程，这些过程同样可以出现在另外的Tag下，比如If，iterate等
 * 这里定义可以在这些地方出现的共性内容
 */
public interface CommonActions extends DomElement {


    @NotNull
    @SubTagList(ServiceCall.TAG_NAME)
    List<ServiceCall> getServiceCallList();

    @NotNull
    @SubTagList(Set.TAG_NAME)
    List<Set> getSetList();

    @NotNull
    @SubTagList(If.TAG_NAME)
    List<If> getIfList();

    @NotNull
    @SubTagList(EntityFind.TAG_NAME)
    List<EntityFind> getEntityFindList();

    @NotNull
    @SubTagList(EntityFindOne.TAG_NAME)
    List<EntityFindOne> getEntityFindOneList();

    @NotNull
    @SubTagList(Script.TAG_NAME)
    List<Script> getScriptList();
    @NotNull
    @SubTagList(Continue.TAG_NAME)
    List<Continue> getContinueList();
    @NotNull
    @SubTagList(Break.TAG_NAME)
    List<Break> getBreakList();

    @NotNull
    @SubTagList(Iterate.TAG_NAME)
    List<Iterate> getIterateList();

}
