package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Component;


public final class LocationUtils {
    private LocationUtils() {
        throw new UnsupportedOperationException();
    }

    public static final class LocationDescriptor{

        private final String location;
        private String filePath = MyStringUtils.EMPTY_STRING;
        private String contentName = MyStringUtils.EMPTY_STRING;
        private int contentStartIndex;
        public LocationDescriptor(@NotNull String location){
            this.location = location;
            if(MyStringUtils.isEmpty(location)) {
                isEmpty = true;
            }else {
                isEmpty = false;
                if(location.contains("${")) {
                    isVariable = true;
                    return;
                }
                int poundIndex = location.indexOf("#");
                if(poundIndex>=0) {
                    isContent = true;
                    contentStartIndex = poundIndex + 1;
                    contentName = location.substring(poundIndex+1);
                    filePath = location.substring(0,poundIndex);
                }else {
                    if(location.contains("//")) {

                        filePath = location;
                    }else {
                        //可能是FormSingle或FormList的extends本文件中的名称
                        isContent = true;
                        contentStartIndex = 1;
                        contentName = location;
                    }
                }

            }
        }

        private boolean isEmpty = false;
        /**
         * Location中包含有${}的变量
         */
        private boolean isVariable = false;

        /**
         * Location中带有#，指向一个文件内部的内容
         */
        private boolean isContent =false;

        public String getLocation() {return location;}

        public String getContentName() {
            return contentName;
        }

        public String getFilePath() {
            return filePath;
        }

        public boolean isVariable() {
            return isVariable;
        }

        public boolean isContent() {
            return isContent;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        /**
         * 根据location，获取在location中定义的path和fileName，并按先后次序添加到Array中
         * @return
         */
        public String[] getPathFileFromLocation(){
            return MyDomUtils.getPathFileFromLocation(location);
        }

        public int getContentStartIndex() {
            return contentStartIndex;
        }
    }




}
