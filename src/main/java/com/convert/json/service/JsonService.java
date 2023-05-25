package com.convert.json.service;

import com.convert.json.vo.InfoAboutJsonVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface JsonService {

    public InfoAboutJsonVO convertJson(MultipartFile excelFile) throws IOException;

//    public void downloadJson(String path, HttpServletResponse res) throws IOException;

}
