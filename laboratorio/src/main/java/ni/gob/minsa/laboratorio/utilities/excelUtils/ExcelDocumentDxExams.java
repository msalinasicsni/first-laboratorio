package ni.gob.minsa.laboratorio.utilities.excelUtils;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Miguel Salinas on 12/06/2018.
 * V1.0
 */
public class ExcelDocumentDxExams{

    Map<String, Object> model;
    HSSFWorkbook workbook;

    public ExcelDocumentDxExams(Map<String, Object> model,
            HSSFWorkbook workbook){
        this.model = model;
        this.workbook = workbook;
    }

    public void buildExcel() {
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
            String nombreHoja = dxs.get(indicedx).replaceAll(" ","");
            if (nombreHoja.length()>24) {
                nombreHoja = nombreHoja.substring(0, 24) + " Consol";
            }else{
                nombreHoja = nombreHoja+ " Consolidado";
            }
            HSSFSheet sheet = workbook.createSheet(nombreHoja);
            int rowCount = 1;
            List<Object[]> consolidado = consolidados.get(indicedx);
            for (Object[] registro : consolidado) {
                if (registro.length > 1) {
                    indiceRegistroTabla++;
                    HSSFRow aRow = sheet.createRow(rowCount++);
                    ExcelBuilder.setRowData(aRow, registro, contentCellStyle, dateCellStyle);
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
            nombreHoja = dxs.get(indicedx).replaceAll(" ","");
            if (nombreHoja.length()>25) nombreHoja = nombreHoja.substring(0,25);
            HSSFSheet sheetDatos = workbook.createSheet(nombreHoja+" Datos");
            int rowCountDat = 1;
            List<Object[]> dato = datos.get(indicedx);
            for (Object[] registro : dato) {
                if (registro.length > 1) {
                    indiceRegistroTabla++;
                    HSSFRow aRow = sheetDatos.createRow(rowCountDat++);
                    ExcelBuilder.setRowData(aRow, registro, contentCellStyle, dateCellStyle);
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
    protected void setHeaderTableDatExams(HSSFSheet sheet, CellStyle style, CellStyle style2, List<String> semanas, List<String> meses, int rowcount, String examen){
        int indiceSem = 1, indiceSubSem=1, indiceMes=0;

        //Se crea etiqueta "TOTAL DE (Examen)"
        HSSFRow headerExamen = sheet.createRow(rowcount);
        ExcelBuilder.createCell(headerExamen, "TOTAL DE "+examen.toUpperCase(),0,false, style2);

        //Se crea encabezado de tabla dónde van los meses
        HSSFRow headerMeses = sheet.createRow(rowcount+1);
        //en la primera columna va la etiqueta "SILAIS"
        ExcelBuilder.createVerticalCellRange(sheet, headerMeses, "SILAIS", headerMeses.getRowNum(), headerMeses.getRowNum()+2, 0, indiceMes, false, style);
        indiceMes++;
        int semanasMes =0;
        //luego se pone la etiqueta de cada mes
        for(String mes : meses){
            String[] partesMes = mes.split(",");
            semanasMes = Integer.valueOf(partesMes[1]);
            //para cada mes se unen todas las celdas (sheet.addMergedRegion) según la cantidad de semanas * 2 (esto porque la semana a su vez contiene T y P)
            int indiceCeldaFinMes = indiceMes+(semanasMes*2)-1;
            ExcelBuilder.createHorizontalCellRange(sheet, headerMeses, getNombreMes(Integer.valueOf(partesMes[0])),indiceMes, indiceCeldaFinMes, false, style);
            //el siguiente mes, iniciará inmediatamente después de la última celda creada para el mes actual
            indiceMes=indiceCeldaFinMes+1;
        }
        //Se crea encabezado de tabla dónde van las semanas
        HSSFRow headerSemanas = sheet.createRow(rowcount+2);
        //Se crea encabezado de tabla dónde van las cantidades totales y de positivos
        HSSFRow subHeaderSemanas = sheet.createRow(rowcount+3);
        for(String columna : semanas){
            //cada semana contiene dos celdas (sheet.addMergedRegion), para poder crear bajo cada semana T y P
            ExcelBuilder.createHorizontalCellRange(sheet, headerSemanas, Integer.valueOf(columna), indiceSem, indiceSem+1, false, style);
            //inicio de la siguiente semana
            indiceSem+=2;
            //Totales
            ExcelBuilder.createCell(subHeaderSemanas, "T", indiceSubSem, false, style);
            indiceSubSem++;
            //Positivos
            ExcelBuilder.createCell(subHeaderSemanas, "P", indiceSubSem, false, style);
            indiceSubSem++;
        }

    }

    /**
     * Método para totalizar cada columna de datos en la hoja de datos del respectivo dx
     */
    private void setRowTotalsDat(HSSFSheet sheet, CellStyle style, CellStyle styleTot, int rowCount, int totalColumnas, int indicePrimeraFila, int indiceUltimafila){
        HSSFRow aRowTot = sheet.createRow(rowCount);
        ExcelBuilder.createCell(aRowTot, "Total", 0, false, styleTot);

        for(int i = 1; i <= totalColumnas ; i++){
            String columnLetter = CellReference.convertNumToColString(i);
            String formula = "SUM("+columnLetter+indicePrimeraFila+":"+columnLetter+indiceUltimafila+")";
            ExcelBuilder.createCell(aRowTot, formula, i, true, style);
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
        ExcelBuilder.createVerticalCellRange(sheet, headerExamen, "SILAIS", headerExamen.getRowNum(), headerExamen.getRowNum()+2, 0, 0, false, style2);
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
            ExcelBuilder.createHorizontalCellRange(sheet, headerMeses, getNombreMes(Integer.valueOf(partesMes[0])).toUpperCase(), indiceColumnaMes, indiceColumnaMes+1, false, style2);
            //Totales
            ExcelBuilder.createCell(subHeadermeses, "T", indiceSubMes, false, style2);
            indiceSubMes++;
            //Positivos
            ExcelBuilder.createCell(subHeadermeses, "P", indiceSubMes, false, style2);
            indiceSubMes++;
            //el siguiente mes, iniciará inmediatamente después de la última celda creada para el mes actual
            indiceColumnaMes+=2;

            if (indiceMes == 0) primerMes = getNombreMes(Integer.valueOf(partesMes[0]));
            if (indiceMes == meses.size()-1) ultimoMes = getNombreMes(Integer.valueOf(partesMes[0]));
            indiceMes++;
        }
        //combinar todas las celdas de arriba de los meses
        ExcelBuilder.createHorizontalCellRange(sheet, headerExamen, "", 1, indiceColumnaMes-1, false, style2);
        //en la primera fila poner hasta el final la etiqueta "Total (Examen)"
        ExcelBuilder.createHorizontalCellRange(sheet, headerExamen, "Total "+examen, indiceColumnaMes, indiceColumnaMes+2, false, style2);
        ExcelBuilder.createHorizontalCellRange(sheet, headerMeses, primerMes+"-"+ultimoMes+" "+anioReporte, indiceColumnaMes, indiceColumnaMes+2, false, style );
        ExcelBuilder.createCell(subHeadermeses, "T", indiceSubMes, false, style2);
        ExcelBuilder.createCell(subHeadermeses, "P", indiceSubMes+1, false, style2);
        ExcelBuilder.createCell(subHeadermeses, "%", indiceSubMes+2, false, style2);

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

    /**
     * Método para totalizar cada columna y fila de consolidado en la hoja de consolidado del respectivo dx
     */
    private void setRowTotalsConsol(HSSFSheet sheet, CellStyle style, CellStyle styleTot, CellStyle percentCellStyle, int rowCount, int totalColumnas, int indicePrimeraFila, int indiceUltimafila){
        HSSFRow aRowTot = sheet.createRow(rowCount);
        ExcelBuilder.createCell(aRowTot, "Total", 0, false, styleTot);

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
            ExcelBuilder.createCell(row, formulaTotales, totalColumnas+1, true, style);
            //aplicar fórmula para sumar los positivos de cada mes
            String formulaPos = "SUM(";
            for(int j = 2; j <= totalColumnas ; j+=2){
                String columnLetter = CellReference.convertNumToColString(row.getCell(j).getColumnIndex());
                formulaPos += (j==1?"":",")+columnLetter+i;
            }
            formulaPos += ")";
            //poner la sumatoria de los positivos
            ExcelBuilder.createCell(row, formulaPos, totalColumnas+2, true, style);
            //aplicar fórmula del porcentaje de positividad
            String columnLetterTot = CellReference.convertNumToColString(row.getCell(totalColumnas+1).getColumnIndex());
            String columnLetterPos = CellReference.convertNumToColString(row.getCell(totalColumnas+2).getColumnIndex());
            String formularPorcen = "("+columnLetterPos+(row.getRowNum()+1)+"/"+columnLetterTot+(row.getRowNum()+1)+")*100";
            //poner porcentaje de positividad
            ExcelBuilder.createCell(row, formularPorcen, totalColumnas+3, true, percentCellStyle);

        }

        for(int i = 1; i <= totalColumnas+2 ; i++){
            //aplicar formula de suma para todas las columnas de totales y positivos
            String columnLetter = CellReference.convertNumToColString(i);
            String formula = "SUM("+columnLetter+indicePrimeraFila+":"+columnLetter+indiceUltimafila+")";
            ExcelBuilder.createCell(aRowTot, formula, i, true, style);
        }

        String columnLetterTot = CellReference.convertNumToColString(aRowTot.getCell(totalColumnas+1).getColumnIndex());
        String columnLetterPos = CellReference.convertNumToColString(aRowTot.getCell(totalColumnas+2).getColumnIndex());
        String formularPorcen = "("+columnLetterPos+(aRowTot.getRowNum()+1)+"/"+columnLetterTot+(aRowTot.getRowNum()+1)+")*100";
        //poner porcentaje de positividad total
        ExcelBuilder.createCell(aRowTot, formularPorcen, totalColumnas+3, true, percentCellStyle);

    }

}
