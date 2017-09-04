package ni.gob.minsa.laboratorio.utilities.excelUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractExcelView;

/**
 * This class builds an Excel spreadsheet document using Apache POI library.
 * @author www.codejava.net
 *
 */
public class ExcelBuilder extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

        List<Object[]> listaDxPos = (List<Object[]>) model.get("listaDxPos");
        List<Object[]> listaDxNeg = (List<Object[]>) model.get("listaDxNeg");
        List<Object[]> listaDxInadec = (List<Object[]>) model.get("listaDxInadec");

        List<String> columnas = (List<String>) model.get("columnas");
        boolean incluirMxInadecuadas = (boolean)model.get("incluirMxInadecuadas");
        String tipoReporte =  model.get("tipoReporte").toString();
        // create a new Excel sheet
        HSSFSheet sheet = workbook.createSheet(tipoReporte);
        sheet.setDefaultColumnWidth(30);

        // create style for header cells
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight((short)(11*20));
        font.setColor(HSSFColor.BLACK.index);
        headerStyle.setFont(font);

        //Cell style for content cells
        font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeight((short)(11*20));
        font.setColor(HSSFColor.BLACK.index);

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
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

        //tabla con dx positivos
        // create header row
        HSSFRow header = sheet.createRow(3);
        setHeaderTable(header, headerStyle, columnas);
        // create data rows
        int rowCount = 4;
        int filaInicioNeg = 0;

        for (Object[] registro : listaDxPos) {
            HSSFRow aRow = sheet.createRow(rowCount++);
            setRowData(aRow, registro, contentCellStyle, dateCellStyle);
        }
        if (listaDxPos.size()<=0){
            HSSFRow aRow = sheet.createRow(rowCount++);
            sheet.addMergedRegion(new CellRangeAddress(aRow.getRowNum(), aRow.getRowNum(), 0, columnas.size() - 1));
            aRow.createCell(0).setCellValue(model.get("sinDatos").toString());
            aRow.getCell(0).setCellStyle(noDataCellStyle);
        }

        //tabla con dx negativos
        rowCount+=2; // PARA DEJAR UNA FILA EN BLANCO ENTRE AMBAS TABLAS
        filaInicioNeg = rowCount++;
        HSSFRow headerPos = sheet.createRow(rowCount++);
        setHeaderTable(headerPos, headerStyle, columnas);
        for (Object[] registro : listaDxNeg) {
            HSSFRow aRow = sheet.createRow(rowCount++);
            setRowData(aRow, registro, contentCellStyle, dateCellStyle);
        }
        if (listaDxNeg.size()<=0){
            HSSFRow aRow = sheet.createRow(rowCount);
            sheet.addMergedRegion(new CellRangeAddress(aRow.getRowNum(), aRow.getRowNum(),0,columnas.size()-1));
            aRow.createCell(0).setCellValue(model.get("sinDatos").toString());
            aRow.getCell(0).setCellStyle(noDataCellStyle);
        }
        for(int i =0;i<columnas.size();i++){
            sheet.autoSizeColumn(i);
        }

        // create style for title cells
        CellStyle titleStyle = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeight((short)(16*20));
        font.setColor(HSSFColor.BLACK.index);
        titleStyle.setFont(font);

        // create style for filters cells
        CellStyle filterStyle = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeight((short)(14*20));
        font.setColor(HSSFColor.BLACK.index);
        filterStyle.setFont(font);

        HSSFRow titulo = sheet.createRow(0);
        titulo.createCell(1).setCellValue(model.get("titulo").toString());
        titulo.getCell(1).setCellStyle(titleStyle);

        HSSFRow subtitulo = sheet.createRow(1);
        subtitulo.createCell(1).setCellValue(model.get("subtitulo").toString());
        subtitulo.getCell(1).setCellStyle(titleStyle);

        HSSFRow filtros = sheet.createRow(2);
        filtros.createCell(1).setCellValue(model.get("tablaPos").toString());
        filtros.getCell(1).setCellStyle(filterStyle);

        HSSFRow filtrosNeg = sheet.createRow(filaInicioNeg);
        filtrosNeg.createCell(1).setCellValue(model.get("tablaNeg").toString());
        filtrosNeg.getCell(1).setCellStyle(filterStyle);

        if (incluirMxInadecuadas){
            // create a new Excel sheet
            HSSFSheet sheetInadec = workbook.createSheet("MX INADEC");
            sheetInadec.setDefaultColumnWidth(30);
            //tabla con dx muestras inadecuadas
            // create header row
            HSSFRow headerInadec = sheetInadec.createRow(3);
            setHeaderTable(headerInadec, headerStyle, columnas);
            // create data rows
            rowCount = 4;

            for (Object[] registro : listaDxInadec) {
                HSSFRow aRow = sheetInadec.createRow(rowCount++);
                setRowData(aRow, registro, contentCellStyle, dateCellStyle);
            }
            if (listaDxInadec.size()<=0){
                HSSFRow aRow = sheetInadec.createRow(rowCount);
                sheetInadec.addMergedRegion(new CellRangeAddress(rowCount, rowCount,0,columnas.size()-1));
                aRow.createCell(0).setCellValue(model.get("sinDatos").toString());
                aRow.getCell(0).setCellStyle(noDataCellStyle);
            }
            for(int i =0;i<columnas.size();i++){
                sheetInadec.autoSizeColumn(i);
            }

            HSSFRow tituloInadec = sheetInadec.createRow(0);
            tituloInadec.createCell(1).setCellValue(model.get("titulo").toString());
            tituloInadec.getCell(1).setCellStyle(titleStyle);

            HSSFRow subtituloInadec = sheetInadec.createRow(1);
            subtituloInadec.createCell(1).setCellValue(model.get("subtitulo").toString());
            subtituloInadec.getCell(1).setCellStyle(titleStyle);

            HSSFRow filtroInadec = sheetInadec.createRow(2);
            filtroInadec.createCell(1).setCellValue(model.get("tablaMxInadec").toString());
            filtroInadec.getCell(1).setCellStyle(filterStyle);

        }
	}

    public HSSFWorkbook buildExcel(Map<String, Object> model){
        HSSFWorkbook workbook = new HSSFWorkbook();
        List<Object[]> listaDxPos = (List<Object[]>) model.get("listaDxPos");
        List<Object[]> listaDxNeg = (List<Object[]>) model.get("listaDxNeg");
        List<Object[]> listaDxInadec = (List<Object[]>) model.get("listaDxInadec");

        List<String> columnas = (List<String>) model.get("columnas");
        boolean incluirMxInadecuadas = (boolean)model.get("incluirMxInadecuadas");
        String tipoReporte =  model.get("tipoReporte").toString();
        // create a new Excel sheet
        HSSFSheet sheet = workbook.createSheet(tipoReporte);
        sheet.setDefaultColumnWidth(30);

        // create style for header cells
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight((short)(11*20));
        font.setColor(HSSFColor.BLACK.index);
        headerStyle.setFont(font);

        //Cell style for content cells
        font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeight((short)(11*20));
        font.setColor(HSSFColor.BLACK.index);

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
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

        //tabla con dx positivos
        // create header row
        HSSFRow header = sheet.createRow(3);
        setHeaderTable(header, headerStyle, columnas);
        // create data rows
        int rowCount = 4;
        int filaInicioNeg = 0;

        for (Object[] registro : listaDxPos) {
            HSSFRow aRow = sheet.createRow(rowCount++);
            setRowData(aRow, registro, contentCellStyle, dateCellStyle);
        }
        if (listaDxPos.size()<=0){
            HSSFRow aRow = sheet.createRow(rowCount++);
            sheet.addMergedRegion(new CellRangeAddress(aRow.getRowNum(), aRow.getRowNum(),0,columnas.size()-1));
            aRow.createCell(0).setCellValue(model.get("sinDatos").toString());
            aRow.getCell(0).setCellStyle(noDataCellStyle);
        }

        //tabla con dx negativos
        rowCount+=2; // PARA DEJAR UNA FILA EN BLANCO ENTRE AMBAS TABLAS
        filaInicioNeg = rowCount++;
        HSSFRow headerPos = sheet.createRow(rowCount++);
        setHeaderTable(headerPos, headerStyle, columnas);
        for (Object[] registro : listaDxNeg) {
            HSSFRow aRow = sheet.createRow(rowCount++);
            setRowData(aRow, registro, contentCellStyle, dateCellStyle);
        }
        if (listaDxNeg.size()<=0){
            HSSFRow aRow = sheet.createRow(rowCount);
            sheet.addMergedRegion(new CellRangeAddress(aRow.getRowNum(), aRow.getRowNum(),0,columnas.size()-1));
            aRow.createCell(0).setCellValue(model.get("sinDatos").toString());
            aRow.getCell(0).setCellStyle(noDataCellStyle);
        }
        for(int i =0;i<columnas.size();i++){
            sheet.autoSizeColumn(i);
        }

        // create style for title cells
        CellStyle titleStyle = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeight((short)(16*20));
        font.setColor(HSSFColor.BLACK.index);
        titleStyle.setFont(font);

        // create style for filters cells
        CellStyle filterStyle = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeight((short)(14*20));
        font.setColor(HSSFColor.BLACK.index);
        filterStyle.setFont(font);

        HSSFRow titulo = sheet.createRow(0);
        titulo.createCell(1).setCellValue(model.get("titulo").toString());
        titulo.getCell(1).setCellStyle(titleStyle);

        HSSFRow subtitulo = sheet.createRow(1);
        subtitulo.createCell(1).setCellValue(model.get("subtitulo").toString());
        subtitulo.getCell(1).setCellStyle(titleStyle);

        HSSFRow filtros = sheet.createRow(2);
        filtros.createCell(1).setCellValue(model.get("tablaPos").toString());
        filtros.getCell(1).setCellStyle(filterStyle);

        HSSFRow filtrosNeg = sheet.createRow(filaInicioNeg);
        filtrosNeg.createCell(1).setCellValue(model.get("tablaNeg").toString());
        filtrosNeg.getCell(1).setCellStyle(filterStyle);

        if (incluirMxInadecuadas){
            // create a new Excel sheet
            HSSFSheet sheetInadec = workbook.createSheet("MX INADEC");
            sheetInadec.setDefaultColumnWidth(30);
            //tabla con dx muestras inadecuadas
            // create header row
            HSSFRow headerInadec = sheetInadec.createRow(3);
            setHeaderTable(headerInadec, headerStyle, columnas);
            // create data rows
            rowCount = 4;

            for (Object[] registro : listaDxInadec) {
                HSSFRow aRow = sheetInadec.createRow(rowCount++);
                setRowData(aRow, registro, contentCellStyle, dateCellStyle);
            }
            if (listaDxInadec.size()<=0){
                HSSFRow aRow = sheetInadec.createRow(rowCount);
                sheetInadec.addMergedRegion(new CellRangeAddress(rowCount, rowCount,0,columnas.size()-1));
                aRow.createCell(0).setCellValue(model.get("sinDatos").toString());
                aRow.getCell(0).setCellStyle(noDataCellStyle);
            }
            for(int i =0;i<columnas.size();i++){
                sheetInadec.autoSizeColumn(i);
            }

            HSSFRow tituloInadec = sheetInadec.createRow(0);
            tituloInadec.createCell(1).setCellValue(model.get("titulo").toString());
            tituloInadec.getCell(1).setCellStyle(titleStyle);

            HSSFRow subtituloInadec = sheetInadec.createRow(1);
            subtituloInadec.createCell(1).setCellValue(model.get("subtitulo").toString());
            subtituloInadec.getCell(1).setCellStyle(titleStyle);

            HSSFRow filtroInadec = sheetInadec.createRow(2);
            filtroInadec.createCell(1).setCellValue(model.get("tablaMxInadec").toString());
            filtroInadec.getCell(1).setCellStyle(filterStyle);

        }

        return workbook;
    }

    private void setHeaderTable(HSSFRow header, CellStyle style, List<String> columnas){
        int indice = 0;
        for(String columna : columnas){
            header.createCell(indice).setCellValue(columna);
            header.getCell(indice).setCellStyle(style);
            indice++;
        }
    }

    private void setRowData(HSSFRow aRow, Object[] registro, CellStyle contentCellStyle, CellStyle dateCellStyle){
        int indice = 0;
        for(Object dato : registro){
            aRow.createCell(indice);
            boolean isDate= false;
            if (dato !=null){
                if (dato instanceof Date){
                    aRow.getCell(indice).setCellValue((Date)dato);
                    isDate = true;
                }else if (dato instanceof Integer){
                    aRow.getCell(indice).setCellValue((int)dato);
                }else if (dato instanceof Float){
                    aRow.getCell(indice).setCellValue((float)dato);
                }else if (dato instanceof Double){
                    aRow.getCell(indice).setCellValue((double)dato);
                }
                else{
                    aRow.createCell(indice).setCellValue(dato.toString());
                }
            }
            if (!isDate)
                aRow.getCell(indice).setCellStyle(contentCellStyle);
            else
                aRow.getCell(indice).setCellStyle(dateCellStyle);

            indice++;
        }
    }

}