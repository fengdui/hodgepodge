package com.fengdui.framework.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class Dom4jUtils {

    private Dom4jUtils() {
    }

    private static final String DEFAULT_CHAR_SET = "utf-8";//默认编码
    private static SAXReader reader;

    static {
        reader = new SAXReader();
    }

    /**
     * 获取Document对象.<br>
     *
     * @param in 流
     * @return
     * @throws DocumentException
     * @author hong
     */
    public static Document getDocument(InputStream in) throws Exception {
        return reader.read(in);
    }

    /**
     * 获取Document对象.<br>
     *
     * @param xml XML字符串
     * @return
     * @throws Exception
     * @author hong
     */
    public static Document getDocument(String xml) throws Exception {
        byte[] arr = xml.getBytes(DEFAULT_CHAR_SET);
        InputStream in = new ByteArrayInputStream(arr);

        return getDocument(in);
    }

    /**
     * 获取根节点.<br>
     *
     * @param xml
     * @return
     * @throws Exception
     * @author hong
     */
    public static Element getRootElement(String xml) throws Exception {
        Document doc = getDocument(xml);
        return doc.getRootElement();
    }

    /**
     * 获取根节点.<br>
     *
     * @param in
     * @return
     * @throws Exception
     * @author hong
     */
    public static Element getRootElement(InputStream in) throws Exception {
        Document doc = getDocument(in);
        return doc.getRootElement();
    }

    /**
     * 根据节点名称获取子节点.<br>
     *
     * @param parent
     * @param name
     * @return
     * @author hong
     */
    public static Element getElementByName(Element parent, String name) {
        return parent.element(name);
    }

    /**
     * 根据节点名称获取子节点.<br>
     *
     * @param parent
     * @param name
     * @return
     * @author hong
     */
    @SuppressWarnings("unchecked")
    public static List<Element> getElementsByName(Element parent, String name) {
        return parent.elements(name);
    }

    /**
     * 获取节点属性值.<br>
     *
     * @param parant
     * @param name
     * @return
     * @author hong
     */
    public static String getAtrributeByName(Element parant, String name) {
        return parant.attributeValue(name);
    }

}

