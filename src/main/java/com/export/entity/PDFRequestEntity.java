package com.export.entity;

/**
 * @author Shiyx
 * @date 2019/9/9
 */
public class PDFRequestEntity {
    private Long companyId;
    private String htmlContent;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}
