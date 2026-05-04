package pdfer.templates;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import pdfer.template.AbstractJsonPdfTemplate;
import pdfer.template.PdfTemplateComponent;
import pdfer.template.exceptions.PdfTemplateException;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Default template that just prints the payload map into a table of key-values.
 */
@PdfTemplateComponent(name = "default")
public class DefaultTemplate extends AbstractJsonPdfTemplate {

    private byte[] content;

    @Override
    @SuppressWarnings("unchecked")
    public Class<Map<String, Object>> getPayloadClass() {
        return (Class<Map<String, Object>>) (Class<?>) Map.class;
    }

    @Override
    public boolean validatePayload() {
        return getPayload() != null && !getPayload().isEmpty();
    }

    @Override
    public void generate() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            Document layoutDocument = new Document(PageSize.A4, 25f, 25f, 25f, 25f);

            // Creates fonts.
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE);
            Font keyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);

            // Adds title.
            Paragraph title = new Paragraph("Cool PDF Document", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);

            // Creates table.
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 40f, 60f });

            // Header cells with styling.
            PdfPCell keyCell = new PdfPCell(new Phrase("KEY", headerFont));
            keyCell.setBackgroundColor(BaseColor.BLUE);
            keyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            keyCell.setPadding(10);
            table.addCell(keyCell);

            PdfPCell valueCell = new PdfPCell(new Phrase("VALUE", headerFont));
            valueCell.setBackgroundColor(BaseColor.BLUE);
            valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            valueCell.setPadding(10);
            table.addCell(valueCell);

            // Data rows with alternating colors.
            boolean alternate = false;
            for (Map.Entry<String, Object> pair : getPayload().entrySet()) {
                BaseColor rowColor = alternate ? BaseColor.LIGHT_GRAY : BaseColor.WHITE;

                PdfPCell kCell = new PdfPCell(new Paragraph(pair.getKey(), keyFont));
                kCell.setBackgroundColor(rowColor);
                kCell.setPadding(8);
                table.addCell(kCell);

                PdfPCell vCell = new PdfPCell(new Paragraph(pair.getValue().toString(), valueFont));
                vCell.setBackgroundColor(rowColor);
                vCell.setPadding(8);
                table.addCell(vCell);

                alternate = !alternate;
            }

            // Writes to output stream.
            PdfWriter.getInstance(layoutDocument, os);

            layoutDocument.open();
            layoutDocument.add(title);
            layoutDocument.add(new Paragraph(" "));
            layoutDocument.add(table);
            layoutDocument.close();

        } catch (DocumentException e) {
            throw new PdfTemplateException(e);
        }
        this.content = os.toByteArray();
    }

    @Override
    public byte[] getContent() {
        return content;
    }
}
