package org.moqui.idea.plugin.dom.xsd;

import com.intellij.javaee.ResourceRegistrar;
import com.intellij.javaee.StandardResourceProvider;

public class MoquiXsdProvider implements StandardResourceProvider {
    @Override
    public void registerResources(ResourceRegistrar registrar) {
        registrar.addStdResource("http://moqui.org/xsd/email-eca-3.xsd","/xsd/email-eca-3.xsd",getClass());
        registrar.addStdResource("http://moqui.org/xsd/entity-definition-3.xsd","/xsd/entity-definition-3.xsd",getClass());
        registrar.addStdResource("http://moqui.org/xsd/entity-eca-3.xsd","/xsd/entity-eca-3.xsd",getClass());
        registrar.addStdResource("http://moqui.org/xsd/moqui-conf-3.xsd","/xsd/moqui-conf-3.xsd",getClass());
        registrar.addStdResource("http://moqui.org/xsd/rest-api-3.xsd","/xsd/rest-api-3.xsd",getClass());
        registrar.addStdResource("http://moqui.org/xsd/service-definition-3.xsd","/xsd/service-definition-3.xsd",getClass());
        registrar.addStdResource("http://moqui.org/xsd/service-eca-3.xsd","/xsd/service-eca-3.xsd",getClass());
        registrar.addStdResource("http://moqui.org/xsd/xml-actions-3.xsd","/xsd/xml-actions-3.xsd",getClass());
        registrar.addStdResource("http://moqui.org/xsd/xml-form-3.xsd","/xsd/xml-form-3.xsd",getClass());
        registrar.addStdResource("http://moqui.org/xsd/xml-screen-3.xsd","/xsd/xml-screen-3.xsd",getClass());
    }
}
