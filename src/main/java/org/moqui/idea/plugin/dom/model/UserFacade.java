package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface UserFacade extends DomElement {

    String TAG_NAME = "user-facade";


    @NotNull
    @SubTag(Password.TAG_NAME)
    Password getPassword();
    @NotNull
    @SubTag(LoginKey.TAG_NAME)
    LoginKey getLoginKey();
    @NotNull
    @SubTag(Login.TAG_NAME)
    Login getLogin();

}
