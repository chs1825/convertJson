package com.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ExcelUtils {

    private DecimalFormat df = new DecimalFormat("#.##");

    public List<Map<String, String>> handleExcel(MultipartFile file) {

        // 결과를 담을 리스트
        List<Map<String, String>> resList = new ArrayList<Map<String, String>>();
        // 컬럼명 추출 리스트
        List<String> columnNameList = new ArrayList<String>();
        //파일 이름
        String fileName = file.getOriginalFilename();

        Workbook workbook;

        //1.파일 버전 체크 및 버전별 객체 생성
        if(fileName.endsWith(".xls")){
            workbook = makeHssf(file);
        }else{
            workbook = makeXssf(file);
        }

        //2.시트 생성 및 시트 데이터 추출
        Sheet sheet = workbook.getSheetAt(0);
        log.debug("last로우:{} ", sheet.getLastRowNum());
        log.debug("last로우:{} ", sheet.getPhysicalNumberOfRows());
        for(int i =0; i <= sheet.getLastRowNum(); i++){
            //시트안의 row 별 데이터 추출
            List<String> cellList = getCellDataList(sheet.getRow(i));

            if(i ==0){//row에 대한 데이터가 컬럼명이라면
                columnNameList = cellList;
            }else{//결과값 생성
                if(isAllBlank(cellList)){
                    continue;
                }
               resList.add(getRowDataMap(columnNameList,cellList));
            }
        }
        return resList;
    }


    public boolean isAllBlank(List<String> cellList) {
        if (cellList == null || cellList.isEmpty()) {
            return true; // null 또는 빈 리스트는 공백으로 간주합니다.
        }

        for (String cell : cellList) {
            if (cell != null && !cell.trim().isEmpty()) {
                return false; // 문자열에 실제 문자가 포함되어 있으면 false를 반환합니다.
            }
        }

        return true; // 모든 문자열이 공백 문자로만 이루어져 있으면 true를 반환합니다.
    }



    private Map<String, String> getRowDataMap(List<String> columnNameList, List<String> cellList) {
        // 컬럼명과 일반 데이터를 조합하고 map에 담아 리스트에 추가하는 과정
        Map<String, String> resMap = new HashMap<String, String>();
        for (int i = 0; i < columnNameList.size(); i++) {
            resMap.put(columnNameList.get(i), cellList.get(i));

        }
        return resMap;
    }



    //셀 데이터 추출
    public List<String> getCellDataList(Row row){

        List<String> list = new ArrayList<String>();

        for (int i = 0; i < row.getLastCellNum(); i++){
            //셀이 null인지 체크
            Cell cell = row.getCell(i,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            String value = "";
            DecimalFormat decimalFormat = new DecimalFormat("#");
            switch (cell.getCellType()) {
                case FORMULA:
//                    value = cell.getCellFormula();
//                    value = String.valueOf(cell.getNumericCellValue());
                    value = decimalFormat.format(cell.getNumericCellValue());
                    break;
                case NUMERIC:
                    // 날짜 값이면, SimpleDateFormat을 사용하여 문자열로 변환합니다.
                    if (DateUtil.isCellDateFormatted(cell)) {
                        value = new SimpleDateFormat("yyyyMMdd").format(cell.getDateCellValue());
                    } else {
                        double v = Double.valueOf(cell.getNumericCellValue()).doubleValue();
                        value = df.format(v);
                    }
                    break;
                case STRING:
                    value = cell.getStringCellValue() + "";
                    break;
                case BLANK:
                    value = "";
                    break;
                case ERROR:
                    value = cell.getErrorCellValue() + "";
                    break;
            }
            list.add(value);
        }

        return list;

    }

    private Workbook makeHssf(MultipartFile file) {

        Workbook workbook = null;

        try {
            workbook = new HSSFWorkbook(file.getInputStream());
        } catch (IOException e) {

        }

        return workbook;
    }

    private Workbook makeXssf(MultipartFile file) {

        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {

        }
        return workbook;
    }

}