package com.export.config;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;



/**
 * @author Shiyx
 * @date 2019/9/9
 */
public class PDFBuilder extends PdfPageEventHelper {
    /**
     * 页眉
     */
    public String header = "";

    /**
     * 文档字体大小，页脚页眉最好和文本大小一致
     */
    public int presentFontSize = 12;

    public Float pageWidth = 595.0F;
    public Float pageHeigh = 800.0F;


    /**
     * 文档页面大小，最好前面传入，否则默认为A4纸张
     */
    public Rectangle pageSize = PageSize.A4;

    // 模板
    public PdfTemplate total;

    // 基础字体对象
    public BaseFont bf = null;

    // 利用基础字体生成的字体对象，一般用于生成中文文字
    public Font fontDetail = null;

    public PDFBuilder() {

    }


    public PDFBuilder(String yeMei, int presentFontSize, Rectangle pageSize) {
        this.header = yeMei;
        this.presentFontSize = presentFontSize;
        this.pageSize = pageSize;
    }



    @Override
    public void onEndPage(PdfWriter writer, Document document) {
       this.addPage(writer, document);
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        createHeadLine(document);
    }


    //加分页
    public void addPage(PdfWriter writer, Document document) {

        //设置分页页眉页脚字体
        try {
            if (bf == null) {
                bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", false);
            }
            if (fontDetail == null) {
                // 数据体字体
                fontDetail = new Font(bf, presentFontSize, Font.NORMAL);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 2.写入前半部分的 第 X页/共
        int pageS = writer.getPageNumber();
        String foot1 = "第 " + pageS + " 页 /共";
        Phrase footer = new Phrase(foot1, fontDetail);



        // 3.计算前半部分的foot1的长度，后面好定位最后一部分的'Y页'这俩字的x轴坐标，字体长度也要计算进去 = len
        float len = bf.getWidthPoint(foot1, presentFontSize);

        // 4.拿到当前的PdfContentByte
        PdfContentByte cb = writer.getDirectContent();

        // 5.写入页脚1，x轴就是(右margin+左margin + right() -left()- len)/2.0F
        // 再给偏移20F适合人类视觉感受，否则肉眼看上去就太偏左了
        // ,y轴就是底边界-20,否则就贴边重叠到数据体里了就不是页脚了；注意Y轴是从下往上累加的，最上方的Top值是大于Bottom好几百开外的。
        ColumnText
                .showTextAligned(
                        cb,
                        Element.ALIGN_CENTER,
                        footer,
                        (document.rightMargin() + document.right()
                                + document.leftMargin() - document.left() - len) / 2.0F + 20F,
                        document.bottom() - 20, 0);


        // 6.写入页脚2的模板（就是页脚的Y页这俩字）添加到文档中，计算模板的和Y轴,X=(右边界-左边界 - 前半部分的len值)/2.0F +
        // len ， y 轴和之前的保持一致，底边界-20
        // 调节模版显示的位置
        cb.addTemplate(total, (document.rightMargin() + document.right()
                        + document.leftMargin() - document.left()) / 2.0F + 20F,
                document.bottom() - 20);

    }


    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {

        // 共 页 的矩形的长宽高
        total = writer.getDirectContent().createTemplate(50, 50);
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        // 7.最后一步了，就是关闭文档的时候，将模板替换成实际的 Y 值,至此，page x of y 制作完毕，完美兼容各种文档size。
        total.beginText();
        // 生成的模版的字体、颜色
        total.setFontAndSize(bf, presentFontSize);
        //页脚内容拼接  如  第1页/共2页
        String foot2 = " " + (writer.getPageNumber()) + " 页";
        //页脚内容拼接  如  第1页/共2页        // 模版显示的内容
        total.showText(foot2);
        total.endText();
        total.closePath();
    }


    private void createFootLine(Document document) {

        try {
            float[] cellWidth = {0.10F,0.82F,0.08F};
            PdfPTable table = new PdfPTable(cellWidth);
            table.setWidthPercentage(100F);

            PdfPCell cellRight = new PdfPCell();

            File file = ResourceUtils.getFile("classpath:static");
            String baseUri = file.getPath();
            Image imageRight = Image.getInstance(baseUri + "/img/001.png");
            cellRight.setImage(imageRight);

            PdfPCell cellCenter = new PdfPCell();
            cellCenter.setPhrase(new Paragraph("Wind Information Co.,Ltd.",fontDetail));
            cellCenter.setPhrase(new Paragraph("Add:7/F,Wind Plaza,No.1500 Puming Road,Shanghai,China",fontDetail));
            cellCenter.setPhrase(new Paragraph("Tel:(8621)2070 0800",fontDetail));
            cellCenter.setPhrase(new Paragraph("Fax:(8621)2070 0888",fontDetail));
            cellCenter.setPhrase(new Paragraph("Email:sales@wind.com.cn",fontDetail));
            cellCenter.setPhrase(new Paragraph("Http://www.wind.com.cn",fontDetail));


            PdfPCell cellLeft = new PdfPCell();
            cellLeft.setPhrase(new Paragraph("1/2",fontDetail));



            table.addCell(cellRight);
            table.addCell(cellCenter);
            table.addCell(cellLeft);

            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置页眉图片
     * @param document
     */
    private void createHeadLine(Document document) {

        try {
            float[] cellWidth = {0.15F,0.55F,0.3F};
            PdfPTable table = new PdfPTable(cellWidth);
            table.setWidthPercentage(100F);

            PdfPCell cellRight = new PdfPCell();

            File file = ResourceUtils.getFile("classpath:static");
            String baseUri = file.getPath();
            Image imageRight = Image.getInstance(baseUri + "/img/002.png");
            cellRight.setImage(imageRight);
            cellRight.disableBorderSide(1);
            cellRight.disableBorderSide(12);

            PdfPCell cellCenter = new PdfPCell();
            cellCenter.disableBorderSide(1);
            cellCenter.disableBorderSide(12);

            PdfPCell cellLeft = new PdfPCell();
            Image imageLeft = Image.getInstance(baseUri + "/img/003.png");
            cellLeft.setImage(imageLeft);
            cellLeft.disableBorderSide(1);
            cellLeft.disableBorderSide(12);


            table.addCell(cellRight);
            table.addCell(cellCenter);
            table.addCell(cellLeft);

            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //加水印
    public void addWatermark(PdfWriter writer) {
        // 水印图片
        Image image;
        try {
            File file = ResourceUtils.getFile("classpath:static");
            String baseUri = file.getPath();
            image = Image.getInstance(baseUri + "/img/001.png");
            PdfContentByte content = writer.getDirectContentUnder();
            content.beginText();

            float width = image.getWidth();
            float height = image.getHeight();
            float absWidth = (pageWidth - width) / 2.0F;
            float absHeigh = (pageHeigh - height) / 2.0F;

            image.setAbsolutePosition(absWidth, absHeigh);
            content.addImage(image);

            content.endText();
        } catch (IOException | DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
