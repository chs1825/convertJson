package com.convert.json.controller;

import com.convert.json.service.JsonService;
import com.convert.json.vo.InfoAboutJsonVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
@Controller
@RequestMapping("/")
public class JsonController {

    private final JsonService jsonService;

    public JsonController(JsonService jsonService) {
        this.jsonService = jsonService;
    }

    @RequestMapping("/")
    public String jsonPage(){
        return "excelToJSonPage";
    }


    @ResponseBody
    @RequestMapping("/convertExcel.do")
    public InfoAboutJsonVO convertJson(@RequestParam MultipartFile excelFile) throws IOException {

        log.debug("excelFile.getOriginalFilename():,{}", excelFile.getOriginalFilename());

        return jsonService.convertJson(excelFile);
    }

    @RequestMapping("/downLoadJson.do")
    public void downLoadJson(@RequestParam("path") String path, HttpServletResponse response) throws IOException {
        log.debug("/downLoadJson.do 에서 패스 확인 : {} " , path);
//        jsonService.downloadJson(path,response);

//        String filePath = "/Users/chs/excelToJson/jsonFolder/excel2json.json";
        File file = new File(path);
        String fileName = file.getName();

        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // 파일 내용을 읽어들여서 response body에 작성합니다.
        InputStream inputStream = new FileInputStream(file);
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();

        // 다운로드가 완료되면 파일을 삭제합니다.
//        file.delete();

    }

    @RequestMapping("/downJson.do")
    public void downJson(HttpServletResponse response) throws IOException {
        String path = "/Users/chs/excelToJson/jsonFolder/excel2json.json";
        File file = new File(path);
        String fileName = file.getName();

        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // 파일 내용을 읽어들여서 response body에 작성합니다.
        InputStream inputStream = new FileInputStream(file);
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }




}
