package org.moqui.idea.plugin.util;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static org.moqui.idea.plugin.util.MyStringUtils.isEmpty;
import static org.moqui.idea.plugin.util.ServiceUtils.SERVICE_NAME_DOT;
import static org.moqui.idea.plugin.util.ServiceUtils.SERVICE_NAME_HASH;

public class ServiceCallDescriptor {
    public static ServiceCallDescriptor of(@NotNull String fulllName){
        return new ServiceCallDescriptor(fulllName);
    }
    public static ServiceCallDescriptor of(String className,String verb, String noun){
        ServiceCallDescriptor target = new ServiceCallDescriptor();
        target.setClassName(className);
        target.setVerb(verb);
        target.setNoun(noun);
        return target;
    }


    public ServiceCallDescriptor(){}
    /**
     * 从字符串中获取服务的信息,标准格式为：
     * className.verb#noun
     * 但也有例外，noun可能会不存在，这种情况也需要考虑
     * 如果是CRUD服务，格式为：
     * verb#entityName
     * 针对crud服务，返回的className为空
     */
    public ServiceCallDescriptor(@NotNull String fullName){
        final int index = fullName.lastIndexOf(SERVICE_NAME_HASH);
        if(index < 0) {
            //没有noun的处理
            final int verbIndex = fullName.lastIndexOf(SERVICE_NAME_DOT);
            myHasNoun = false;
            if (verbIndex >= 0) {
                myVerbIndex = verbIndex + 1;
                myVerb = fullName.substring(myVerbIndex);
                myClassName = fullName.substring(0, verbIndex);
            } else {
                myVerbIndex = 0;
                myVerb = MyStringUtils.EMPTY_STRING;
                myClassName = fullName;
            }
            myIsCRUD = false;

        }else {
            myNounIndex = index + 1;
            myNoun = fullName.substring(myNounIndex);

            //不是服务，是对entity的CRUD
            final String pathVerb = fullName.substring(0, index);

            final int verbIndex = pathVerb.lastIndexOf(SERVICE_NAME_DOT);

            if (verbIndex >= 0) {//标准的 service Call
                myVerbIndex = verbIndex + 1;
                myVerb = pathVerb.substring(myVerbIndex);
                myClassName = pathVerb.substring(0, verbIndex);
                myIsCRUD = false;
            } else { //crud
                myVerbIndex = 0;
                myVerb = pathVerb;
                myClassName = MyStringUtils.EMPTY_STRING;
                myIsCRUD = true;
            }
        }
    }
    private boolean myHasNoun = true;
    private boolean myIsCRUD = false; //是否为标准的CRUD操作，如果是，则verb为标准的crud，noun为entity name
    private String myClassName = MyStringUtils.EMPTY_STRING;
    private String myVerb = MyStringUtils.EMPTY_STRING;
    private String myNoun = MyStringUtils.EMPTY_STRING;
    private int myVerbIndex = 0;
    private int myNounIndex = 0;

    public String getClassName() {
        return myClassName;
    }

    public void setClassName(String className) {

        this.myClassName = className;
        myVerbIndex = className.length()+1;
        myNounIndex = myVerbIndex + myVerb.length();

    }

    public String getNoun() {
        return myNoun;
    }

    public void setNoun(String noun) {
        this.myNoun = noun;
    }

    public String getVerb() {
        return myVerb;
    }

    public void setVerb(String myVerb) {
        this.myVerb = myVerb;
        myNounIndex = myVerbIndex + myVerb.length();
    }
    public String getActionString(){
        return myVerb + SERVICE_NAME_HASH + myNoun;
    }

    public int getNounIndex() {
        return myNounIndex;
    }

    public int getVerbIndex() {
        return myVerbIndex;
    }

    public boolean isCRUD() {
        return myIsCRUD;
    }

    public boolean hasNoun() {
        return myHasNoun;
    }

    public Optional<String> getServiceCallOptional(){
        String result = getServiceCallString();
        if(isEmpty(result)) {
            return Optional.empty();
        }else {
            return Optional.of(result);
        }

    }
    public String getServiceCallString(){
        if(isEmpty(myVerb) && isEmpty(myNoun)) return MyStringUtils.EMPTY_STRING;
        if(myIsCRUD) {
            return myVerb + SERVICE_NAME_HASH + myNoun;
        }else {
            if(isEmpty(myNoun)) {
                return myClassName + SERVICE_NAME_DOT + myVerb;
            }else {
                return myClassName + SERVICE_NAME_DOT + myVerb + SERVICE_NAME_HASH + myNoun;
            }
        }

    }

}
