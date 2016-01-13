package ni.gob.minsa.laboratorio.utilities.pdfUtils;

import ni.gob.minsa.laboratorio.domain.parametros.Imagen;
import ni.gob.minsa.laboratorio.service.ImagenesService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
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


}
