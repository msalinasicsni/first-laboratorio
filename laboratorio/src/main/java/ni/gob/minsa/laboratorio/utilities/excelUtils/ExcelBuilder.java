package ni.gob.minsa.laboratorio.utilities.excelUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
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
        String reporte = model.get("reporte").toString();
        if (reporte.equalsIgnoreCase("DXVIG"))
            buildExcelDocumentVigDx(model, workbook);
        if (reporte.equalsIgnoreCase("DXEXAMS")) {
            ExcelDocumentDxExams excelDocumentDxExams = new ExcelDocumentDxExams(model, workbook);
            excelDocumentDxExams.buildExcel();
        }
	}


    public void buildExcelDocumentVigDx(Map<String, Object> model, HSSFWorkbook workbook){
        List<Object[]> listaDxPos = (List<Object[]>) model.get("listaDxPos");
        List<Object[]> listaDxNeg = (List<Object[]>) model.get("listaDxNeg");
        List<Object[]> listaDxInadec = (List<Object[]>) model.get("listaDxInadec");

        List<String> columnas = (List<String>) model.get("columnas");
        boolean incluirMxInadecuadas = (boolean)model.get("incluirMxInadecuadas");
        boolean mostrarTabla1 = (boolean)model.get("mostrarTabla1");
        boolean mostrarTabla2 = (boolean)model.get("mostrarTabla2");
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
        // create data rows
        int rowCount = 4;
        int filaInicioNeg = 0;

        if (mostrarTabla1) {
            //tabla con dx positivos
            // create header row
            HSSFRow header = sheet.createRow(3);
            setHeaderTable(header, headerStyle, columnas);

            for (Object[] registro : listaDxPos) {
                HSSFRow aRow = sheet.createRow(rowCount++);
                setRowData(aRow, registro, contentCellStyle, dateCellStyle);
            }
            if (listaDxPos.size() <= 0) {
                HSSFRow aRow = sheet.createRow(rowCount++);
                sheet.addMergedRegion(new CellRangeAddress(aRow.getRowNum(), aRow.getRowNum(), 0, columnas.size() - 1));
                aRow.createCell(0).setCellValue(model.get("sinDatos").toString());
                aRow.getCell(0).setCellStyle(noDataCellStyle);
            }
        }

        if (mostrarTabla2) {
            //tabla con dx negativos
            rowCount += 2; // PARA DEJAR UNA FILA EN BLANCO ENTRE AMBAS TABLAS
            filaInicioNeg = rowCount++;
            HSSFRow headerPos = sheet.createRow(rowCount++);
            setHeaderTable(headerPos, headerStyle, columnas);
            for (Object[] registro : listaDxNeg) {
                HSSFRow aRow = sheet.createRow(rowCount++);
                setRowData(aRow, registro, contentCellStyle, dateCellStyle);
            }
            if (listaDxNeg.size() <= 0) {
                HSSFRow aRow = sheet.createRow(rowCount);
                sheet.addMergedRegion(new CellRangeAddress(aRow.getRowNum(), aRow.getRowNum(), 0, columnas.size() - 1));
                aRow.createCell(0).setCellValue(model.get("sinDatos").toString());
                aRow.getCell(0).setCellStyle(noDataCellStyle);
            }
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

        if (mostrarTabla1) {
            HSSFRow filtros = sheet.createRow(2);
            filtros.createCell(1).setCellValue(model.get("tablaPos").toString());
            filtros.getCell(1).setCellStyle(filterStyle);
        }

        if (mostrarTabla2) {
            HSSFRow filtrosNeg = sheet.createRow(filaInicioNeg);
            filtrosNeg.createCell(1).setCellValue(model.get("tablaNeg").toString());
            filtrosNeg.getCell(1).setCellStyle(filterStyle);
        }

        if (incluirMxInadecuadas ){
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

    public static void setRowData(HSSFRow aRow, Object[] registro, CellStyle contentCellStyle, CellStyle dateCellStyle){
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


    /**
     * Método para crear una celda y ponerle el valor que va a contener deacuerdo al tipo de dato
     * @param row Fila en la que se creará la celda
     * @param value Valor que se le asignará
     * @param posicion número de la columna en la fila (recordar que la primera celda tiene posición 0)
     * @param esFormula TRUE para indicar si la celda contendrá una fórmula
     * @param style Estilo que se le aplicará a la celda
     */
    public static void createCell(HSSFRow row, Object value, int posicion, boolean esFormula, CellStyle style){
        row.createCell(posicion);
        if (esFormula){
            row.getCell(posicion).setCellFormula(value.toString());
            row.getCell(posicion).setCellType(CellType.FORMULA);
        }else{
            if (value instanceof Integer){
                row.getCell(posicion).setCellValue((int)value);
                row.getCell(posicion).setCellType(CellType.NUMERIC);
            }else if (value instanceof Float){
                row.getCell(posicion).setCellValue((float)value);
                row.getCell(posicion).setCellType(CellType.NUMERIC);
            }else if (value instanceof Double){
                row.getCell(posicion).setCellValue((double)value);
                row.getCell(posicion).setCellType(CellType.NUMERIC);
            }
            else{
                row.createCell(posicion).setCellValue(value.toString());
                row.getCell(posicion).setCellType(CellType.STRING);
            }
        }
        row.getCell(posicion).setCellStyle(style);
    }

    /**
     * Método para crear en orientación horizonta un rango de celdas en una hoja y ponerle el valor que va a contener deacuerdo al tipo de dato. Sobre una misma fila
     * @param sheet Hoja en la que se creará el rango de celdas combinadas
     * @param row Fila en la que se creará la celda
     * @param value Valor que se le asignará
     * @param posicionInicio número de la columna en que iniciará la combinación de celdas (recordar que la primera celda tiene posición 0)
     * @param posicionFin número de la columna en que terminará la combinación de celdas
     * @param esFormula TRUE para indicar si la celda contendrá una fórmula
     * @param style Estilo que se le aplicará a cada celda dentro del rango
     */
    public static void createHorizontalCellRange(HSSFSheet sheet, HSSFRow row, Object value, int posicionInicio, int posicionFin, boolean esFormula, CellStyle style){
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), posicionInicio, posicionFin));
        createCell(row, value, posicionInicio, esFormula, style);
        //inicializando resto de celdas contenidas en el merge
        for (int i = posicionInicio+1; i <= posicionFin; i++){
            row.createCell(i);
            row.getCell(i).setCellStyle(style);
        }
    }

    /**
     * Método para crear en orientación vertical un rango de celdas en una hoja y ponerle el valor que va a contener deacuerdo al tipo de dato. Sobre una misma columna
     * @param sheet Hoja en la que se creará el rango de celdas combinadas
     * @param row Fila en la que se creará la celda
     * @param value Valor que se le asignará
     * @param posicionInicio número de la columna en que iniciará la combinación de celdas (recordar que la primera celda tiene posición 0)
     * @param posicionFin número de la columna en que terminará la combinación de celdas
     * @param columna columna sobre la que se aplicará la combinación
     * @param posicionValue posicion de la celda dentro del rango que va a contener el valor que se asignará
     * @param esFormula TRUE para indicar si la celda contendrá una fórmula
     * @param style Estilo que se le aplicará a cada celda dentro del rango
     */
    public static void createVerticalCellRange(HSSFSheet sheet, HSSFRow row, Object value, int posicionInicio, int posicionFin, int columna, int posicionValue, boolean esFormula, CellStyle style){
        sheet.addMergedRegion(new CellRangeAddress(posicionInicio, posicionFin, columna, columna));
        createCell(row, value, posicionValue, esFormula, style);
    }

}