package ni.gob.minsa.laboratorio.utilities.excelUtils;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import java.util.List;
import java.util.Map;

import static ni.gob.minsa.laboratorio.utilities.excelUtils.ExcelBuilder.setHeaderTable;
import static ni.gob.minsa.laboratorio.utilities.excelUtils.ExcelBuilder.setRowData;

public class ExcelDocumentTiemposProc {

    Map<String, Object> model;
    HSSFWorkbook workbook;

    public ExcelDocumentTiemposProc(Map<String, Object> model,
                                HSSFWorkbook workbook){
        this.model = model;
        this.workbook = workbook;
    }

    public void buildExcel(){
        List<String> columnas = (List<String>) model.get("columnas");
        List<Object[]> datos = (List<Object[]>) model.get("datos");
        String tipoReporte =  model.get("reporte").toString();

        // create style for title cells
        CellStyle titleStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeight((short)(16*20));
        font.setColor(HSSFColor.BLACK.index);
        titleStyle.setFont(font);

        // create style for header cells
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeight((short)(11*20));
        font.setColor(HSSFColor.BLACK.index);

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy hh:mm AM/PM"));
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        dateCellStyle.setBorderTop(BorderStyle.THIN);
        dateCellStyle.setBorderLeft(BorderStyle.THIN);
        dateCellStyle.setBorderRight(BorderStyle.THIN);
        dateCellStyle.setFont(font);

        CellStyle contentCellStyle = workbook.createCellStyle();
        contentCellStyle.setBorderBottom(BorderStyle.THIN);
        contentCellStyle.setBorderTop(BorderStyle.THIN);
        contentCellStyle.setBorderLeft(BorderStyle.THIN);
        contentCellStyle.setBorderRight(BorderStyle.THIN);
        contentCellStyle.setFont(font);

        CellStyle noDataCellStyle = workbook.createCellStyle();
        noDataCellStyle.setAlignment(HorizontalAlignment.CENTER);
        noDataCellStyle.setFont(font);

        font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeight((short)(11*20));
        font.setColor(HSSFColor.RED.index);

        CellStyle alertCellStyle = workbook.createCellStyle();
        alertCellStyle.setBorderBottom(BorderStyle.THIN);
        alertCellStyle.setBorderTop(BorderStyle.THIN);
        alertCellStyle.setBorderLeft(BorderStyle.THIN);
        alertCellStyle.setBorderRight(BorderStyle.THIN);
        alertCellStyle.setFont(font);

        // create a new Excel sheet
        HSSFSheet sheet = workbook.createSheet(tipoReporte);
        sheet.setDefaultColumnWidth(30);

        HSSFRow header = sheet.createRow(3);
        setHeaderTable(header, headerStyle, columnas);
        int rowCount = 4;
        for (Object[] registro : datos) {
            HSSFRow aRow = sheet.createRow(rowCount++);
            setRowData(aRow, registro, contentCellStyle, dateCellStyle);
        }

        if (datos.size() <= 0) {
            HSSFRow aRow = sheet.createRow(rowCount++);
            sheet.addMergedRegion(new CellRangeAddress(aRow.getRowNum(), aRow.getRowNum(), 0, columnas.size() - 1));
            aRow.createCell(0).setCellValue(model.get("sinDatos").toString());
            aRow.getCell(0).setCellStyle(noDataCellStyle);
        }else{
            //estilo para celdas de totales en la última fila
            Font font2 = workbook.createFont();
            font2.setFontName("Arial");
            font2.setFontHeight((short) (10 * 20));
            font2.setColor(HSSFColor.BLACK.index);
            font2.setBold(true);
            CellStyle totalCellStyle = workbook.createCellStyle();
            totalCellStyle.setAlignment(HorizontalAlignment.CENTER);
            totalCellStyle.setBorderBottom(BorderStyle.THIN);
            totalCellStyle.setBorderTop(BorderStyle.THIN);
            totalCellStyle.setBorderLeft(BorderStyle.THIN);
            totalCellStyle.setBorderRight(BorderStyle.THIN);
            totalCellStyle.setFont(font2);
        }
        //ajustar el ancho de la celda al tamanio del texto
        for(int i =0;i<columnas.size();i++){
            sheet.autoSizeColumn(i);
        }

        HSSFRow titulo = sheet.createRow(0);
        titulo.createCell(0).setCellValue(model.get("titulo").toString());
        titulo.getCell(0).setCellStyle(titleStyle);

        HSSFRow subtitulo = sheet.createRow(1);
        subtitulo.createCell(0).setCellValue(model.get("subtitulo").toString());
        subtitulo.getCell(0).setCellStyle(titleStyle);

        HSSFRow rangos = sheet.createRow(2);
        rangos.createCell(0).setCellValue(model.get("rangoFechas").toString());
        rangos.getCell(0).setCellStyle(titleStyle);
    }
}
