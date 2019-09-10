package com.export.controller;

import com.export.Service.PDFExportService;
import com.export.entity.PDFRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Shiyx
 * @date 2019/9/9
 */

@RestController
@RequestMapping
public class PDFExportController {
    @Autowired
    private PDFExportService PDFexportService;


    @PostMapping("/pdfOut")
    public void pdfOut(@RequestBody PDFRequestEntity pdfRequestEntity, HttpServletResponse httpServletResponse){
        PDFexportService.pdfOut(pdfRequestEntity,httpServletResponse);
    }



    @GetMapping("/pdfSave")
    public void pdfSave(String htmlContent) throws Exception {
        PDFexportService.pdfSave(htmlContent);
    }








}
