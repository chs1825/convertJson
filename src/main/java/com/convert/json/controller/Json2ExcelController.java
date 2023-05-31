package com.convert.json.controller;

import com.convert.json.vo.json2ExcelVO.ExcelVO;
import com.utils.MakeExcelUtils;
import com.utils.MappingExcelVOUtils;
import com.utils.ReadJsonFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("")
public class Json2ExcelController {

    @RequestMapping("goJson2Excel.do")
    public String goJson2Excel(Model model) throws IOException, ParseException {


//        String path = "/Users/chs/jsonToExcel 프로그램 개발/용이성 평가/export_simplicity_amended.json";
//        String chk = "simplicity";

        String path = "/Users/chs/jsonToExcel 프로그램 개발/정확성평가/export_acurracy_amended.json";
        String chk = "accuracy";


        ReadJsonFileUtils readJsonFileUtils = new ReadJsonFileUtils();
        MappingExcelVOUtils mappingExcelVOUtils = new MappingExcelVOUtils();
        MakeExcelUtils makeExcelUtils = new MakeExcelUtils();
        List<ExcelVO> resList = mappingExcelVOUtils.mappingJsonToObject(readJsonFileUtils.readJsonFile(path),chk);


        if (chk.equals("simplicity")) {
            String excelFile = makeExcelUtils.makeSimplicityExcel(resList);
            log.debug("용이성 피벗 실행 시작 : {}" , excelFile);

            makeExcelUtils.addPivotTable(excelFile,chk);
        } else {
            String excelFile = makeExcelUtils.makeAccuracyExcel(resList);
            log.debug("정확성 피벗 실행 시작 : {}" , excelFile);
            makeExcelUtils.addPivotTable(excelFile,chk);
        }



        return "json2Excel";
    }








}
