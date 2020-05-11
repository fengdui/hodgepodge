package com.fengdui.wheel.regex;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public void phones() {
        Map<String, String> phones = new HashMap<>();
        phones.put("ar-DZ", "/^(\\+?213|0)(5|6|7)\\d{8}$/");
        phones.put("ar-SY", "/^(!?(\\+?963)|0)?9\\d{8}$/");
        phones.put("ar-SA", "/^(!?(\\+?966)|0)?5\\d{8}$/");
        phones.put("en-US", "/^(\\+?1)?[2-9]\\d{2}[2-9](?!11)\\d{6}$/)");
        phones.put("cs-CZ", " /^(\\+?420)? ?[1-9][0-9]{2} ?[0-9]{3} ?[0-9]{3}$/");
        phones.put("de-DE", " /^(\\+?49[ \\.\\-])?([\\(]{1}[0-9]{1,6}[\\)])?([0-9 \\.\\-\\/]{3,20})((x|ext|extension)[ ]?[0-9]{1,4})?$/");
        phones.put("da-DK", " /^(\\+?45)?(\\d{8})$/");
        phones.put("el-GR", " /^(\\+?30)?(69\\d{8})$/");
        phones.put("en-AU", " /^(\\+?61|0)4\\d{8}$/");
        phones.put("en-GB", " /^(\\+?44|0)7\\d{9}$/");
        phones.put("en-HK", " /^(\\+?852\\-?)?[569]\\d{3}\\-?\\d{4}$/");
        phones.put("en-IN", " /^(\\+?91|0)?[789]\\d{9}$/");
        phones.put("en-NZ", " /^(\\+?64|0)2\\d{7,9}$/");
        phones.put("en-ZA", " /^(\\+?27|0)\\d{9}$/");
        phones.put("en-ZM", " /^(\\+?26)?09[567]\\d{7}$/");
        phones.put("es-ES", " /^(\\+?34)?(6\\d{1}|7[1234])\\d{7}$/");
        phones.put("fi-FI", " /^(\\+?358|0)\\s?(4(0|1|2|4|5)?|50)\\s?(\\d\\s?){4,8}\\d$/");
        phones.put("fr-FR", " /^(\\+?33|0)[67]\\d{8}$/");
        phones.put("he-IL", " /^(\\+972|0)([23489]|5[0248]|77)[1-9]\\d{6}/");
        phones.put("hu-HU", " /^(\\+?36)(20|30|70)\\d{7}$/");
        phones.put("it-IT", " /^(\\+?39)?\\s?3\\d{2} ?\\d{6,7}$/");
        phones.put("ja-JP", " /^(\\+?81|0)\\d{1,4}[ \\-]?\\d{1,4}[ \\-]?\\d{4}$/");
        phones.put("ms-MY", " /^(\\+?6?01){1}(([145]{1}(\\-|\\s)?\\d{7,8})|([236789]{1}(\\s|\\-)?\\d{7}))$/");
        phones.put("nb-NO", " /^(\\+?47)?[49]\\d{7}$/");
        phones.put("nl-BE", " /^(\\+?32|0)4?\\d{8}$/");
        phones.put("nn-NO", " /^(\\+?47)?[49]\\d{7}$/");
        phones.put("pl-PL", " /^(\\+?48)? ?[5-8]\\d ?\\d{3} ?\\d{2} ?\\d{2}$/");
        phones.put("pt-BR", " /^(\\+?55|0)\\-?[1-9]{2}\\-?[2-9]{1}\\d{3,4}\\-?\\d{4}$/");
        phones.put("pt-PT", " /^(\\+?351)?9[1236]\\d{7}$/");
        phones.put("ru-RU", " /^(\\+?7|8)?9\\d{9}$/");
        phones.put("sr-RS", " /^(\\+3816|06)[- \\d]{5,9}$/");
        phones.put("tr-TR", " /^(\\+?90|0)?5\\d{9}$/");
        phones.put("vi-VN", " /^(\\+?84|0)?((1(2([0-9])|6([2-9])|88|99))|(9((?!5)[0-9])))([0-9]{7})$/");
        phones.put("zh-CN", " /^(\\+?0?86\\-?)?1[345789]\\d{9}$/");
        phones.put("zh-TW", " /^(\\+?886\\-?|0)?9\\d{8}$/");
    }

    public static final String illegalChars="+-&|!(){}[]^”~*?:\\'";

    public static Pattern telephonePattern = Pattern.compile("^\\d{11}$");
    public static Pattern emailPattern = Pattern.compile("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
    public static Pattern htmlPattern = Pattern.compile("<[^>]+>"); //定义HTML标签的正则表达式
    public static Pattern scriptPattern = Pattern.compile("<script[^>]*?>[\\s\\S]*?<\\/script>",Pattern.CASE_INSENSITIVE);

    public static boolean isTelephone(String telephone)
    {
        return telephonePattern.matcher(telephone).find();
    }

    public static boolean isEmail(String email)
    {
        return emailPattern.matcher(email).find();
    }

    public static String escapeHtml(String htmlStr) {
        htmlStr = htmlStr.replaceAll("\\<br/\\>","/r/n");

        Matcher m_script=scriptPattern.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签

        Matcher m_html=htmlPattern.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        htmlStr = htmlStr.replaceAll("/r/n","<br/>");
        return htmlStr;
    }
    public static String clearIllegalChar(String input) {
        return StringUtils.replaceChars(input, illegalChars, "");
    }

}
