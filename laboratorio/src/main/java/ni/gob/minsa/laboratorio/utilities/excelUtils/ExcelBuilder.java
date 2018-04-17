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
        if (reporte.equalsIgnoreCase("DXEXAMS"))
            buildExcelDocumentDxExams(model, workbook);

	}

    public void buildExcelDocumentDxExams(Map<String, Object> model,
                                        HSSFWorkbook workbook) {
        List<String> columnas = (List<String>) model.get("columnas");
        List<String> meses = (List<String>) model.get("meses");
        List<String> dxs = (List<String>) model.get("dxs");
        Integer registrosPorTabla = (Integer) model.get("registrosPorTabla");
        Integer anioReporte = (Integer) model.get("anio");
        List<List<Object[]>> consolidados = (List<List<Object[]>>) model.get("consol");
        List<List<Object[]>> datos = (List<List<Object[]>>) model.get("datos");
        // create style for header cells
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeight((short)(11*20));
        font.setColor(HSSFColor.BLACK.index);
        headerStyle.setFont(font);
        //estilo para celda en el encabezado que dice  primerMes+"-"+ultimoMes+" "+anioReporte
        CellStyle headerStyle2 = workbook.createCellStyle();
        headerStyle2.setAlignment(HorizontalAlignment.CENTER);
        headerStyle2.setBorderBottom(BorderStyle.THIN);
        headerStyle2.setBorderTop(BorderStyle.THIN);
        headerStyle2.setBorderLeft(BorderStyle.THIN);
        headerStyle2.setBorderRight(BorderStyle.THIN);
        Font font2 = workbook.createFont();
        font2.setFontName("Arial");
        font2.setFontHeight((short) (10 * 20));
        font2.setColor(HSSFColor.BLACK.index);
        font2.setBold(true);
        headerStyle2.setFont(font2);

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
        dateCellStyle.setBorderBottom(BorderStyle.THIN);
        dateCellStyle.setBorderTop(BorderStyle.THIN);
        dateCellStyle.setBorderLeft(BorderStyle.THIN);
        dateCellStyle.setBorderRight(BorderStyle.THIN);
        dateCellStyle.setFont(font);
        //estilo para celdas de datos
        CellStyle contentCellStyle = workbook.createCellStyle();
        contentCellStyle.setBorderBottom(BorderStyle.THIN);
        contentCellStyle.setBorderTop(BorderStyle.THIN);
        contentCellStyle.setBorderLeft(BorderStyle.THIN);
        contentCellStyle.setBorderRight(BorderStyle.THIN);
        contentCellStyle.setFont(font);
        //estilo para celdas de totales en la última fila
        CellStyle totalCellStyle = workbook.createCellStyle();
        totalCellStyle.setAlignment(HorizontalAlignment.CENTER);
        totalCellStyle.setBorderBottom(BorderStyle.THIN);
        totalCellStyle.setBorderTop(BorderStyle.THIN);
        totalCellStyle.setBorderLeft(BorderStyle.THIN);
        totalCellStyle.setBorderRight(BorderStyle.THIN);
        totalCellStyle.setFont(font2);
        //estilo para celdas de % positividad
        CellStyle percentCellStyle = workbook.createCellStyle();
        percentCellStyle.setBorderBottom(BorderStyle.THIN);
        percentCellStyle.setBorderTop(BorderStyle.THIN);
        percentCellStyle.setBorderLeft(BorderStyle.THIN);
        percentCellStyle.setBorderRight(BorderStyle.THIN);
        percentCellStyle.setFont(font);
        percentCellStyle.setDataFormat(workbook.createDataFormat().getFormat("0"));

        int indiceRegistroTabla = 0;
        int indicePrimeraFila = 0, indiceUltimafila = 0;

        for(int indicedx = 0; indicedx < dxs.size(); indicedx++){
            // create a new Excel sheet
            HSSFSheet sheet = workbook.createSheet(dxs.get(indicedx)+ " Consolidado");
            int rowCount = 1;
            List<Object[]> consolidado = consolidados.get(indicedx);
            for (Object[] registro : consolidado) {
                if (registro.length > 1) {
                    indiceRegistroTabla++;
                    HSSFRow aRow = sheet.createRow(rowCount++);
                    setRowData(aRow, registro, contentCellStyle, dateCellStyle);
                    sheet.autoSizeColumn(0);
                    if(registrosPorTabla == indiceRegistroTabla){
                        indiceUltimafila = rowCount;
                        //poner celdad de totales
                        setRowTotalsConsol(sheet, contentCellStyle, totalCellStyle, percentCellStyle, rowCount++, meses.size() * 2, indicePrimeraFila, indiceUltimafila);
                    }
                } else {
                    // poner encabezado de tablas
                    if (rowCount > 1) rowCount += 2;
                    setHeaderTableConsolExams(sheet, headerStyle, headerStyle2, meses, rowCount, registro[0].toString(), anioReporte);
                    rowCount += 3;
                    indiceRegistroTabla = 0;
                    indicePrimeraFila = rowCount+1;
                }
            }
            // create a new Excel sheet
            HSSFSheet sheetDatos = workbook.createSheet(dxs.get(indicedx)+" Datos");
            int rowCountDat = 1;
            List<Object[]> dato = datos.get(indicedx);
            for (Object[] registro : dato) {
                if (registro.length > 1) {
                    indiceRegistroTabla++;
                    HSSFRow aRow = sheetDatos.createRow(rowCountDat++);
                    setRowData(aRow, registro, contentCellStyle, dateCellStyle);
                    sheetDatos.autoSizeColumn(0);
                    if(registrosPorTabla == indiceRegistroTabla){
                        indiceUltimafila = rowCountDat;
                        //poner celdas de totales
                        setRowTotalsDat(sheetDatos, contentCellStyle, totalCellStyle, rowCountDat++, columnas.size() * 2, indicePrimeraFila, indiceUltimafila);
                    }

                } else {
                    // poner encabezado de tablas
                    if (rowCountDat > 1) rowCountDat += 2;
                    setHeaderTableDatExams(sheetDatos, headerStyle, headerStyle2, columnas, meses, rowCountDat, registro[0].toString());
                    rowCountDat += 4;
                    indiceRegistroTabla = 0;
                    indicePrimeraFila = rowCountDat+1;
                }
            }
        }
    }

    /**
     * Método para poner los encabezados de las tablas de datos en la hoja de datos del respectivo dx
     */
    private void setHeaderTableDatExams(HSSFSheet sheet, CellStyle style, CellStyle style2, List<String> semanas, List<String> meses, int rowcount, String examen){
        int indiceSem = 1, indiceSubSem=1, indiceMes=0;

        //Se crea etiqueta "TOTAL DE (Examen)"
        HSSFRow headerExamen = sheet.createRow(rowcount);
        createCell(headerExamen, "TOTAL DE "+examen.toUpperCase(),0,false, style2);

        //Se crea encabezado de tabla dónde van los meses
        HSSFRow headerMeses = sheet.createRow(rowcount+1);
        //en la primera columna va la etiqueta "SILAIS"
        createVerticalCellRange(sheet, headerMeses, "SILAIS", headerMeses.getRowNum(), headerMeses.getRowNum()+2, 0, indiceMes, false, style);
        indiceMes++;
        int semanasMes =0;
        //luego se pone la etiqueta de cada mes
        for(String mes : meses){
            String[] partesMes = mes.split(",");
            semanasMes = Integer.valueOf(partesMes[1]);
            //para cada mes se unen todas las celdas (sheet.addMergedRegion) según la cantidad de semanas * 2 (esto porque la semana a su vez contiene T y P)
            int indiceCeldaFinMes = indiceMes+(semanasMes*2)-1;
            createHorizontalCellRange(sheet, headerMeses, getNombreMes(Integer.valueOf(partesMes[0])),indiceMes, indiceCeldaFinMes, false, style);
            //el siguiente mes, iniciará inmediatamente después de la última celda creada para el mes actual
            indiceMes=indiceCeldaFinMes+1;
        }
        //Se crea encabezado de tabla dónde van las semanas
        HSSFRow headerSemanas = sheet.createRow(rowcount+2);
        //Se crea encabezado de tabla dónde van las cantidades totales y de positivos
        HSSFRow subHeaderSemanas = sheet.createRow(rowcount+3);
        for(String columna : semanas){
            //cada semana contiene dos celdas (sheet.addMergedRegion), para poder crear bajo cada semana T y P
            createHorizontalCellRange(sheet, headerSemanas, Integer.valueOf(columna), indiceSem, indiceSem+1, false, style);
            //inicio de la siguiente semana
            indiceSem+=2;
            //Totales
            createCell(subHeaderSemanas, "T", indiceSubSem, false, style);
            indiceSubSem++;
            //Positivos
            createCell(subHeaderSemanas, "P", indiceSubSem, false, style);
            indiceSubSem++;
        }

    }

    /**
     * Método para totalizar cada columna de datos en la hoja de datos del respectivo dx
     */
    private void setRowTotalsDat(HSSFSheet sheet, CellStyle style, CellStyle styleTot, int rowCount, int totalColumnas, int indicePrimeraFila, int indiceUltimafila){
        HSSFRow aRowTot = sheet.createRow(rowCount);
        createCell(aRowTot, "Total", 0, false, styleTot);

        for(int i = 1; i <= totalColumnas ; i++){
            String columnLetter = CellReference.convertNumToColString(i);
            String formula = "SUM("+columnLetter+indicePrimeraFila+":"+columnLetter+indiceUltimafila+")";
            createCell(aRowTot, formula, i, true, style);
        }
    }

    /**
     * Método para poner los encabezados de las tablas de consolidades en la hoja de consolidado del respectivo dx
     */
    private void setHeaderTableConsolExams(HSSFSheet sheet, CellStyle style, CellStyle style2, List<String> meses, int rowcount, String examen, int anioReporte){
        int indiceSubMes=1, indiceColumnaMes=1;

        //Se crea etiqueta "TOTAL DE (Examen)"
        HSSFRow headerExamen = sheet.createRow(rowcount);
        //en la primera columna va la etiqueta "SILAIS"
        createVerticalCellRange(sheet, headerExamen, "SILAIS", headerExamen.getRowNum(), headerExamen.getRowNum()+2, 0, 0, false, style2);
        //Se crea encabezado de tabla dónde van los meses
        HSSFRow headerMeses = sheet.createRow(rowcount+1);
        HSSFRow subHeadermeses = sheet.createRow(rowcount+2);

        //luego se pone la etiqueta de cada mes
        String primerMes = "";
        String ultimoMes = "";
        int indiceMes = 0;
        for(String mes : meses){
            String[] partesMes = mes.split(",");
            //para cada mes se unen 2 celdas (sheet.addMergedRegion)  (esto porque luego cada mes contiene T y P)
            createHorizontalCellRange(sheet, headerMeses, getNombreMes(Integer.valueOf(partesMes[0])).toUpperCase(), indiceColumnaMes, indiceColumnaMes+1, false, style2);
            //Totales
            createCell(subHeadermeses, "T", indiceSubMes, false, style2);
            indiceSubMes++;
            //Positivos
            createCell(subHeadermeses, "P", indiceSubMes, false, style2);
            indiceSubMes++;
            //el siguiente mes, iniciará inmediatamente después de la última celda creada para el mes actual
            indiceColumnaMes+=2;

            if (indiceMes == 0) primerMes = getNombreMes(Integer.valueOf(partesMes[0]));
            if (indiceMes == meses.size()-1) ultimoMes = getNombreMes(Integer.valueOf(partesMes[0]));
            indiceMes++;
        }
        //combinar todas las celdas de arriba de los meses
        createHorizontalCellRange(sheet, headerExamen, "", 1, indiceColumnaMes-1, false, style2);
        //en la primera fila poner hasta el final la etiqueta "Total (Examen)"
        createHorizontalCellRange(sheet, headerExamen, "Total "+examen, indiceColumnaMes, indiceColumnaMes+2, false, style2);
        createHorizontalCellRange(sheet, headerMeses, primerMes+"-"+ultimoMes+" "+anioReporte, indiceColumnaMes, indiceColumnaMes+2, false, style );
        createCell(subHeadermeses, "T", indiceSubMes, false, style2);
        createCell(subHeadermeses, "P", indiceSubMes+1, false, style2);
        createCell(subHeadermeses, "%", indiceSubMes+2, false, style2);

    }

    /**
     * Método para totalizar cada columna y fila de consolidado en la hoja de consolidado del respectivo dx
     */
    private void setRowTotalsConsol(HSSFSheet sheet, CellStyle style, CellStyle styleTot, CellStyle percentCellStyle, int rowCount, int totalColumnas, int indicePrimeraFila, int indiceUltimafila){
        HSSFRow aRowTot = sheet.createRow(rowCount);
        createCell(aRowTot, "Total", 0, false, styleTot);

        for (int i = indicePrimeraFila; i <= indiceUltimafila; i++){
            HSSFRow row = sheet.getRow(i-1);
            //aplicar fórmula para sumar los totales de cada mes
            String formulaTotales = "SUM(";
            for(int j = 1; j <= totalColumnas ; j+=2){
                String columnLetter = CellReference.convertNumToColString(row.getCell(j).getColumnIndex());
                formulaTotales += (j==1?"":",")+columnLetter+i;
            }
            formulaTotales += ")";
            //poner la sumatoria de los totales
            createCell(row, formulaTotales, totalColumnas+1, true, style);
            //aplicar fórmula para sumar los positivos de cada mes
            String formulaPos = "SUM(";
            for(int j = 2; j <= totalColumnas ; j+=2){
                String columnLetter = CellReference.convertNumToColString(row.getCell(j).getColumnIndex());
                formulaPos += (j==1?"":",")+columnLetter+i;
            }
            formulaPos += ")";
            //poner la sumatoria de los positivos
            createCell(row, formulaPos, totalColumnas+2, true, style);
            //aplicar fórmula del porcentaje de positividad
            String columnLetterTot = CellReference.convertNumToColString(row.getCell(totalColumnas+1).getColumnIndex());
            String columnLetterPos = CellReference.convertNumToColString(row.getCell(totalColumnas+2).getColumnIndex());
            String formularPorcen = "("+columnLetterPos+(row.getRowNum()+1)+"/"+columnLetterTot+(row.getRowNum()+1)+")*100";
            //poner porcentaje de positividad
            createCell(row, formularPorcen, totalColumnas+3, true, percentCellStyle);

        }

        for(int i = 1; i <= totalColumnas+2 ; i++){
            //aplicar formula de suma para todas las columnas de totales y positivos
            String columnLetter = CellReference.convertNumToColString(i);
            String formula = "SUM("+columnLetter+indicePrimeraFila+":"+columnLetter+indiceUltimafila+")";
            createCell(aRowTot, formula, i, true, style);
        }

        String columnLetterTot = CellReference.convertNumToColString(aRowTot.getCell(totalColumnas+1).getColumnIndex());
        String columnLetterPos = CellReference.convertNumToColString(aRowTot.getCell(totalColumnas+2).getColumnIndex());
        String formularPorcen = "("+columnLetterPos+(aRowTot.getRowNum()+1)+"/"+columnLetterTot+(aRowTot.getRowNum()+1)+")*100";
        //poner porcentaje de positividad total
        createCell(aRowTot, formularPorcen, totalColumnas+3, true, percentCellStyle);

    }

    public String getNombreMes(int mes){
        switch (mes){
            case 1: return "Enero";
            case 2: return "Febrero";
            case 3: return "Marzo";
            case 4: return "Abril";
            case 5: return "Mayo";
            case 6: return "Junio";
            case 7: return "Julio";
            case 8: return "Agosto";
            case 9: return "Septiembre";
            case 10: return "Octubre";
            case 11: return "Noviembre";
            case 12: return "Diciembre";
            default: return "-";
        }
    }

    public void buildExcelDocumentVigDx(Map<String, Object> model, HSSFWorkbook workbook){
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


    /**
     * Método para crear una celda y ponerle el valor que va a contener deacuerdo al tipo de dato
     * @param row Fila en la que se creará la celda
     * @param value Valor que se le asignará
     * @param posicion número de la columna en la fila (recordar que la primera celda tiene posición 0)
     * @param esFormula TRUE para indicar si la celda contendrá una fórmula
     * @param style Estilo que se le aplicará a la celda
     */
    private void createCell(HSSFRow row, Object value, int posicion, boolean esFormula, CellStyle style){
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
    private void createHorizontalCellRange(HSSFSheet sheet, HSSFRow row, Object value, int posicionInicio, int posicionFin, boolean esFormula, CellStyle style){
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
    private void createVerticalCellRange(HSSFSheet sheet, HSSFRow row, Object value, int posicionInicio, int posicionFin, int columna, int posicionValue, boolean esFormula, CellStyle style){
        sheet.addMergedRegion(new CellRangeAddress(posicionInicio, posicionFin, columna, columna));
        createCell(row, value, posicionValue, esFormula, style);
    }

}