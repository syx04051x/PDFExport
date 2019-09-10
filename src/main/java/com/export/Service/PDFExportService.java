package com.export.Service;

import com.export.entity.PDFRequestEntity;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Shiyx
 * @date 2019/9/9
 */
public interface PDFExportService {

    public void pdfOut(PDFRequestEntity pdfRequestEntity, HttpServletResponse httpServletResponse);


    public String pdfSave(String htmlContent) throws Exception;


}
