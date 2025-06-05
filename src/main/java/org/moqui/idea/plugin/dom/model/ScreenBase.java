package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

public interface ScreenBase extends DomElement {

    
    public static final String ATTR_STANDALONE = "standalone";
    
    public static final String ATTR_DEFAULT_MENU_TITLE = "default-menu-title";
    
    public static final String ATTR_DEFAULT_MENU_INDEX = "default-menu-index";
    
    public static final String ATTR_DEFAULT_MENU_INCLUDE = "default-menu-include";
    
    public static final String ATTR_MENU_IMAGE = "menu-image";
    
    public static final String ATTR_MENU_IMAGE_TYPE = "menu-image-type";

    
    public static final String ATTR_REQUIRE_AUTHENTICATION = "require-authentication";
    
    public static final String ATTR_BEGIN_TRANSACTION = "begin-transaction";
    
    public static final String ATTR_TRANSACTION_TIMEOUT = "transaction-timeout";
    
    public static final String ATTR_INCLUDE_CHILD_CONTENT = "include-child-content";
    
    public static final String ATTR_SCREEN_THEME_TYPE_ENUM_ID = "screen-theme-type-enum-id";
    
    public static final String ATTR_HISTORY = "history";
    
    public static final String ATTR_LOGIN_PATH = "login-path";
    
    public static final String ATTR_ALLOW_EXTRA_PATH = "allow-extra-path";
    
    public static final String ATTR_RENDER_MODES = "render-modes";
    
    public static final String ATTR_SERVER_STATIC = "server-static";
    @NotNull
    @Attribute(ATTR_STANDALONE)
    GenericAttributeValue<String> getStandalone();
    @NotNull
    @Attribute(ATTR_DEFAULT_MENU_TITLE)
    GenericAttributeValue<String> getDefaultMenuTitle();
    @NotNull
    @Attribute(ATTR_DEFAULT_MENU_INDEX)
    GenericAttributeValue<String> getDefaultMenuIndex();
    @NotNull
    @Attribute(ATTR_DEFAULT_MENU_INCLUDE)
    GenericAttributeValue<String> getDefaultMenuInclude();
    @NotNull
    @Attribute(ATTR_MENU_IMAGE)
    @Convert(LocationConverter.class)
    GenericAttributeValue<String> getMenuImage();

    @NotNull
    @Attribute(ATTR_MENU_IMAGE_TYPE)
    GenericAttributeValue<String> getMenuImageType();
    @NotNull
    @Attribute(ATTR_REQUIRE_AUTHENTICATION)
    GenericAttributeValue<String> getRequireAuthentication();
    @NotNull
    @Attribute(ATTR_BEGIN_TRANSACTION)
    GenericAttributeValue<String> getBeginTransaction();
    @NotNull
    @Attribute(ATTR_TRANSACTION_TIMEOUT)
    GenericAttributeValue<String> getTransactionTimeout();
    @NotNull
    @Attribute(ATTR_INCLUDE_CHILD_CONTENT)
    GenericAttributeValue<String> getIncludeChildContent();
    @NotNull
    @Attribute(ATTR_SCREEN_THEME_TYPE_ENUM_ID)
    GenericAttributeValue<String> getScreenThemeTypeEnumId();
    @NotNull
    @Attribute(ATTR_HISTORY)
    GenericAttributeValue<String> getHistory();
    @NotNull
    @Attribute(ATTR_LOGIN_PATH)
    GenericAttributeValue<String> getLoginPath();
    @NotNull
    @Attribute(ATTR_ALLOW_EXTRA_PATH)
    GenericAttributeValue<String> getAllowExtraPath();
    @NotNull
    @Attribute(ATTR_RENDER_MODES)
    GenericAttributeValue<String> getRenderModes();
    @NotNull
    @Attribute(ATTR_SERVER_STATIC)
    GenericAttributeValue<String> getServerStatic();
}

