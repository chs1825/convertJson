package com.convert.json.service;

import com.convert.json.vo.InfoAboutJsonVO;
import com.convert.json.vo.JSonVO;
import com.convert.json.vo.MetaDataVO;
import com.convert.json.vo.UtteranceVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@ComponentScan(basePackages = {"com.utils"})
public class BackupImpl {

    private ExcelUtils excelUtils;

    public BackupImpl(ExcelUtils excelUtils) {
        this.excelUtils = excelUtils;
    }

    public InfoAboutJsonVO convertJson(MultipartFile excelFile) throws IOException {
        //1.엑셀 변환
        List<Map<String, String>> dataList = excelUtils.handleExcel(excelFile);

//        log.debug(dataList.toString());
        log.debug("데이터 개수 : {}", dataList.size());
        List<JSonVO> jsonList = new ArrayList<JSonVO>();
        int cntForUtterence = 0; // 사이즈 1인 utterance 개수
        int cntForUtterenceAndEnter = 0; // 사이즈 1이면서 개행없는 utterance 개수
        List<String> chkList1 = new ArrayList<String>(); // 사이즈 1인 utterance 아이디 담는 리스트
        List<String> chkList2 = new ArrayList<String>(); // 사이즈가 1이면서 개행이 없는 utterance 다는 아이디 리스트

        for(int i =0; i < dataList.size(); i++){

            //2.데이터 처리
            JSonVO jSonVO = new JSonVO();
            MetaDataVO metaDataVO = new MetaDataVO();
            String text = dataList.get(i).get("보도자료 본문");

            //2-1.아이디 생성
            jSonVO.setId("JSON"+ (i+1));
            log.debug(jSonVO.getId());
//            log.debug("날짜: {}", dataList.get(i).get("생성일"));
            // 2-2 MetaDataVO 생성
            metaDataVO.setDate(dataList.get(i).get("생성일"));
            metaDataVO.setOrgan_name(dataList.get(i).get("기관명"));
            metaDataVO.setOrgan_class(dataList.get(i).get("기관 특성"));
            metaDataVO.setTitle(dataList.get(i).get("보도자료 제목"));
            metaDataVO.setCharge(dataList.get(i).get("평가 모둠"));
            //어절수 세기
//            int wordCnt = countWord(text);
            int wordCnt = getWord_cnt(text);

            metaDataVO.setWord_cnt(String.valueOf(wordCnt));

            //3. jsonVO 완성
            jSonVO.setMetaData(metaDataVO);

            //utterance 만들기


            List<UtteranceVO> utteranceVOList = makeUtteranceList(text);
            if(utteranceVOList.size() == 1) {
                cntForUtterence++;
                chkList1.add(jSonVO.getId());
                if(!utteranceVOList.get(0).getForm().contains("\n")){
                    cntForUtterenceAndEnter++;
                    chkList2.add(jSonVO.getId());
                }
            }
            jSonVO.setUtterance(utteranceVOList);

            //4. 리스트에 넣어주기
            jsonList.add(jSonVO);
        }

        //4.제이슨 파일 생성
        String path = makeJsonFile(jsonList);

        //5. 반환 정보 세팅
        InfoAboutJsonVO infoVO = new InfoAboutJsonVO();

        infoVO.setPath(path);
        infoVO.setDataSize(jsonList.size());
        infoVO.setUtterenceNum(cntForUtterence);
        infoVO.setUtterenceNumId(chkList1);
        infoVO.setNoEnterUtterenceNum(cntForUtterenceAndEnter);
        infoVO.setNoEnterUtterenceNumId(chkList2);

        return infoVO;

    }



    private List<UtteranceVO> makeUtteranceList(String text) {

        List<UtteranceVO> resList = new ArrayList<UtteranceVO>();

        String[] textArr = text.split("\\r+\\n+");


        List<String> sList = new ArrayList<String>();
        for (String item : textArr) {
            String[] arr = item.split("\n\\s*\n");

            for(int i =0; i < arr.length; i++){
                if(arr[i].matches("\\s*")){
                    continue;
                }else{
                    arr[i] = arr[i].replaceAll("\\s+$", "");
                    sList.add(arr[i]);
                }
            }
        }

        int cnt = 1;
        for (int i = 0; i < sList.size(); i++) {
            String item = sList.get(i);
            if(item.startsWith("\n")){
                item = item.substring(1);
            }

            if(item.endsWith("\n")){
                item = item.substring(0, item.length() - 1);
            }

            UtteranceVO utteranceVO = new UtteranceVO();

            utteranceVO.setId(String.valueOf(cnt));
            utteranceVO.setForm(item);
            resList.add(utteranceVO);
            cnt++;
        }

        return resList;
    }


    public int getWord_cnt(String text){

        String[] textArr = text.split("\\r+\\n+");


        List<String> sList = new ArrayList<String>();
        for (String item : textArr) {
            String[] arr = item.split("\n\\s*\n");

            for(int i =0; i < arr.length; i++){
                if(arr[i].matches("\\s*")){
                    continue;
                }else{
                    arr[i] = arr[i].replaceAll("\\s+$", "");
                    sList.add(arr[i]);
                }
            }
        }

        int res = 0;
        for (int i = 0; i < sList.size(); i++) {
            String item = sList.get(i);
            if(item.startsWith("\n")){
                item = item.substring(1);
            }

            if(item.endsWith("\n")){
                item = item.substring(0, item.length() - 1);
            }
//            System.out.println(String.valueOf(countWord(item)));
            res += countWord(item);
        }
//        System.out.println("총합 = " + res);

        return res;
    }

    private int countWord(String text) {
        int res  = 0;


        if(text == null || text.isEmpty()){
            return 0;
        }

        String[] words = text.split("\\ +");
//        String[] words = text.split("\\s+|\\n");
//        String[] words = text.split("\\s+");  //나중 업무 대응용
        res = words.length;

        return res;
    }


    public String makeJsonFile(List<JSonVO> dataList){
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//            objectMapper.enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
            objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

            // .json 파일 생성
            String filePath = "/Users/chs/excelToJson/jsonFolder/excel2json.json";
            File file = new File(filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            objectMapper.writeValue(fileOutputStream, dataList);
            fileOutputStream.close();

            return filePath;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }











    public void downloadJson(String path, HttpServletResponse res) throws IOException {
        File file = new File(path);
        String fileName = file.getName();

        res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        res.setContentType("application/json");

        InputStream fileInputStream = new FileInputStream(file);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ServletOutputStream outputStream = res.getOutputStream();
        OutputStream outputStream = res.getOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

//        byte[] jsonData = outputStream.toByteArray();
        outputStream.flush();
        outputStream.close();
        fileInputStream.close();
    }

    private void downLoadFile(String path, HttpServletResponse res) throws IOException {

        File file = new File(path);
        String fileName = file.getName();

        res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        res.setContentType("application/json");

        InputStream fileInputStream = new FileInputStream(file);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ServletOutputStream outputStream = res.getOutputStream();
        OutputStream outputStream = res.getOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

//        byte[] jsonData = outputStream.toByteArray();
        outputStream.flush();
        outputStream.close();
        fileInputStream.close();
        //다운로드

//        res.setContentLength(jsonData.length);

//        ServletOutputStream servletOutputStream = res.getOutputStream();
//        servletOutputStream.write(jsonData);
//        servletOutputStream.flush();
//        servletOutputStream.close();

        // 파일을 삭제합니다.
//        file.delete();

    }



}
