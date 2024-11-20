package org.moqui.idea.plugin.util;

import org.jetbrains.annotations.NotNull;

/**
 * 处理字符串分割后每一段字符串的情况
 */
public class FieldStringSplitUnit {
    private final String originalString;//原始字符串
    private final String trimmedString;//将前后的空格去掉的字符串，以及排序的控制字符
    private final int beginIndex;//原始字符串在原来字符串中的开始位置
    private final int endIndex;//原始字符串在原来字符串中的结束位置
    private final boolean isContainGroovyVariable; //是否包含Groovy变量
    private final boolean isEmpty;//是否不包含任何内容
    private final int trimmedStringBeginIndex;//去掉空格的字符串在原来字符串中的开始位置
    private final int trimmedStringEndIndex;//去掉空格的字符串在原来字符串中结束位置

    FieldStringSplitUnit(@NotNull String originalString, int beginIndex, int endIndex){
        this.originalString = originalString;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        String tmpTrimmedString = originalString.trim();
        //判断fieldName的第一个字符是否为控制字符，如果是，则跳过第一个字符
        if(EntityUtils.fieldHasOrderCommand(tmpTrimmedString)){
            this.trimmedString = tmpTrimmedString.substring(1);
        }else {
            this.trimmedString = tmpTrimmedString;
        }

        this.isEmpty = this.trimmedString.isEmpty();
        if(this.isEmpty) {
            this.trimmedStringBeginIndex = beginIndex;
            this.trimmedStringEndIndex = endIndex;
        }else {
            int index = originalString.indexOf(trimmedString);
            if (index == 0) {
                this.trimmedStringBeginIndex = beginIndex;
                this.trimmedStringEndIndex = endIndex;
            } else {
                this.trimmedStringBeginIndex = beginIndex + index;
                this.trimmedStringEndIndex = this.trimmedStringBeginIndex + trimmedString.length();
            }
        }
        this.isContainGroovyVariable = MyStringUtils.containGroovyVariables(originalString);

    }
    public String getOriginalString(){return this.originalString;}

    public String getTrimmedString() {
        return trimmedString;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getTrimmedStringBeginIndex() {
        return trimmedStringBeginIndex;
    }

    public int getTrimmedStringEndIndex() {
        return trimmedStringEndIndex;
    }

    public boolean isContainGroovyVariable() {
        return isContainGroovyVariable;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
