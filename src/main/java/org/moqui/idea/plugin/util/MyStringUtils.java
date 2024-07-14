package org.moqui.idea.plugin.util;

import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.completion.CompletionUtil.DUMMY_IDENTIFIER;
import static com.intellij.codeInsight.completion.CompletionUtil.DUMMY_IDENTIFIER_TRIMMED;

/**
 * The type String utils.
 */
public final class MyStringUtils {
    private MyStringUtils() {
        throw new UnsupportedOperationException();
    }

    public static final String EMPTY_STRING = "";
    public static final String TRANSITION_NAME_REGEXP = "^[a-z][a-zA-Z0-9_-]*$";
    public static final String SCREEN_FILE_PATH_REGEXP = "(?:(?:\\.\\./)+)?[A-Z][a-zA-Z0-9_/-]+|\\.|\\.\\.";
    public static final String CONTAIN_VARIABLE_REGEXP =".*(\\$\\{)[a-zA-Z0-9_\\-\\.](\\}).*";
    public static final String ABSOLUTE_URL_REGEXP = "^//([a-zA-Z][\\w\\/]*)+$";
    public static final String COMPONENT_CHILD_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]component[/|\\\\])[a-zA-Z0-9_\\-\\.]+";
    public static final String COMPONENT_DATA_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]component[/|\\\\])[a-zA-Z0-9_\\-\\.]+([/|\\\\]data[/|\\\\]?).*";
    public static final String COMPONENT_ENTITY_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]component[/|\\\\])[a-zA-Z0-9_\\-\\.]+([/|\\\\]entity[/|\\\\]?).*";
    public static final String COMPONENT_SERVICE_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]component[/|\\\\])[a-zA-Z0-9_\\-\\.]+([/|\\\\]service[/|\\\\]?).*";
    public static final String COMPONENT_SCREEN_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]component[/|\\\\])[a-zA-Z0-9_\\-\\.]+([/|\\\\]screen[/|\\\\]?).*";
    public static final String COMPONENT_SRC_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]component[/|\\\\])[a-zA-Z0-9_\\-\\.]+([/|\\\\]src[/|\\\\]?).*";
    public static final String FRAMEWORK_DATA_PATH_REGEXP = ".*([/|\\\\]framework[/|\\\\]data[/|\\\\]?).*";
    public static final String FRAMEWORK_ENTITY_PATH_REGEXP = ".*([/|\\\\]framework[/|\\\\]entity[/|\\\\]?).*";
    public static final String FRAMEWORK_SRC_PATH_REGEXP = ".*([/|\\\\]framework[/|\\\\]src[/|\\\\]?).*";

    public static final String FRAMEWORK_SCREEN_PATH_REGEXP = ".*([/|\\\\]framework[/|\\\\]screen[/|\\\\]?).*";
    public static final String FRAMEWORK_SERVICE_PATH_REGEXP = ".*([/|\\\\]framework[/|\\\\]service[/|\\\\]?).*";
    public static final String BASE_COMPONENT_DATA_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]base-component[/|\\\\])[a-zA-Z0-9_\\-\\.]+([/|\\\\]data[/|\\\\]?).*";
    public static final String BASE_COMPONENT_ENTITY_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]base-component[/|\\\\])[a-zA-Z0-9_\\-\\.]+([/|\\\\]entity[/|\\\\]?).*";
    public static final String BASE_COMPONENT_SERVICE_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]base-component[/|\\\\])[a-zA-Z0-9_\\-\\.]+([/|\\\\]service[/|\\\\]?).*";
    public static final String BASE_COMPONENT_SCREEN_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]base-component[/|\\\\])[a-zA-Z0-9_\\-\\.]+([/|\\\\]screen[/|\\\\]?).*";
    public static final String BASE_COMPONENT_SRC_PATH_REGEXP = ".*([/|\\\\]runtime[/|\\\\]base-component[/|\\\\])[a-zA-Z0-9_\\-\\.]+([/|\\\\]src[/|\\\\]?).*";
    /**
     * Upper case first char string.
     *
     * @param str the str
     * @return the string
     */
    public static String upperCaseFirstChar(String str) {
        if (str == null) {
            return null;
        } else {
            return str.isEmpty() ? str : str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }


    /**
     * Lower case first char string.
     *
     * @param str the str
     * @return the string
     */
    public static String lowerCaseFirstChar(String str) {
        if (str == null) {
            return null;
        } else {
            return str.isEmpty() ? str : str.substring(0, 1).toLowerCase() + str.substring(1);
        }
    }


    /**
     * convert string from slash style to camel style, such as my_course will convert to MyCourse
     *
     * @param str the str
     * @return string
     */
    public static String dbStringToCamelStyle(String str) {
        if (str != null) {
            str = str.toLowerCase();
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(str.charAt(0)).toUpperCase());
            for (int i = 1; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c != '_') {
                    sb.append(c);
                } else {
                    if (i + 1 < str.length()) {
                        sb.append(String.valueOf(str.charAt(i + 1)).toUpperCase());
                        i++;
                    }
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Is empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    /**
     * Is Not empty
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isNotEmpty(Object str) {
        return !(isEmpty(str));
    }

    /**
     * 驼峰转下划线
     * @param camelStr
     * @return
     */
    public static String camelToSlash(String camelStr){
        String[] strings = splitByCharacterType(camelStr, true);
        return Arrays.stream(strings).map(MyStringUtils::lowerCaseFirstChar).collect(Collectors.joining("_"));
    }

    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        } else if (str.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            char[] c = str.toCharArray();
            List<String> list = new ArrayList();
            int tokenStart = 0;
            int currentType = Character.getType(c[tokenStart]);

            for(int pos = tokenStart + 1; pos < c.length; ++pos) {
                int type = Character.getType(c[pos]);
                if (type != currentType) {
                    if (camelCase && type == 2 && currentType == 1) {
                        int newTokenStart = pos - 1;
                        if (newTokenStart != tokenStart) {
                            list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                            tokenStart = newTokenStart;
                        }
                    } else {
                        list.add(new String(c, tokenStart, pos - tokenStart));
                        tokenStart = pos;
                    }

                    currentType = type; }
            }

            list.add(new String(c, tokenStart, c.length - tokenStart));
            return (String[])list.toArray(new String[list.size()]);
        }
    }

    public static @NotNull
    String removeDummy(@Nullable String str) {
        if (str == null) {
            return EMPTY_STRING;
        }
        return StringUtil.trim(str.replace(DUMMY_IDENTIFIER, "").replace(DUMMY_IDENTIFIER_TRIMMED, ""));
    }
    public static @NotNull
    String getDummyFrontString(@Nullable String str) {
        if (str == null) {
            return EMPTY_STRING;
        }
        int index = str.indexOf(DUMMY_IDENTIFIER_TRIMMED);
        if(index<0) {
            return str;
        }else {
            return str.substring(0,index);
        }
    }

    public static String formatPresentationName(String tagName,String str){
        return tagName +": " + str;
    }

    /**
     * 对xml字符串进行转化，以便能在html中显示
     * @param xml
     * @return
     */
    public static String formatXmlForHtml(@NotNull String xml){
        return xml.replaceAll("<","&lt;")
                .replaceAll(">","&gt;");
    }



}
