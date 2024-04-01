package org.moqui.idea.plugin.annotator;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomElementsAnnotator;

public class ServicesDomAnnotator implements DomElementsAnnotator {

    @Override
    public void annotate(DomElement element, DomElementAnnotationHolder holder) {

            System.out.println("Annotation:"+ element);
    }
}
