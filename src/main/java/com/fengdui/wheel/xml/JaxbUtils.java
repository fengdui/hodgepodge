package com.fengdui.wheel.xml;

import lombok.Data;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class JaxbUtils
{

    /**
     * JavaBean转换成xml
     * 默认编码UTF-8
     *
     * @param obj
     * @return
     */
    public static String convertToXml(Object obj)
    {
        return convertToXml(obj, "UTF-8");
    }

    /**
     * JavaBean转换成xml
     *
     * @param obj
     * @param encoding
     * @return
     */
    public static String convertToXml(Object obj, String encoding)
    {
        String result = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * xml转换成JavaBean
     *
     * @param xml
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T converyToJavaBean(String xml, Class<T> c)
    {
        T t = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(new StringReader(xml));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return t;
    }

    public static void main(String[] args)
    {
        JaxbUtils util = new JaxbUtils();
        util.testStrToObj();
        util.testObjToStr();
    }

    private void testStrToObj()
    {
        String str = "<xml>\n" +
                " <ToUserName>toUser</ToUserName>\n" +
                " <FromUserName>CDATA[fromUser</FromUserName> \n" +
                " <CreateTime>1348831860</CreateTime>\n" +
                " <MsgType>text></MsgType>\n" +
                " <Content>this is a test</Content>\n" +
                " <MsgId>1234567890123456</MsgId>\n" +
                " </xml>";
        Xml msg = converyToJavaBean(str, Xml.class);
        System.out.println(msg.getContent());
    }

    private void testObjToStr()
    {
        Item item = new Item();
        item.setDescription("fsdfsd");
        item.setUrl("http://www.baiduc.od");
        List<Item> items = new ArrayList<Item>();
        items.add(item);
        items.add(item);
        Articles ar = new Articles();
        ar.setItem(items);

        Xml xml = new Xml();
        xml.setArticles(ar);
        xml.setArticleCount(ar.getItem().size());
        String str = convertToXml(xml);
        System.out.println(str);
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    @XmlType(name = "xml", propOrder = {"ToUserName", "FromUserName", "MsgType", "CreateTime", "MsgId", "Content", "Event", "EventKey", "Ticket", "Articles", "ArticleCount", "PicUrl", "Title", "Url", "Description"})
    public static class Xml
    {
        private String ToUserName = "";
        private String FromUserName = "";
        private String MsgType = "";
        private String Content = "";
        private String PicUrl = "";
        private String Event = "";
        private String EventKey = "";
        private String Ticket = "";
        private long MsgId = 0;
        private long CreateTime = 0;

        private String Description = "";
        private String Url = "";
        private String Title = "";
        private Articles Articles;
        private int ArticleCount;

    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "item")
    @XmlType(propOrder = {"Title", "Description", "PicUrl", "Url"})
    public static class Item
    {
        private String Title = "";
        private String Description = "";
        private String PicUrl = "";
        private String Url = "";
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    public static class Articles
    {
        private List<Item> item = new ArrayList<Item>();
    }
}
