package ni.gob.minsa.laboratorio.utilities.pdfUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;

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
}
