package vn.nuce.datn_be.controller;

import org.apache.catalina.connector.Response;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.nuce.datn_be.services.ScheduledTasks;
import vn.nuce.datn_be.utils.ExcelUtils;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Controller
@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RequestMapping("/resources")
public class ResourcesController {

    @Autowired
    ScheduledTasks scheduledTasks;

    @GetMapping(value = "/template-candidate-list")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> getTemplateCandidateList(HttpServletResponse httpServletResponse) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ProductTemplate.xlsx");
        try {
            return new ResponseEntity<>(new ByteArrayResource(ExcelUtils.class.getResourceAsStream("/template/CandidateTemp.xlsx").readAllBytes()),
                    header, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @GetMapping(value = "/send-mail")
//    public ResponseEntity<?> sendMail(){
//        scheduledTasks.autoSendMailToCandidate();
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
