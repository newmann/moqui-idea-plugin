package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface FormListConditionalField extends FormListDefaultField {
//    public static final String TAG_NAME = "form-list-conditional-field";


    @NotNull GenericAttributeValue<String> getCondition();

}
