package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Webapp extends DomElement {
    public static final String TAG_NAME = "webapp";

    @NotNull
    GenericAttributeValue<String> getName();
    @NotNull
    GenericAttributeValue<String> getHttpsEnabled();
    @NotNull
    GenericAttributeValue<String> getHttpsPort();
    @NotNull
    GenericAttributeValue<String> getHttpsHost();
    @NotNull
    GenericAttributeValue<String> getHttpPort();
    @NotNull
    GenericAttributeValue<String> getHttpHost();
    @NotNull
    GenericAttributeValue<String> getHandleCors();
    @NotNull
    GenericAttributeValue<String> getAllowOrigins();
    @NotNull
    GenericAttributeValue<String> getRequireSessionToken();
    @NotNull
    GenericAttributeValue<String> getWebsocketTimeout();

    @NotNull
    GenericAttributeValue<String> getUploadExecutableAllow();
    @NotNull
    GenericAttributeValue<String> getClientIpHeader();

    @NotNull
    @SubTagList(RootScreen.TAG_NAME)
    List<RootScreen> getRootScreenList();

    @NotNull
    @SubTagList(ErrorScreen.TAG_NAME)
    List<ErrorScreen> getErrorScreenList();

    @NotNull
    @SubTag(FirstHitInVisit.TAG_NAME)
    FirstHitInVisit getFirstHitInVisit();

    @NotNull
    @SubTag(BeforeRequest.TAG_NAME)
    BeforeRequest getBeforeRequest();
    @NotNull
    @SubTag(AfterRequest.TAG_NAME)
    AfterRequest getAfterRequest();
    @NotNull
    @SubTag(AfterLogin.TAG_NAME)
    AfterLogin getAfterLogin();
    @NotNull
    @SubTag(BeforeLogout.TAG_NAME)
    BeforeLogout getBeforeLogout();
    @NotNull
    @SubTag(AfterStartup.TAG_NAME)
    AfterStartup getAfterStartup();
    @NotNull
    @SubTag(BeforeShutdown.TAG_NAME)
    BeforeShutdown getBeforeShutdown();

    @NotNull
    @SubTagList(ContextParam.TAG_NAME)
    List<ContextParam> getContextParamList();
    @NotNull
    @SubTagList(Filter.TAG_NAME)
    List<Filter> getFilterList();
    @NotNull
    @SubTagList(Listener.TAG_NAME)
    List<Listener> getListenerList();
    @NotNull
    @SubTagList(Servlet.TAG_NAME)
    List<Servlet> getServletList();

    @NotNull
    @SubTag(SessionConfig.TAG_NAME)
    SessionConfig getSessionConfig();


    @NotNull
    @SubTagList(Endpoint.TAG_NAME)
    List<Endpoint> getEndpointList();

    @NotNull
    @SubTagList(ResponseHeader.TAG_NAME)
    List<ResponseHeader> getResponseHeaderList();
}
