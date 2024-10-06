package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.ServiceCallReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.ServicePresentationProvider;

import java.util.List;
@Presentation(icon="MoquiIcons.ServiceTag", provider = ServicePresentationProvider.class)
public interface Service extends AbstractLocation {
    public static final String TAG_NAME = "service";
    public static final String ATTR_VERB = "verb";
    public static final String ATTR_NOUN = "noun";

    public static final String ATTR_DISPLAY_NAME = "displayName";

    public static final String ATTR_TYPE = "type";
//    public static final String ATTR_LOCATION = "location";
    public static final String ATTR_METHOD = "method";
    public static final String ATTR_AUTHENTICATE = "authenticate";
    public static final String ATTR_AUTHZ_ACTION = "authz-action";
    public static final String ATTR_ALLOW_REMOTE = "allow-remote";
    public static final String ATTR_VALIDATE = "validate";
    public static final String ATTR_NO_REMEMBER_PARAMENTERS = "no-remember-parameters";
    public static final String ATTR_TRANSACTION = "transaction";
    public static final String ATTR_TRANSACTION_TIMEOUT = "authenticate-timeout";
    public static final String ATTR_NO_TX_CACHE = "no-tx-cache";
    public static final String ATTR_SEMAPHORE = "semaphore";
    public static final String ATTR_SEMAPHORE_NAME = "semaphore-name";
    public static final String ATTR_SEMAPHORE_TIMEOUT = "semaphore-timeout";
    public static final String ATTR_SEMAPHORE_SLEEP = "semaphore-sleep";
    public static final String ATTR_SEMAPHORE_IGNORE = "semaphore-ignore";
    public static final String ATTR_SEMAPHORE_PARAMETER = "semaphore-parameter";

    //for rest api
    public static final String ATTR_NAME = "name";
    //for rest api end
    @NotNull
    @Attribute(ATTR_NAME)
    @Referencing(ServiceCallReferenceConverter.class)
    GenericAttributeValue<String> getName();

    @NotNull
    @NameValue(unique = false)
    @Attribute(ATTR_VERB)
    GenericAttributeValue<String> getVerb();

    @NotNull
    @NameValue(unique = false)
    @Attribute(ATTR_NOUN)
    GenericAttributeValue<String> getNoun();
    @NotNull
    @Attribute(ATTR_DISPLAY_NAME)
    GenericAttributeValue<String> getDisplayName();
    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();
//    @NotNull
//    @Attribute(ATTR_LOCATION)
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();
    @NotNull
    @Attribute(ATTR_METHOD)
    GenericAttributeValue<String> getMethod();
    @NotNull
    @Attribute(ATTR_AUTHENTICATE)
    GenericAttributeValue<String> getAuthenticate();
    @NotNull
    @Attribute(ATTR_AUTHZ_ACTION)
    GenericAttributeValue<String> getAuthzAction();
    @NotNull
    @Attribute(ATTR_ALLOW_REMOTE)
    GenericAttributeValue<String> getAllowRemote();
    @NotNull
    @Attribute(ATTR_VALIDATE)
    GenericAttributeValue<String> getValidate();
    @NotNull
    @Attribute(ATTR_NO_REMEMBER_PARAMENTERS)
    GenericAttributeValue<String> getNoRememberParameters();
    @NotNull
    @Attribute(ATTR_TRANSACTION)
    GenericAttributeValue<String> getTransaction();
    @NotNull
    @Attribute(ATTR_TRANSACTION_TIMEOUT)
    GenericAttributeValue<String> getTransactionTimeout();
    @NotNull
    @Attribute(ATTR_NO_TX_CACHE)
    GenericAttributeValue<String> getNoTxCache();
    @NotNull
    @Attribute(ATTR_SEMAPHORE)
    GenericAttributeValue<String> getSemaphore();
    @NotNull
    @Attribute(ATTR_SEMAPHORE_NAME)
    GenericAttributeValue<String> getSemaphoreName();
    @NotNull
    @Attribute(ATTR_SEMAPHORE_TIMEOUT)
    GenericAttributeValue<String> getSemaphoreTimeout();
    @NotNull
    @Attribute(ATTR_SEMAPHORE_SLEEP)
    GenericAttributeValue<String> getSemaphoreSleep();
    @NotNull
    @Attribute(ATTR_SEMAPHORE_IGNORE)
    GenericAttributeValue<String> getSemaphoreIgnore();
    @NotNull
    @Attribute(ATTR_SEMAPHORE_PARAMETER)
    GenericAttributeValue<String> getSemaphoreParameter();



    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();

    @NotNull
    @SubTag(Actions.TAG_NAME)
    Actions getActions();

    @NotNull
    @SubTag(InParameters.TAG_NAME)
    InParameters getInParameters();

    @NotNull
    @SubTag(OutParameters.TAG_NAME)
    OutParameters getOutParameters();

    @NotNull
    @SubTagList(Implements.TAG_NAME)
    List<Implements> getImplementsList();


}
