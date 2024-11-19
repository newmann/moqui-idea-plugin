package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;

import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import kotlinx.html.I;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.AbstractIndexEntity;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.*;

import static org.moqui.idea.plugin.util.EntityUtils.*;
import static org.moqui.idea.plugin.util.MyDomUtils.*;

public class EntityFieldNameConverter implements CustomReferenceConverter<String> {
    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue<String> genericDomValue, PsiElement psiElement, ConvertContext convertContext) {
        return new PsiReference[0];
    }
}
