package ni.gob.minsa.laboratorio.utilities.excelUtils;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import java.util.List;
import java.util.Map;

public class ExcelDocumentResultDx {
    Map<String, Object> model;
    HSSFWorkbook workbook;

    public ExcelDocumentResultDx(Map<String, Object> model, HSSFWorkbook workbook) {
        this.model = model;
        this.workbook = workbook;
    }

    public void buildExcel() {
        List<String> columnas = (List<String>) model.get("columnas");
        String tipoReporte =  model.get("tipoReporte").toString();

        // create style for title cells
        CellStyle titleStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeight((short)(16*20));
        font.setColor(HSSFColor.BLACK.index);
        titleStyle.setFont(font);

        // create a new Excel sheet
        HSSFSheet sheet = workbook.createSheet(tipoReporte);
        sheet.setDefaultColumnWidth(30);
        HSSFRow titulo = sheet.createRow(0);
        titulo.createCell(1).setCellValue(model.get("titulo").toString());
        titulo.getCell(1).setCellStyle(titleStyle);

        HSSFRow subtitulo = sheet.createRow(1);
        subtitulo.createCell(1).setCellValue(model.get("subtitulo").toString());
        subtitulo.getCell(1).setCellStyle(titleStyle);
    }
}
