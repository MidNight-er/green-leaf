package ru.greenleaf.template;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HtmlView extends AbstractTemplateView {

    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    public HtmlView() {
        setContentType(CONTENT_TYPE);
    }

    @Override
    protected boolean generatesDownloadContent() {
        return false;
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Read the template
        InputStream inputStream = getServletContext().getResourceAsStream(getUrl());
        String template = copyToString(inputStream, UTF_8);
        // Render the template
        CompiledTemplate compiledTemplate = TemplateCompiler.compileTemplate(template);
        template = (String) (model.isEmpty() ? TemplateRuntime.execute(compiledTemplate) : TemplateRuntime.execute(compiledTemplate, model));
        copy(template, UTF_8, response);
    }

    @Override
    public boolean checkResource(Locale locale) {
        return getApplicationContext().getResource(getUrl()).exists();
    }

    private static String copyToString(InputStream inputStream, Charset charset) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toString(charset.name());
        }
    }

    private static void copy(String in, Charset charset, HttpServletResponse response) throws IOException {
        try (OutputStream outputStream = response.getOutputStream();
             InputStream inputStream = new ByteArrayInputStream(in.getBytes(charset))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
    }
}
