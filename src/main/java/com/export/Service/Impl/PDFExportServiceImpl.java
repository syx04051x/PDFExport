package com.export.Service.Impl;

import com.export.Service.PDFExportService;
import com.export.config.PDFBuilder;
import com.export.entity.PDFRequestEntity;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shiyx
 * @date 2019/9/9
 */
@Service
public class PDFExportServiceImpl implements PDFExportService {


    private Configuration configuration;

    @Override
    public void pdfOut(PDFRequestEntity pdfRequestEntity, HttpServletResponse httpServletResponse) {
        String htmlContent = pdfRequestEntity.getHtmlContent();
        //获取公司id
        Long companyId = pdfRequestEntity.getCompanyId();

        //查找公司基本信息
        //TODO
        String companyName = companyNameInfo(companyId);
        companyName = "昆山沪光汽车电器股份有限公司";

        //生成pdf
        String path = pdfSave(htmlContent);

        //下载pdf
        downloadPdfFile(path, companyName, httpServletResponse);


    }

    @Override
    public String pdfSave(String htmlContent) {
        //1.建立一个A4的document
        Document document = new Document(PageSize.A4);


        //2.设置输出的路径并创建File文件
        String property = System.getProperty("user.dir");
        String filename = String.valueOf(System.currentTimeMillis());
        String temp1 = property + "\\" + filename + ".pdf";
        File pdfFile = new File(temp1);

        try {

            //3.将文件转成FileOutputStream流文件
            FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);

            //4.将流文件定义为PdfWriter格式
            PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);

            //5.添加分页
            PDFBuilder builder = new PDFBuilder();

            writer.setPageEvent(builder);

            document.open();

            XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(htmlContent));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            document.close();
        }

        return temp1;
    }



    /**
     * 下载pdf文件
     *
     * @param path
     * @param resp
     */
    private void downloadPdfFile(String path, String companyName, HttpServletResponse resp) {

        InputStream fin = null;
        ServletOutputStream out = null;

        File file1 = new File(path);

        try {


            fin = new FileInputStream(file1);
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/pdf");

            String filename = companyName + "年度报告.pdf";
            // 设置浏览器以下载的方式处理该文件
            filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");

            resp.setHeader("Content-Disposition", "attachment;filename=" + filename);

            out = resp.getOutputStream();
            // 缓冲区
            byte[] buffer = new byte[512];
            int bytesToRead = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((bytesToRead = fin.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
                if (out != null) {
                    out.close();
                }
                if (path != null) {
                    file1.delete(); // 删除临时文件
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("export file finish");
    }


    /**
     * 根据公司id查找公司基本信息
     *
     * @param companyId
     * @return
     */
    private String companyNameInfo(Long companyId) {
        return null;
    }


    public void pdfOut1() {

        configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");


        //根据公司id获取公司名称
        String companyName = "昆山沪光汽车电器股份有限公司";

        Map<String, String> dataMap = new HashMap<>();
//        dataMap.put("companyname",companyName);
//        dataMap.put("content","江苏省徐州市鼓楼区之战神归来！江苏省徐州市鼓楼区之战神归来！江苏省徐州市鼓楼区之战神归来！江苏省徐州市鼓楼区之战神归来！");


        //获取首页的模板
        //FTL文件所存在的位置
        configuration.setClassForTemplateLoading(this.getClass(), "/static/temp");
        Template t = null;
        try {
            //首页文件名
            t = configuration.getTemplate("index.html");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //设置输出的文件信息
        String property = System.getProperty("user.dir");
        String filename = String.valueOf(System.currentTimeMillis());
        File outFile = new File(property + "\\" + filename + ".pdf");

        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }


        try {
            t.process(dataMap, out);
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {


        PDFExportServiceImpl pdfExportService = new PDFExportServiceImpl();
        pdfExportService.pdfOut1();


    }
}
