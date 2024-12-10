package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.moqui.idea.plugin.util.MyStringUtils.EMPTY_STRING;

public class BeginAndEndCharPattern {
    public static BeginAndEndCharPattern of(@NotNull String checkString){
        return new BeginAndEndCharPattern(checkString);
    }
    public static BeginAndEndCharPattern of(@NotNull PsiElement psiElement){
        //处理XmlTag的name问题
        if(psiElement instanceof XmlTag xmlTag) {
            int index = psiElement.getText().indexOf(xmlTag.getName());
            if(index<0) return new BeginAndEndCharPattern(EMPTY_STRING);

            return new BeginAndEndCharPattern(psiElement.getText().substring(0,index+xmlTag.getName().length()));
        }else {
            return new BeginAndEndCharPattern(psiElement.getText());
        }
    }

    private String content = EMPTY_STRING;
    private String beginChar = EMPTY_STRING;
    private String endChar = EMPTY_STRING;

    public BeginAndEndCharPattern(@Nullable String checkString){
        if(checkString == null) return;
        if (checkString.charAt(0) == '"' || checkString.charAt(0) == '\'' || checkString.charAt(0) == '<') {
            beginChar = checkString.substring(0, 1);
            checkString = checkString.substring(1);
        }

        if(checkString.endsWith("\"")|| checkString.endsWith("'")) {
            endChar = checkString.substring(checkString.length()-1);
            checkString = checkString.substring(0,checkString.length()-1);
        }
        content = checkString;

    }
    public BeginAndEndCharPattern(@NotNull PsiElement psiElement){
        this(psiElement.getText());
    }

    public String getContent() {
        return content;
    }

    public String getBeginChar() {
        return beginChar;
    }

    public String getEndChar() {
        return endChar;
    }

}
