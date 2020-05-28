package com.fengdui.wheel.regex;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

/**
 * @desc Validator.java <br>
 *       字段验证器
 */
public class Validator {
	//正则中需要被转义的关键字
	public final static Set<Character> RE_KEYS = Sets.newHashSet(new Character[] { '$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|' });
	//英文字母 数字和下划线
	public final static Pattern GENERAL = Pattern.compile("^\\w+$");
	//中英文字符
	public final static Pattern ONLY_CH_EN_NO = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9]+$");
	//中文字、英文字母、数字和下划线
	public final static Pattern GENERAL_WITH_CHINESE = Pattern.compile("^[\\u0391-\\uFFE5\\w]+$");
	//中文
	public final static String RE_CHINESE = "[\u4E00-\u9FFF]";
	//数字
	public final static Pattern NUMBER = Pattern.compile("\\d+");
	//IP v4
	public final static Pattern IPV4 = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
	//货币
	public final static Pattern MONEY = Pattern.compile("^(\\d+(?:\\.\\d+)?)$");
	//邮件
	public final static Pattern EMAIL = Pattern.compile("(\\w|.)+@\\w+(\\.\\w+){1,2}");
	public final static Pattern EMAIL_PATTERN = Pattern.compile("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
	public final static Pattern FORMAT_EMAIL = Pattern.compile("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
	//身份证号码 出生日期只支持到到2999年
	public final static Pattern CITIZEN_ID = Pattern.compile("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X|x)");
	//邮编
	public final static Pattern ZIP_CODE = Pattern.compile("\\d{6}");
	//邮编(中国)
	public final static Pattern BIRTHDAY = Pattern.compile("(\\d{4})(/|-|\\.)(\\d{1,2})(/|-|\\.)(\\d{1,2})日?$");
	//URL
	public final static Pattern URL = Pattern.compile("(https://|http://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?");
	//UUID
	public final static Pattern UUID = Pattern.compile("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$");
	//html
	public final static Pattern HTML_PATTERN = Pattern.compile("<[^>]+>"); //定义HTML标签的正则表达式
	//script
	public final static Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*?>[\\s\\S]*?<\\/script>",Pattern.CASE_INSENSITIVE);

	//移动电话(中国)
	public final static Pattern MOBILE = Pattern.compile("1\\d{10}");
	//手机号 全世界
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
}
