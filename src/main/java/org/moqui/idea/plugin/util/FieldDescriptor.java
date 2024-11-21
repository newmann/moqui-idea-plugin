package org.moqui.idea.plugin.util;

import org.jetbrains.annotations.NotNull;

/**
 * 处理字段字符串分割后每一段字段字符串的情况
 * 处理字符串中包含排序字符的情况
 */
public class FieldDescriptor {
    private final String originalString;//原始字符串
    private final String fieldName;//将前后的空格去掉的字符串，以及排序的控制字符
    private final int originalBeginIndex;//原始字符串在原来字符串中的开始位置
    private final int originalEndIndex;//原始字符串在原来字符串中的结束位置
    private final boolean isContainGroovyVariable; //是否包含Groovy变量
    private final boolean isEmpty;//是否不包含任何内容
    private final int fieldNameBeginIndex;//去掉空格的字符串在原来字符串中的开始位置
    private final int fieldNameEndIndex;//去掉空格的字符串在原来字符串中结束位置

    public static FieldDescriptor of(@NotNull String originalString, int beginIndex, int endIndex){
        return new FieldDescriptor(originalString,beginIndex,endIndex);
    }

    FieldDescriptor(@NotNull String originalString, int beginIndex, int endIndex){
        this.originalString = originalString;
        this.originalBeginIndex = beginIndex;
        this.originalEndIndex = endIndex;
        String tmpTrimmedString = originalString.trim();
        //判断fieldName的第一个字符是否为控制字符，如果是，则跳过第一个字符
        if (EntityUtils.fieldHasOrderCommand(tmpTrimmedString)) {
            this.fieldName = tmpTrimmedString.substring(1);
        } else {
            this.fieldName = tmpTrimmedString;
        }

        this.isEmpty = this.fieldName.isEmpty();
        if (this.isEmpty) {
            this.fieldNameBeginIndex = beginIndex;
            this.fieldNameEndIndex = endIndex;
        } else {
            int index = originalString.indexOf(fieldName);
            if (index == 0) {
                this.fieldNameBeginIndex = beginIndex;
                this.fieldNameEndIndex = endIndex;
            } else {
                this.fieldNameBeginIndex = beginIndex + index;
                this.fieldNameEndIndex = this.fieldNameBeginIndex + fieldName.length();
            }
        }
        this.isContainGroovyVariable = MyStringUtils.containGroovyVariables(originalString);

    }
    public String getOriginalString(){return this.originalString;}

    public String getFieldName() {
        return fieldName;
    }

    public int getOriginalBeginIndex() {
        return originalBeginIndex;
    }

    public int getOriginalEndIndex() {
        return originalEndIndex;
    }

    public int getFieldNameBeginIndex() {
        return fieldNameBeginIndex;
    }

    public int getFieldNameEndIndex() {
        return fieldNameEndIndex;
    }

    public boolean isContainGroovyVariable() {
        return isContainGroovyVariable;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
