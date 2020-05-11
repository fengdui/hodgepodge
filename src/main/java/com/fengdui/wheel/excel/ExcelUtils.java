package com.fengdui.wheel.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Excel 工具类。
 */
public class ExcelUtils {

	/**
     * 创建一个Excel文件后并输出到前端提供下载。
     * 
     * @param list 数据内容列表
     * @param title 文件名称抬头
     * @param returnList 数据首行列表
     */
    public static void print(List<?> list, String title, List<SQLField> returnList, HttpServletResponse response) {
        HSSFWorkbook workbook = null;
        OutputStream os = null;
        try {
            workbook = new HSSFWorkbook();
            response.setContentType("application/octet-stream ");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + new String((title + ".xls").getBytes("GBK"), "8859_1"));
            // 创建一个Sheet
            HSSFSheet sheet = workbook.createSheet("Sheet1");
            // 在该Sheet创建一个首行
            HSSFRow row = sheet.createRow(0);
            // 填充首行标题栏数据（名称）
            for (int i = 0; i < returnList.size(); i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(returnList.get(i).getDiscription());
                // 填充数据
                for (int j = 0; j < list.size(); j++) {
                    String fieldName = returnList.get(i).getFieldName();
                    Object object = list.get(j);
                    SQLFieldTransform transform = returnList.get(i).getTransformer();
                    Object o = getObject(fieldName, object);
                    String content = getContent(transform, o);
                    HSSFRow row2 = sheet.createRow(j + 1);
                    HSSFCell cell2 = row2.createCell(i);
                    cell2.setCellValue(content);
                }
            }
            os = response.getOutputStream();
            workbook.write(os);
            os.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * 获取内容。
     * 
     * @param transform
     * @param o
     */
    private static String getContent(SQLFieldTransform transform, Object o) {
        String content;
        // 有转换器则经过转换在输出
        if (null != transform) {
            content = transform.transform(o);
        } else {
            content = (null != o) ? o.toString() : "";
        }
        return content;
    }

    /**
     * 获取Object对象。
     */
    private static Object getObject(String fieldName, Object object) {
        Object o = null;
        // 如果是继承于Map的类型则通过get方式获取，否则通过反射get方法的方式获取值
        if (Map.class.isAssignableFrom(object.getClass())) {
            o = DownloadXLS.byMap(object, fieldName);
        }
        return o;
    }
}
