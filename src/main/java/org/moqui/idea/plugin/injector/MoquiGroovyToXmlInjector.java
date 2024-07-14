package org.moqui.idea.plugin.injector;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagValue;
import com.intellij.psi.xml.XmlText;
import icons.JetgroovyIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.GroovyLanguage;
import org.moqui.idea.plugin.dom.model.Actions;
import org.moqui.idea.plugin.dom.model.Eecas;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.dom.model.Script;
import org.moqui.idea.plugin.util.*;


import java.util.Collections;
import java.util.List;

/**
 * 由于将所有actions内容都考虑进来需要定制基于xml的PsiLanguageInjectionHost，比较复杂，先只考虑script部分的脚本。
 * 其他单行的groovy脚本通过moqui-xml-injections.xml来实现。
 *
 */
public class MoquiGroovyToXmlInjector implements MultiHostInjector {
    private static final Logger logger = Logger.getInstance(MoquiGroovyToXmlInjector.class);

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if(shouldInject(context)) {
//            logger.warn("线程名称：" + Thread.currentThread().getName()+"; context:" + context.getText());
            doActionsInject(registrar,context);

        }
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
//        return List.of(XmlText.class);
        return Collections.singletonList(XmlTag.class);
    }

    private boolean shouldInject(@NotNull PsiElement element) {
        PsiFile psiFile = element.getContainingFile();

        if(ServiceUtils.isServicesFile(psiFile)
                || ScreenUtils.isScreenFile(psiFile)
                || EecaUtils.isEecasFile(psiFile)
                || SecaUtils.isSecasFile(psiFile)
        ) {
            if (element instanceof XmlTag xmlTag) {
                return xmlTag.getName().equals(Script.TAG_NAME);
            }
        }
        return false;
    }
    private void doActionsInject(@NotNull MultiHostRegistrar registrar,@NotNull PsiElement context){

        Script script = MyDomUtils.getLocalDomElementByXmlTag((XmlTag) context,Script.class).orElse(null);
        if(script == null) return;
        if(script.getXmlTag() == null) return;
//        if(script.getXmlTag().getValue() == null) return;

        PsiLanguageInjectionHost host = (PsiLanguageInjectionHost) script.getXmlTag().getValue().getTextElements()[0];

//        PsiLanguageInjectionHost host = InjectedLanguageManager.getInstance(context.getProject()).getInjectionHost(context.getContainingFile().getViewProvider());

        String sbPrefix = System.lineSeparator() +
                "import static org.moqui.util.ObjectUtilities.*" +
                System.lineSeparator() +
                "import static org.moqui.util.CollectionUtilities.*" +
                System.lineSeparator() +
                "import static org.moqui.util.StringUtilities.*" +
                System.lineSeparator() +
                "import java.sql.Timestamp" +
                System.lineSeparator() +
                "import java.sql.Time" +
                System.lineSeparator() +
                "import java.time.*" +
                System.lineSeparator() +
                "import org.moqui.Moqui" +
                System.lineSeparator() +
                "import org.moqui.context.*" +
                System.lineSeparator() +
                "def ec = Moqui.getExecutionContext()" +
                System.lineSeparator() +
                "def context=[String : Object]" +
                System.lineSeparator() +
                "def result = [String : Object]"+
                System.lineSeparator()
                ;


        XmlTagValue xmlTagValue = script.getXmlTag().getValue();
        String xmlText = xmlTagValue.getText();
        int beginOffset = 0;
        int endOffset = xmlText.length();

        if(xmlText.startsWith("<![CDATA[")) {
            beginOffset = beginOffset+"<![CDATA[".length();
        }
        if(xmlText.endsWith("]]")) {
            endOffset = endOffset -2;
        }

        TextRange textRange = new TextRange(beginOffset, endOffset);
        registrar
                .startInjecting(GroovyLanguage.INSTANCE)
                .addPlace(sbPrefix,null,
                        host,
                        textRange)
                .doneInjecting();

    }
}
