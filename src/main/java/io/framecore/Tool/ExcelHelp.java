package io.framecore.Tool;

 
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.framecore.Frame.Note;
import io.framecore.Frame.Result;
import io.framecore.Frame.ViewStore;

public class ExcelHelp {
	
	public static <T extends ViewStore> Workbook  toExcel (List<T> list,Class<T> type, String title) throws IOException
	{

		SimpleDateFormat df_default = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Workbook workbook = WorkbookFactory.create(true);
		Sheet sheet = workbook.createSheet();//.getSheetAt(0);
		 
		//title
		Row rowTitle = sheet.createRow(0);
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,5));		
		Cell cellTitle = rowTitle.createCell(0);
		cellTitle.setCellValue(title);
		
		
		CellStyle headTitle = workbook.createCellStyle();
		Font fontTitle =  workbook.createFont();
		fontTitle.setBold(true); 
		fontTitle.setFontHeightInPoints((short) 22);
		headTitle.setFont(fontTitle);
		
		cellTitle.setCellStyle(headTitle);
		
		// head
		Row rowHead = sheet.createRow(1);
		
		CellStyle headStyle = workbook.createCellStyle();
		Font font =  workbook.createFont();
		font.setBold(true); 
		headStyle.setFont(font);
		
		Map<Integer,String> map = new HashMap<Integer, String>();
		Map<Integer,Note> mapNote = new HashMap<Integer, Note>();

		  
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			if (!method.getName().startsWith("set")) {
				continue;
			}
			JsonProperty jp = method.getAnnotation(JsonProperty.class);
			if (jp == null) {
				continue;
			}

			int index = jp.index();
			if (index < 0) {
				continue;
			}

			Note note = method.getAnnotation(Note.class);
			if (note == null) {
				continue;
			}

			String headVal = note.value();
			String key = jp.value();

			Cell cellHead = rowHead.createCell(index);
			cellHead.setCellValue(headVal);
			cellHead.setCellStyle(headStyle);

			map.put(index, key);
			mapNote.put(index, note);

		}
	
		int rowIndex=2;
		
		if(list!=null)
		{
			for (T t : list) {
				
				ViewStore vs = (ViewStore)t;
				
				Row item = sheet.createRow(rowIndex);			
				for (Integer cellIndex : map.keySet()) {
					
					Object val = vs.get(map.get(cellIndex));
					if(val==null)
					{
						continue;
					}
					
					if(val.getClass().equals(Integer.class))
					{
						item.createCell(cellIndex).setCellValue(Integer.parseInt(val.toString()));  
					}
					if(val.getClass().equals(String.class))
					{
						item.createCell(cellIndex).setCellValue(val.toString());  
					}
					if(val.getClass().equals(Date.class))
					{
						if(!mapNote.get(cellIndex).format().isEmpty())
						{
							String str =new SimpleDateFormat(mapNote.get(cellIndex).format()).format(val);
							item.createCell(cellIndex).setCellValue(str);
						}
						else
						{
							item.createCell(cellIndex).setCellValue(df_default.format(val));  
						}
					}
					if(val.getClass().equals(Double.class))
					{
						item.createCell(cellIndex).setCellValue(Double.parseDouble(val.toString()));  
					}							 
					
				}			
				
				
				rowIndex++;
			}
		}

		
		
		return workbook;
		
	}
		
	public static <T extends ViewStore> Result toList(InputStream inputStream,Class<T> type) throws EncryptedDocumentException, IOException, InstantiationException, IllegalAccessException
	{
		
		SimpleDateFormat df_default = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Workbook workbook = WorkbookFactory.create(inputStream);
		
	    inputStream.close();
	    // 在工作簿获取目标工作表
	    Sheet sheet = workbook.getSheetAt(0);
	    // 获取到最后一行
	    int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
	    // 该集合用来储存行对象
	    List<T> detaileds = new ArrayList<T>();
	    // 遍历整张表，从第二行开始，第一行的表头不要，循环次数不大于最后一行的值
	    for (int i = 2; i < physicalNumberOfRows; i++) {
	      // 该对象用来储存行数据
	      T vo = type.newInstance(); // new T();
	      // 获取当前行数据
	      Row row = sheet.getRow(i);
	      // 获取目标单元格的值并存进对象中
	      	      
	        
	      Method[] methods =type.getMethods();
			for (Method method : methods) {
				if(!method.getName().startsWith("set"))
				{
					continue;
				}
				JsonProperty jp =method.getAnnotation(JsonProperty.class);
				if(jp==null)
				{
					continue;
				}
				
				
				int index = jp.index();
				if(index<0)
				{
					continue;
				}
				Note note = method.getAnnotation(Note.class);
				if (note == null) {
					continue;
				}
				 
				if(row.getCell(index) == null)
				{
					continue;
				}
				String key =jp.value();
				
				Object val =null;				
				
				 			 
				try {
					if(row.getCell(index).getCellType().equals(CellType.STRING))
					{
						val =row.getCell(index).getStringCellValue();
					}
					if(row.getCell(index).getCellType().equals(CellType.NUMERIC))
					{
						val =row.getCell(index).getNumericCellValue();
					}
					
					if(val==null)
					{
						continue;
					}
					
					Class<?> typePar = method.getParameters()[0].getType();
					
					if(typePar.equals(java.lang.String.class))
					{
						vo.set(key, val.toString());
					}
					if(typePar.equals(java.lang.Integer.class))
					{						
						vo.set(key, Double.valueOf(val.toString()).intValue());
					}
					if(typePar.equals(java.lang.Double.class))
					{
												
						double doubleVal = new BigDecimal(val.toString()).setScale(4,RoundingMode.HALF_UP).doubleValue();
						
						vo.set(key, doubleVal);
					}
					if(typePar.equals(java.util.Date.class))
					{	
						if(!note.format().isEmpty())
						{
							String str =new SimpleDateFormat(note.format()).format(val);
							vo.set(key, str);
						}
						else
						{
							vo.set(key, df_default.parse(val.toString()));
						}
						
					}
					
				} catch (Exception e) {
					 String msg ="excel数据不规范：key：%s, 第%s列,第%s行";

					 return Result.failure(String.format(msg, key,index,i));
				}				
	
			}	
	      
			 detaileds.add(vo);
	      
	    }
		
		return Result.succeed(detaileds);
		
	}

	public static void write(HttpServletResponse response, Workbook book, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String name = new String(fileName.getBytes("GBK"), "ISO8859_1") + ".xlsx";
        response.addHeader("Content-Disposition", "attachment;filename=" + name);
        ServletOutputStream out = response.getOutputStream();
        book.write(out);
        out.flush();
        out.close();
    }

}
