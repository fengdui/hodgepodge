package com.fengdui.wheel.excel;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * 导入excel，获取excel内容 
 *
 */
public class ExcelUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
	
	@Deprecated
	public static Map<String, List<Object>> parseExcelToMap(InputStream inputStream) throws Exception {
		Workbook workbook = null;
		Map<String, List<Object>> result = new HashMap<>();
		int rowSize = 0;
		workbook = WorkbookFactory.create(inputStream);
		// 遍历excel中的所有sheet
		try {
			for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
				// 创建测试数据集合
				List<Object> testDataRowList = new ArrayList<>();
				Sheet sheet = workbook.getSheetAt(sheetIndex);
				String sheetName = sheet.getSheetName();

				result.put(sheetName, testDataRowList);
				List<String> headers = createHeaders(sheet,1); // 获取sheet的第一行表头
				for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
					Row row = sheet.getRow(rowIndex);
					if (null == row) {
						continue;
					}
					// 创建测试数据
					Object testDataRow = new Object();
					// 获取该行有多少个单元格
					int tempRowSize = row.getLastCellNum() + 1;
					// 获得所有行中单元格的最大数目
					if (tempRowSize > rowSize) {
						rowSize = tempRowSize;
					}
					for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
						Cell cell = row.getCell(columnIndex);
//						testDataRow.put(headers.get(columnIndex), getCellValue(cell));
					}
					testDataRowList.add(testDataRow);
				}

			}
		} catch (Exception e) {
			logger.error(e.toString());
			throw e;
		}finally{
//			IOUtils.close(inputStream);
//			IOUtils.close(workbook);
		}
		return result;
	}
    
    
	/**
	 * 读取excel里的数据
	 * 
	 * @param inputStream
	 * @param startRow 从第startRow行开始读取
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Map<String, Object>> Map<String, List<T>> parseExcelToMap(InputStream inputStream,int startRow) throws Exception {
		Workbook workbook = null;
		Map<String, List<T>> result = new HashMap<>();
		int rowSize = 0;
		workbook = WorkbookFactory.create(inputStream);
		// 遍历excel中的所有sheet
		try {
			for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
				// 创建测试数据集合
				List<T> testDataRowList = new ArrayList<>();
				Sheet sheet = workbook.getSheetAt(sheetIndex);
				String sheetName = sheet.getSheetName();

				result.put(sheetName, testDataRowList);
				List<String> headers = createHeaders(sheet,startRow-1); // 获取sheet的第一行表头
				
				// 删除表末尾的空行（excel中直接按del删除一行数据，这行空数据仍会被读取。）
				delLastBlankRow(sheet);
				
				// 默认从excel的第三行开始读取数据。（第一行为提示语，第二行为表头）
				for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
					Row row = sheet.getRow(rowIndex);
					// 创建测试数据
					Map<String, Object> testDataRow = new HashMap<>();
					if (null == row) {
					    testDataRowList.add((T) testDataRow);
					    continue;
                    }
					// 获取该行有多少个单元格
					int tempRowSize = row.getLastCellNum() + 1;
					// 获得所有行中单元格的最大数目
					if (tempRowSize > rowSize) {
						rowSize = tempRowSize;
					}
					for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
					    if(StringUtils.isEmpty(headers.get(columnIndex))){
					        continue;
					    }
						Cell cell = row.getCell(columnIndex);
						testDataRow.put(headers.get(columnIndex), getCellValue(cell));
					}
					testDataRowList.add((T) testDataRow);
				}

			}
		} catch (Exception e) {
			logger.error("Excel数据导入失败");
			throw e;
		} finally {
//			IOUtils.close(inputStream);
//			IOUtils.close(workbook);
		}
		return result;
	}
	
	/**
	 * 判断是否是空行
	 * 
	 * @param row
	 * @return
	 */
    private static boolean isRowEmpty(Row row) {
        if(row == null){
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }
    
    /**
     * 删除末尾空行
     * 
     * @param sheet
     */
    private static void delLastBlankRow(Sheet sheet){
        for (int rowIndex = sheet.getLastRowNum(); rowIndex > 0; rowIndex--) {
            Row row = sheet.getRow(rowIndex);
            if(!isRowEmpty(row)){
                break;
            }
			if(row == null){
                continue;
            }
            sheet.removeRow(row);
        }
    }
    
    
    /**
     * 获得表的字段列表
     * 
     * @param sheet
     * @return
     */
    private static List<String> createHeaders(Sheet sheet,int index) {
    	try {
			Row row = sheet.getRow(index);
			List<String> list = new ArrayList<>();
			for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
				Cell cell = row.getCell(i);
				list.add(cell.getStringCellValue());
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException("获取【"+sheet.getSheetName()+"】列名出错");
		}
    }
    
    /**
     * 获取单元格里的值
     * 
     * @param cell
     * @return
     * @throws Exception 
     */
	private static Object getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		Object value = "";
		switch (cell.getCellType()) {
		case STRING:
			value = cell.getStringCellValue();
			break;
		case NUMERIC:
			value = getNumericCellValue(cell);
			break;
		case BLANK:
			break;
		case ERROR:
			break;
		case BOOLEAN:
			value = cell.getBooleanCellValue();
			break;
		default:
			value = "";
		}
		return value;
	}
	
	private static Object getNumericCellValue(Cell cell) {
		// 如果是时间类型
		if (HSSFDateUtil.isCellDateFormatted(cell)) {
			return cell.getDateCellValue();
		}
		double num = cell.getNumericCellValue();
		int tempInt = new Double(num).intValue();
		if (tempInt == num) {
			return tempInt;
		} 
		return BigDecimal.valueOf(num).toPlainString();
	}
}
