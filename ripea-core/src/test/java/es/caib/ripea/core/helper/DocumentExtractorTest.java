package es.caib.ripea.core.helper;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DocumentExtractorTest {

    private final static String EXPECTED_TEXT = "Crear un servei de resum amb Bert Extractive Summarizer";

    @Before
    public void setUp() { }

    @Test
    public void testExtractTextFromPDF() throws IOException {
        String pdfContentType = "application/pdf";
        Path pdfPath = Paths.get("./src/test/resources/docs/doc.pdf");
        byte[] pdfBytes = Files.readAllBytes(pdfPath);
        String actualText = PluginHelper.extractTextFromDocument(pdfBytes, pdfContentType);

        System.out.println(actualText);
        assertTrue(actualText.startsWith(EXPECTED_TEXT));
    }

    @Test
    public void testExtractTextFromDocx() throws IOException {
        String docxContentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        Path docxPath = Paths.get("./src/test/resources/docs/doc.docx");
        byte[] docxBytes = Files.readAllBytes(docxPath);
        String actualText = PluginHelper.extractTextFromDocument(docxBytes, docxContentType);

        assertTrue(actualText.startsWith(EXPECTED_TEXT));
    }

    @Test
    public void testExtractTextFromOdt() throws IOException {
        String odtContentType = "application/vnd.oasis.opendocument.text";
        Path odtPath = Paths.get("./src/test/resources/docs/doc.odt");
        byte[] odtBytes = Files.readAllBytes(odtPath);
        String actualText = PluginHelper.extractTextFromDocument(odtBytes, odtContentType);

        assertTrue(actualText.startsWith(EXPECTED_TEXT));
    }

    @Test
    public void testUnsupportedContentType() {
        byte[] unsupportedBytes = new byte[]{/* dades simulades */};
        String unsupportedContentType = "application/unknown";

        String actualText = PluginHelper.extractTextFromDocument(unsupportedBytes, unsupportedContentType);

        assertNull(actualText);
    }
}