package com.fengdui.wheel.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 导出模板 
 *
 */
public class ExportExcel {

	private HSSFWorkbook workbook = null;
	private HSSFCellStyle titleStyle = null;
	private HSSFCellStyle dataStyle = null;
	
	public static void resExcel(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		String fileName = request.getParameter("fileName");
		String excelName = StringUtils.isEmpty(fileName) ? "模板.xls" : fileName + ".xls";
		String userAgent = request.getHeader("User-Agent").toUpperCase();
		if (userAgent.indexOf("MSIE") >= 0 || userAgent.indexOf("RV:11.0") >= 0) {
			excelName = URLEncoder.encode(excelName, "UTF-8");// IE浏览器
		} else {
			excelName = new String(excelName.getBytes("UTF-8"), "ISO8859-1");// 谷歌
		}
		response.setHeader("Content-Type","application/vnd.ms.excel;charset=GBK");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + excelName + "\"");
	}
	
	public ExportExcel() {
		workbook = new HSSFWorkbook();
	}

	/**
	 * 列头样式
	 * 
	 * @param workbook
	 * @param sheet
	 * @param columnNum
	 */
	public void setTitleCellStyles(HSSFWorkbook workbook,HSSFSheet sheet,int columnNum) {
		titleStyle = workbook.createCellStyle();
		for(int i=0;i<columnNum;i++){
		    sheet.setColumnWidth(i, 5000); // 设置列宽
		}
	}

	/**
	 * 数据样式
	 * 
	 * @param workbook
	 */
	public void setDataCellStyles(HSSFWorkbook workbook) {
		dataStyle = workbook.createCellStyle();

		// 设置边框
//		dataStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//		dataStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//		dataStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//		dataStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//		 设置背景色
//		dataStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
//		dataStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// 设置居中
//		dataStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		// 设置字体
		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 11); // 设置字体大小
		dataStyle.setFont(font);// 选择需要用到的字体格式
		// 设置自动换行
		dataStyle.setWrapText(true);
	}


	/**
	 * 创建一行表头
	 * 
	 * @param headers
	 * @param sheet
	 */
	public void addHead(List<String> headers, HSSFSheet sheet) {
		HSSFRow startRow = sheet.createRow(0);
		for (int i = 0; i < headers.size(); i++) {
		    HSSFCell cell = startRow.createCell(i);
		    cell.setCellValue("时间格式规范：2010-10-10 10:10:10（符号必须为英文字符）");
		}
		// 合并单元格
        CellRangeAddress cra =new CellRangeAddress(0, 0, 0, headers.size()-1); // 起始行, 终止行, 起始列, 终止列
        sheet.addMergedRegion(cra);
        
		HSSFRow row = sheet.createRow(1);
		for (int i = 0; i < headers.size(); i++) {
			HSSFCell serialNumberCell = row.createCell(i);
			serialNumberCell.setCellValue(headers.get(i));
			serialNumberCell.setCellStyle(titleStyle);
		}
	}

	/**
	 * 创建一行数据
	 * 
	 * @param sheet 
	 * @param list 数据
	 * @param naturalRowIndex 行数
	 */
	public void addRow(HSSFSheet sheet, List<Object> list, int naturalRowIndex) {
		HSSFRow row = sheet.createRow(naturalRowIndex - 1);
		
		if(CollectionUtils.isEmpty(list)){
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			HSSFCell serialNumberCell = row.createCell(i);
			serialNumberCell.setCellValue(list.get(i).toString());
			serialNumberCell.setCellStyle(dataStyle);
		}
	}
	
	/**
	 * 设置下拉列表
	 * 
	 * @param sheet 
	 * @param list 枚举值
	 * @param startRow 开始行数
	 * @param endRow 结束行数
	 * @param startCol 开始列数
	 * @param endCol 结束列数
	 */
	public void setCombo(HSSFSheet sheet,List<String> list, Integer startRow, Integer endRow,
			Integer startCol, Integer endCol) {
		// 得到验证对象
		String[] array = list.toArray(new String[list.size()]);
		DataValidation dataValidationList = this.getDataValidationByFormula(array, startRow, endRow, startCol, endCol);
		sheet.addValidationData(dataValidationList);
	}

	/**
	 * 使用已定义的数据源方式设置一个数据验证
	 * 
	 * @param formulaString 枚举数组
	 * @param startRow 开始行数
	 * @param endRow 结束行数
	 * @param startCol 开始列数
	 * @param endCol 结束列数
	 * @return
	 */
	public DataValidation getDataValidationByFormula(String[] formulaString,Integer startRow, Integer endRow,Integer startCol,Integer endCol) {
		// 加载下拉列表内容
		DVConstraint constraint = DVConstraint.createExplicitListConstraint(formulaString);
		// 设置数据有效性加载在哪个单元格上。
		// 四个参数分别是：起始行、终止行、起始列、终止列
		int firstRow = startRow - 1;
		int lastRow = endRow - 1;
		int firstCol = startCol - 1;
		int lastCol = endCol - 1;
		CellRangeAddressList regions = new CellRangeAddressList(firstRow,
				lastRow, firstCol, lastCol);
		// 数据有效性对象
		return new HSSFDataValidation(regions,constraint);
	}

	
	
	public void write(OutputStream out) throws IOException{
		workbook.write(out);
	}
	
	/**
	 * 创建excel表
	 * 
	 * @param sheetName sheet名称
	 * @param headers 列名
	 * @param enumsMap 列名对应的下拉列表数据
	 */
	public void create(String sheetName, Map<String, String> headers,Map<String, List<String>> enumsMap) {
		HSSFSheet sheet = workbook.createSheet(sheetName);// 工作表对象
		// 设置列头样式
		this.setTitleCellStyles(workbook,sheet,headers.size());
		// 设置数据样式
		this.setDataCellStyles(workbook);
		// 创建一行列头数据
		List<String> header = new ArrayList<>(headers.values());
		List<String> columnNames = new ArrayList<>(headers.keySet());
		// 设置表头
		this.addHead(header, sheet);
		// 设置下拉
		if(CollectionUtils.isEmpty(enumsMap)){
			return;
		}
		for (Entry<String, List<String>> entry : enumsMap.entrySet()) {
			int index = columnNames.indexOf(entry.getKey());
			if (index < 0) {
				continue;
			}
			this.setCombo(sheet, entry.getValue(), 2, 65536, index + 1, index + 1);
		}
	}
	

	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}
	
	
}
