package ni.gob.minsa.laboratorio.utilities.pdfUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by FIRSTICT on 4/21/2015.
 * V1.0
 */
public class GeneralUtils {


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
        String workingDir = System.getProperty("user.dir");

        //dibujar encabezado
        BufferedImage headerImage = ImageIO.read(new File(workingDir + "/encabezadoMinsa.jpg"));
        GeneralUtils.drawObject(stream, doc, headerImage, 5, inY,wHeader, hHeader);


        //dibujar pie de pag
        BufferedImage footerImage = ImageIO.read(new File(workingDir + "/piePMinsa.jpg"));
        GeneralUtils.drawObject(stream, doc, footerImage, 5, 20, wFooter, hFooter);
    }


}
