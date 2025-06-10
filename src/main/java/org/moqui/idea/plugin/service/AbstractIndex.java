package org.moqui.idea.plugin.service;

import com.intellij.icons.AllIcons;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractIndex {

    public final Object MUTEX = new Object();//用来做加锁用

    protected long lastRefreshStamp; //最近一次刷新时所在文件的时间戳，根据这个判断是否要重新刷新

    public long getLastRefreshStamp() {
        return lastRefreshStamp;
    }

    public void setLastRefreshStamp(long lastRefreshStamp) {
        this.lastRefreshStamp = lastRefreshStamp;
    }


}
