package org.moqui.idea.plugin.util;

import org.jetbrains.annotations.NotNull;

public class EntityNameDescriptor {
    public static EntityNameDescriptor of(@NotNull String fullName){
        return new EntityNameDescriptor(fullName);
    }

    EntityNameDescriptor(@NotNull String fullName){
        int index = fullName.lastIndexOf(EntityUtils.ENTITY_NAME_DOT);
        if (index<0) {
            myEntityName = fullName;
            myPackageName = MyStringUtils.EMPTY_STRING;
            myEntityNameIndex = 0;
        }else {
            myPackageName = fullName.substring(0,index);
            myEntityName = fullName.substring(index+1);
            myEntityNameIndex = index + 1;
        }
    }
    EntityNameDescriptor(String entityName,String packageName){
        this.myEntityName = entityName;
        this.myPackageName = packageName;
    }
    private String myEntityName;
    private String myPackageName;
    private int myEntityNameIndex = 0;

    public String getEntityName() {
        return myEntityName;
    }

    public String getPackageName() {
        return myPackageName;
    }

    public void setEntityName(@NotNull String entityName) {
        this.myEntityName = entityName;
    }

    public void setPackageName(@NotNull String packageName) {
        this.myPackageName = packageName;
        myEntityNameIndex = packageName.length()+1;
    }

    public int getEntityNameIndex() {
        return myEntityNameIndex;
    }

    public String getFullName(){
        if(MyStringUtils.isEmpty(myPackageName)) {
            return myEntityName;
        }else {
            return myPackageName + EntityUtils.ENTITY_NAME_DOT + myPackageName;
        }

    }

}
