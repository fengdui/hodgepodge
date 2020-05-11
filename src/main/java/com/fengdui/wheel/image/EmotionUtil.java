package com.fengdui.wheel.image;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmotionUtil
{
    private static String patternStr = "";

    public static Pattern dataPattern = null;

    private static Map<String, String> emotionMap = new LinkedHashMap<String, String>(150);

    // 微信邀请码前缀
    private static final String WEDDING_INVATION_CODE_PREFIX = "wedding_invation_code-";

    private static List<String> imgList = new ArrayList<String>(100);


    static
    {
        //1: 微信表情的映射表

        //初始化QQ1-5页表情，共一百个,代码顺序和微信表情一一对应，改变时注意。
        String serverName = "/q3/static/img/emotion";

        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<120;i++)
        {
            String num = "";
            if( i< 10)
            {
                num = num + "00" + i;
            }else if( i < 100)
            {
                num = num + "0" + i;
            }
            else num = num +  i;
            String img = serverName + "/em/f" + num + ".png";
            emotionMap.put("[" + num + "]", img);
            imgList.add(img);
            stringBuffer.append("\\[" + num + "\\]|");
        }
        String temp = stringBuffer.toString();
        patternStr = "(" + temp.substring(0,temp.length() - 1) +  ")";

        dataPattern = Pattern.compile(patternStr);

    }

    public static String parse(String src)
    {
        Matcher matcher = dataPattern.matcher(src);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            matcher.appendReplacement(sb, "<img class='emotion' src="+emotionMap.get(matcher.group(1))+">");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String getPatternStr()
    {
        return patternStr;
    }

    public static List<String> getImgList()
    {
        return imgList;
    }

    public static Map<String,String> getEmotionMap()
    {
        return emotionMap;
    }


}
