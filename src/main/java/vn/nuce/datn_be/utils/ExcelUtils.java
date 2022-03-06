package vn.nuce.datn_be.utils;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.nuce.datn_be.exception.DatnException;
import vn.nuce.datn_be.model.enumeration.ErrorStatus;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by voncount on 6/30/15.
 */
public class ExcelUtils {
    private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    private static String TEMPLATE_BODY_BLOCK = "TMP_BLOCK_BODY";

    public static Workbook cloneExelFile(String filePath){
        InputStream inputStream;
        inputStream = ExcelUtils.class.getResourceAsStream(filePath);
        return cloneExelFile(inputStream);
    }
    public static Workbook cloneExelXlsxFile(String filePath){
        InputStream inputStream;
        inputStream = ExcelUtils.class.getResourceAsStream(filePath);
        return cloneExelXlsxFile(inputStream);
    }

    public static Workbook cloneExelFile(InputStream inputStream){
        Workbook workbook;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException ex) {
            logger.error("cloneExcelFile", ex);
            throw new DatnException(ErrorStatus.UNHANDLED_ERROR);
        }
        return workbook;
    }

    public static Workbook cloneExelXlsxFile(InputStream inputStream){
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException ex) {
            logger.error("cloneExcelFile", ex);
            throw new DatnException(ErrorStatus.UNHANDLED_ERROR);
        }
        return workbook;
    }

    public static List<List<String>> readFile(Workbook workbook, Boolean... readEmptyRow){
        return readFile(workbook, 0, false, readEmptyRow);
    }

    public static List<List<String>> readUnicodeFile(Workbook workbook, Boolean... readEmptyRow){
        return readFile(workbook, 0, true, readEmptyRow);
    }

    public static List<List<String>> readFile(Workbook workbook, int sheetIndex, boolean readUnicodeChar, Boolean... readEmptyRow){
        List<List<String>> result = new ArrayList<>();
        Sheet sheet0 = workbook.getSheetAt(sheetIndex);
        Iterator<Row> rowIterator = sheet0.rowIterator();
        int i = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(readEmptyRow.length==0){
                List<String> items = readRow(row, readUnicodeChar);
                if(!items.isEmpty()){
                    result.add(items);
                }
            }else{
                List<String> items = readRow(row, readUnicodeChar, readEmptyRow);
                result.add(items);
            }
        }
        return result;
    }

    public static Map<Long, List<String>> readUnicodeFileToMap(Workbook workbook, Boolean... readEmptyRow){
        return readFileToMap(workbook, 0, true, readEmptyRow);
    }

    public static Map<Long, List<String>> readFileToMap(Workbook workbook, int sheetIndex, boolean readUnicodeChar, Boolean... readEmptyRow){
        Map<Long, List<String>> result = new HashMap<Long, List<String>>();
        Sheet sheet0 = workbook.getSheetAt(sheetIndex);
        Iterator<Row> rowIterator = sheet0.rowIterator();
        int i = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(readEmptyRow.length==0){
                List<String> items = readRow(row, readUnicodeChar);
                if(!items.isEmpty()){
                    result.put((long) row.getRowNum(), items);
                }
            }else{
                List<String> items = readRow(row, readUnicodeChar, readEmptyRow);
                result.put((long) row.getRowNum(), items);
            }
        }
        return result;
    }

    public static List<List<String>> readFile(InputStream inputStream, boolean... xlsx){
        try {
            Workbook workbook = null;
            if(xlsx.length>0 && xlsx[0]){
                workbook = new XSSFWorkbook(inputStream);
            }else{
                workbook = new HSSFWorkbook(inputStream);
            }

            return ExcelUtils.readFile(workbook);
        } catch (IOException ex) {
            logger.error("read excel failed", ex);
            throw new DatnException(ErrorStatus.UNHANDLED_ERROR);
        }
    }

    public static List<List<String>> readUnicodeFile(InputStream inputStream, boolean... xlsx){
        try {
            Workbook workbook = null;
            if(xlsx.length>0 && xlsx[0]){
                workbook = new XSSFWorkbook(inputStream);
            }else{
                workbook = new HSSFWorkbook(inputStream);
            }

            return ExcelUtils.readUnicodeFile(workbook);
        } catch (IOException ex) {
            logger.error("read excel failed", ex);
            throw new DatnException(ErrorStatus.UNHANDLED_ERROR);
        }
    }

    public static List<String> readRow(Row row, boolean readUnicodeChar, Boolean... readEmptyRow){
        FormulaEvaluator evaluator = row.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        List<String> items = new ArrayList<>();
        boolean isRowHasData = false;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                items.add("");
            }else {
                if(cell.getCellType()==Cell.CELL_TYPE_FORMULA){
                    evaluator.evaluateFormulaCell(cell);
                    cell.setCellType(cell.getCachedFormulaResultType());
                }
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        items.add(String.valueOf(cell.getDateCellValue().getTime()));
                    } else {
                        DecimalFormat decimalFormat = new DecimalFormat("#.###");
                        items.add(decimalFormat.format(cell.getNumericCellValue()));
                    }
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String txt = cell.getStringCellValue();
                    if (!readUnicodeChar) {
                        txt = txt.replaceAll("\\P{Print}", "");
                    }
                    items.add(txt);
                } else {
                    cell.setCellType(Cell.CELL_TYPE_BLANK);
                    items.add("");
                }
            }
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) isRowHasData = true;
        }
        if(readEmptyRow.length==0){
            if(!isRowHasData) items.clear();
        }
        return items;
    }

    public static void writeCell(Workbook workbook, int sheetIdx, int rowIdx, int colIdx, Object value, CellStyle cellStyle){
        if(value==null) return;
        Sheet sheet0 = workbook.getSheetAt(sheetIdx);
        Cell cell = sheet0.getRow(rowIdx).getCell(colIdx);
        cell.setCellStyle(cellStyle);
        if(value instanceof Date){
            cell.setCellValue((Date)value);
        }else if(value instanceof Double){
            cell.setCellValue((Double)value);
        }else if(value instanceof Float){
            cell.setCellValue((Float)value);
        }else if(value instanceof Long){
            cell.setCellValue((Long)value);
        }else{
            cell.setCellValue(value.toString());
        }
    }

    public static Cell readCell(Workbook workbook, int sheetIdx, int rowIdx, int colIdx){
        Sheet sheet0 = workbook.getSheetAt(sheetIdx);
        Cell cell = sheet0.getRow(rowIdx).getCell(colIdx);
        return cell;
    }

    public static ExcelTemplate readTemplate(Workbook workbook, int sheetIdx){
        ExcelTemplate template = new ExcelTemplate();
        List<List<String>> data = readFile(workbook, true);
        for(int i=0; i<data.size(); i++){
            for(int j=0; j<data.get(i).size(); j++){
                //logger.info("i: {}, j: {}, v: {}", new Object[]{i, j, data.get(i).get(j)});
                if(TEMPLATE_BODY_BLOCK.equals(data.get(i).get(j))){
                    template.setBodyColIdx(j);
                    template.setBodyRowIdx(i);
                    break;
                }
            }
        }
        if(template.getBodyColIdx()<0 || template.getBodyRowIdx()<0){
            logger.error("Tempalte BODY not found");
            throw new RuntimeException("template body not found");
        }
        return template;
    }

    public static void setCellBackground(XSSFFont font, CellStyle backgroundStyle) {
        backgroundStyle.setFont(font);
        backgroundStyle.setWrapText(true);
        backgroundStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        backgroundStyle.setBorderBottom(CellStyle.BORDER_THIN);
        backgroundStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        backgroundStyle.setBorderLeft(CellStyle.BORDER_THIN);
        backgroundStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        backgroundStyle.setBorderRight(CellStyle.BORDER_THIN);
        backgroundStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        backgroundStyle.setBorderTop(CellStyle.BORDER_THIN);
        backgroundStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
    }

    public static List<String> readFirtColumn(InputStream inputStream, boolean... xlsx){
        int sheetIdx = 0;
        int cellIdx = 0;

        try {
            Workbook workbook = null;
            if(xlsx.length>0 && xlsx[0]){
                workbook = new XSSFWorkbook(inputStream);
            }else{
                workbook = new HSSFWorkbook(inputStream);
            }

            List<String> result = new ArrayList<>();
            Sheet sheet0 = workbook.getSheetAt(sheetIdx);
            Iterator<Row> rowIterator = sheet0.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String cellValue = readCell(cellIdx, row, false);
                if(!cellValue.isEmpty()){
                    result.add(cellValue);
                }
            }
            return result;
        } catch (IOException ex) {
            logger.error("read excel failed", ex);
            throw new DatnException(ErrorStatus.UNHANDLED_ERROR);
        }
    }

    private static String readCell(int cellIdx, Row row, boolean readUnicodeChar, Boolean... readEmptyRow){
        FormulaEvaluator evaluator = row.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();

        Cell cell = row.getCell(cellIdx);
        String cellVal = null;

        if (cell == null) {
            cellVal = "";
        } else {
            if(cell.getCellType()==Cell.CELL_TYPE_FORMULA){
                evaluator.evaluateFormulaCell(cell);
                cell.setCellType(cell.getCachedFormulaResultType());
            }
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    cellVal = String.valueOf(cell.getDateCellValue().getTime());
                } else {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    cellVal = decimalFormat.format(cell.getNumericCellValue());
                }
            } else if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String txt = cell.getStringCellValue();
                if (!readUnicodeChar) {
                    txt = txt.replaceAll("\\P{Print}", "");
                }
                cellVal = txt;
            } else {
                cell.setCellType(Cell.CELL_TYPE_BLANK);
                cellVal = "";
            }
        }
        return cellVal;
    }

    public static CellStyle createCellStyleColor(Workbook wb, short color) {
        Font font = wb.createFont();
        font.setColor(color);
        CellStyle errorCellStyle = wb.createCellStyle();
        errorCellStyle.setFont(font);
        errorCellStyle.setBottomBorderColor(color);
        errorCellStyle.setTopBorderColor(color);
        errorCellStyle.setLeftBorderColor(color);
        errorCellStyle.setRightBorderColor(color);
        return errorCellStyle;
    }

    public static void main(String[] args){

    }

    public static CellStyle cellStyleWithColor(Workbook wb, short color) {
        Font font = wb.createFont();
        font.setColor(color);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setBottomBorderColor(color);
        cellStyle.setTopBorderColor(color);
        cellStyle.setLeftBorderColor(color);
        cellStyle.setRightBorderColor(color);
        return cellStyle;
    }

    public static List<List<String>> readUnicodeFileImp(Integer cellResult, List<Cell> lstCell,Workbook workbook, Boolean... readEmptyRow){
        return readFileImp(cellResult, lstCell, workbook, 0, true, readEmptyRow);
    }

    public static List<List<String>> readFileImp(Integer cellResult, List<Cell> lstCell, Workbook workbook, int sheetIndex, boolean readUnicodeChar, Boolean... readEmptyRow){
        List<List<String>> result = new ArrayList<>();
        Sheet sheet0 = workbook.getSheetAt(sheetIndex);
        Iterator<Row> rowIterator = sheet0.rowIterator();
        int i = 1;

        CellStyle styleSuccess = workbook.createCellStyle();
        styleSuccess.setWrapText(true);
        styleSuccess.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(readEmptyRow.length==0){
                List<String> items = readRowImp(cellResult, lstCell, i, styleSuccess, row, readUnicodeChar);
                i++;
                if(!items.isEmpty()){
                    result.add(items);
                }
            }else{
                List<String> items = readRowImp(cellResult, lstCell, i, styleSuccess, row, readUnicodeChar, readEmptyRow);
                i++;
                if (null != items && items.size() > 0) {
                    result.add(items);
                }
            }
        }
        return result;
    }

    public static List<String> readRowImp(Integer cellResult, List<Cell> lstCell, Integer k, CellStyle styleSuccess, Row row, boolean readUnicodeChar, Boolean... readEmptyRow){
        FormulaEvaluator evaluator = row.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        List<String> items = new ArrayList<>();
        if (row.getLastCellNum() < 1) return null;

        boolean isRowHasData = false;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                items.add("");
            }else {
                if(cell.getCellType()==Cell.CELL_TYPE_FORMULA){
                    evaluator.evaluateFormulaCell(cell);
                    cell.setCellType(cell.getCachedFormulaResultType());
                }
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        items.add(String.valueOf(cell.getDateCellValue().getTime()));
                    } else {
                        DecimalFormat decimalFormat = new DecimalFormat("#.###");
                        items.add(decimalFormat.format(cell.getNumericCellValue()));
                    }
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String txt = cell.getStringCellValue();
                    if (!readUnicodeChar) {
                        txt = txt.replaceAll("\\P{Print}", "");
                    }
                    items.add(txt);
                } else {
                    cell.setCellType(Cell.CELL_TYPE_BLANK);
                    items.add("");
                }
            }
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) isRowHasData = true;
        }
        if (k == 1) {
            Cell cell = row.getCell(row.getLastCellNum() -1);
            lstCell.add(cell);
        }
        if (k >1) {
            Cell s = row.createCell(cellResult);
            s.setCellStyle(styleSuccess);
            lstCell.add(s);
        }

        if(readEmptyRow.length==0){
            if(!isRowHasData) items.clear();
        }
        return items;
    }
}

