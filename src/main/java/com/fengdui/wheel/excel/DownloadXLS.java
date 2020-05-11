package com.fengdui.wheel.excel;

import java.beans.PropertyDescriptor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadXLS {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
     * Map方式通过get获取属性
     */
    public static Object byMap(Object obj, String fieldName) {
        return ((Map<?, ?>) obj).get(fieldName);
    }

	
	public void writeListToServer(List set, String title, List<SQLField> returnList, ServletContext servletContext, String fileName){
		try{
			FileOutputStream os = new FileOutputStream(fileName);
			WritableWorkbook  wwb = Workbook.createWorkbook(os);
			WritableSheet sheet=wwb.createSheet("sheet", 0);
			
			WritableCell titleCell=sheet.getWritableCell(1, 1); 
			for(int i=0;i<returnList.size();i++){
				Label l1 = new Label(i, 0, returnList.get(i).getDiscription());
				sheet.addCell(l1);
				for(int j=1;j<set.size()+1;j++){
					//如果获取不到列则为空
					String content="";
                    try {
                        SQLFieldTransform transform = returnList.get(i).getTransformer();
                        Object bean = set.get(j - 1);
                        String fieldName = returnList.get(i).getFieldName();
                        Object o = null;
                        // 如果是继承于Map的类型则通过get方式获取，否则通过反射get方法的方式获取值
                        if (Map.class.isAssignableFrom(bean.getClass()))
                            o = byMap(bean, fieldName);
                        // 有转换器则经过转换在输出
                        if (transform != null)
                            content = transform.transform(o);
                        else
                            content = o != null ? o.toString() : "";
                    }catch (Exception e) {
                    
                    }
					Label l2=new Label(i,j, content);
					sheet.addCell(l2);
				}
			}
			wwb.write();
			wwb.close();
			os.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void printList(List<?> set, String title, List<SQLField> returnList, HttpServletResponse response) {
		ServletOutputStream os = null;
		WritableWorkbook wwb = null;
		try{
			os = response.getOutputStream();
			response.setContentType( "application/octet-stream "); 
			response.setHeader("Content-Disposition", "attachment; filename=" + new String((title+".xls").getBytes("GBK"),"8859_1"));
			wwb = Workbook.createWorkbook(os);
			WritableSheet sheet = wwb.createSheet("sheet", 0);
			
			WritableCell titleCell = sheet.getWritableCell(1, 1);
			for (int i = 0; i < returnList.size(); i++) {
				Label l1 = new Label(i, 0, returnList.get(i).getDiscription());
				sheet.addCell(l1);
				for (int j = 1; j < set.size() + 1; j++) {
					// 如果获取不到列则为空
					String content = "";
					try {
						SQLFieldTransform transform = returnList.get(i).getTransformer();
						Object bean = set.get(j - 1);
						String fieldName = returnList.get(i).getFieldName();
						Object o = null;
						// 如果是继承于Map的类型则通过get方式获取，否则通过反射get方法的方式获取值
						if (Map.class.isAssignableFrom(bean.getClass())) {
							o = byMap(bean, fieldName);
						}
						// 有转换器则经过转换在输出
						if (transform != null) {
							content = transform.transform(o);
						} else {
							content = o != null ? o.toString() : "";
						}
					} catch (Exception e) {
						
					}
					Label l2 = new Label(i, j, content);
					sheet.addCell(l2);
				}
			}
			wwb.write();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(wwb!=null){
				try {
					wwb.close();
				} catch (WriteException e) {
					e.printStackTrace();
					logger.error("DownloadXLS:wwb--->"+e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("DownloadXLS:"+e.getMessage());
				}
			}
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("DownloadXLS:ServletOutputStream--->"+e.getMessage());
				}
			}
		}
	}
	
	public File getExcelFile(List set,String title,List<SQLField> returnList){
		WritableWorkbook  wwb = null;
		BufferedOutputStream os = null;
		try{
			File file = new File(title +"_"+ System.currentTimeMillis() + ".xls");
			if(file.exists()){
				file.delete();
				file = new File(title +"_"+ System.currentTimeMillis() + ".xls");
			}
			file.createNewFile();
			os = new BufferedOutputStream(new FileOutputStream(file));
			wwb = Workbook.createWorkbook(os);
			WritableSheet sheet=wwb.createSheet("sheet", 0);
			
			WritableCell titleCell=sheet.getWritableCell(1, 1); 
			for(int i=0;i<returnList.size();i++){
				Label l1 = new Label(i, 0, returnList.get(i).getDiscription());
				sheet.addCell(l1);
				for(int j=1;j<set.size()+1;j++){
					//如果获取不到列则为空
					String content="";
					try{
					SQLFieldTransform transform=returnList.get(i).getTransformer();
					Object bean=set.get(j-1);
					String fieldName = returnList.get(i).getFieldName();
					Object o = null;
					//如果是继承于Map的类型则通过get方式获取，否则�?过反射get方法的方式获取�?
					if(Map.class.isAssignableFrom(bean.getClass()))
						o=byMap(bean, fieldName);
					//有转换器则经过转换在输出
					if(transform!=null)
						content=transform.transform(o);
					else
						content=o!=null?o.toString():"";
					}catch(Exception e){
					}
					Label l2=new Label(i,j, content);
					sheet.addCell(l2);
				}
			}
			wwb.write();
			return file;
		}catch(Exception e){
			logger.error("getExcelFile Exception : "+e);
			return null;
		}finally{
			if(wwb!=null){
				try {
					wwb.close();
				} catch (WriteException e) {
					e.printStackTrace();
					logger.error("DownloadXLS:wwb--->"+e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("DownloadXLS:"+e.getMessage());
				}
			}
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("DownloadXLS:ServletOutputStream--->"+e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 从硬盘导出文件
	 * @param fileName
	 * @param title
	 * @param response
	 * @return boolean false表示文件不存在或导出失败
	 */
	public boolean exportFile(String fileName, String title, HttpServletResponse response) {
		ServletOutputStream os = null;
		WritableWorkbook wwb = null;
		Workbook wb = null;
		try{
			File file = new File(fileName);
			if(!file.exists()) {
				prepareDir(fileName);
				return false;
			}
			wb = Workbook.getWorkbook(file);
			os = response.getOutputStream();
			response.setContentType( "application/octet-stream "); 
			response.setHeader("Content-Disposition", "attachment; filename=" + new String((title+".xls").getBytes("GBK"),"8859_1"));
			wwb = Workbook.createWorkbook(os, wb);
			wwb.write();
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return false;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if(wwb!=null){
				try {
					wwb.close();
				} catch (WriteException e) {
					logger.error("DownloadXLS.exportFile:wwb--->" + e.getMessage(), e);
				} catch (IOException e) {
					logger.error("DownloadXLS.exportFile:" + e.getMessage(), e);
				}
			}
		}
	}

	private void prepareDir(String fileName) {
		String folder = fileName.substring(0, fileName.lastIndexOf("/"));
		File dir = new File(folder);
		if(!dir.isDirectory()) {
			dir.mkdirs();
		}
	}
}