package ni.gob.minsa.laboratorio.utilities.pdfUtils;

import com.sun.javafx.iio.jpeg.JPEGImageLoaderFactory;
import ni.gob.minsa.laboratorio.domain.parametros.Imagen;
import ni.gob.minsa.laboratorio.service.ImagenesService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by FIRSTICT on 4/21/2015.
 * V1.0
 */
@Component
public class GeneralUtils {

    private static ImagenesService imagenesService;

    @Autowired
    private ImagenesService tImagenesService;

    @PostConstruct
    public void init() {
        GeneralUtils.imagenesService = tImagenesService;
    }

    public static PDPage addNewPage(PDDocument doc) {
        PDPage page = new PDPage();
        page.setMediaBox(PDPage.PAGE_SIZE_A4);
        doc.addPage(page);
        return page;
    }

    public static float centerTextPositionX(PDPage page, PDFont font, float fontSize, String texto) throws IOException {
        float titleWidth = font.getStringWidth(texto) / 1000 * fontSize;
        return (page.getMediaBox().getWidth() - titleWidth) / 2;
    }

    public static float centerTextPositionXHorizontal(PDPage page, PDFont font, float fontSize, String texto) throws IOException {
        float titleWidth = font.getStringWidth(texto) / 1000 * fontSize;
        return (page.getMediaBox().getHeight() - titleWidth) / 2;
    }

    public static void drawTEXT(String texto, float inY, float inX, PDPageContentStream stream, float textSize, PDFont textStyle) throws IOException {
        stream.beginText();
        stream.setFont(textStyle, textSize);
        stream.moveTextPositionByAmount(inX, inY);
        stream.drawString(texto);
        stream.endText();
    }


    public static void drawObject(PDPageContentStream stream, PDDocument doc, BufferedImage image, float x, float y, float width, float height) throws IOException {
        BufferedImage awtImage = image;
        PDXObjectImage ximage = new PDPixelMap(doc, awtImage);
        stream.drawXObject(ximage, x, y, width, height);
        }

    public static void drawHeaderAndFooter(PDPageContentStream stream, PDDocument doc, float inY, float wHeader, float hHeader, float wFooter, float hFooter) throws IOException {
        Imagen imagen = imagenesService.getImagenByName("HEADER_REPORTES");
        InputStream inputStream = new ByteArrayInputStream(imagen.getBytes());
        //dibujar encabezado
        BufferedImage headerImage = ImageIO.read(inputStream);
        GeneralUtils.drawObject(stream, doc, headerImage, 5, inY,wHeader, hHeader);

        //dibujar pie de pag
        imagen = imagenesService.getImagenByName("FOOTER_REPORTES");
        inputStream = new ByteArrayInputStream(imagen.getBytes());
        BufferedImage footerImage = ImageIO.read(inputStream);
        GeneralUtils.drawObject(stream, doc, footerImage, 5, 20, wFooter, hFooter);
    }

    public static void addBarcode128(PDPageContentStream stream, PDDocument document, String text, float x, float y) {
        try {
            int dpi = 400;
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            Code128Bean code128Bean = new Code128Bean();
            code128Bean.setFontSize(4);
            code128Bean.setBarHeight(10);
            code128Bean.generateBarcode(canvas, text.trim());
            canvas.finish();
            BufferedImage bImage = canvas.getBufferedImage();
            drawObject(stream, document, bImage, x, y, 200, 40);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addBarcode39(PDPageContentStream stream, PDDocument document, String text, float x, float y) {
        try {
            int dpi = 300;
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            Code39Bean code39Bean = new Code39Bean();
            code39Bean.generateBarcode(canvas, text.trim());
            canvas.finish();
            BufferedImage bImage = canvas.getBufferedImage();
            drawObject(stream, document, bImage, x, y, 190, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addLink(PDDocument document, PDPage page, String texto, float startY, float startX, float fontSize, PDFont font) {
        PDAnnotationLink txtLink = new PDAnnotationLink();
        try {
            float textWidth = font.getStringWidth(texto) / 1000 * fontSize;

            float startLinkY = startY + fontSize;

            PDRectangle position = new PDRectangle();
            position.setLowerLeftX(startX);
            position.setLowerLeftY(startLinkY);
            position.setUpperRightX(startX + textWidth);
            position.setUpperRightY(startY);
            txtLink.setRectangle(position);
            txtLink.setPrinted(false);
            txtLink.setInvisible(true);
            PDActionURI action = new PDActionURI();
            action.setURI(texto);
            txtLink.setAction(action);

            page.getAnnotations().add(txtLink);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
