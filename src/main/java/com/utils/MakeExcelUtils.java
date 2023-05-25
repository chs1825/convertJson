package com.utils;

import com.convert.json.vo.json2ExcelVO.ExcelVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STAxis;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataConsolidateFunction;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

@Slf4j
public class MakeExcelUtils {


    public String makeAccuracyExcel(List<ExcelVO> excelVOList) throws ParseException {


        // 새로운 워크북 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 시트 생성
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        // 데이터 입력
        Object[][] column = {
                {"표본명", "기관명+문서번호", "기관명", "보도자료 생산일자", "보도자료 제목", "잘못된 사례 분류 \n (어문규범/어법)", "잘못된 사례", "세부유형", "주석 메모",
                        "잘못된 표현 \n 표기 개수", "보도자료 \n 어절 개수", "보도자료 1매 기준 잘못된 표현 개수", "연구진 검토 의견", "국어원 검토 요청"
                }
        };


        int rowNum = 0;
        System.out.println("엑셀 파일 생성 중...");

        //셀 개행 적용 스타일 생성
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);

        //첫번째 로우 생성
        for (Object[] rowData : column) {
            XSSFRow row = sheet.createRow(rowNum++);

            int cellNum = 0;
            for (Object field : rowData) {
                XSSFCell cell = row.createCell(cellNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                    cell.setCellStyle(cellStyle);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                    cell.setCellStyle(cellStyle);
                }
            }
        }


        for (ExcelVO excelVO : excelVOList) {
            XSSFRow row = sheet.createRow(rowNum++);
            int cellNum = 0;

            // 표본명
            XSSFCell cell0 = row.createCell(cellNum++);
            if (excelVO.getId() != null) {
                cell0.setCellValue(excelVO.getId());
            }


            // 기관명+문서번호
            XSSFCell cell1 = row.createCell(cellNum++);
            if (excelVO.getDocumentNumber() != null) {
                cell1.setCellValue(excelVO.getDocumentNumber());
            }


            // 기관명
            XSSFCell cell2 = row.createCell(cellNum++);
            if (excelVO.getOrganizationName() != null) {
                cell2.setCellValue(excelVO.getOrganizationName());
            }

            // 보도자료 생산일자
            XSSFCell cell3 = row.createCell(cellNum++);
            if (excelVO.getDate() != null) {
                cell3.setCellValue(excelVO.getDate());
            }

            // 보도자료 제목
            XSSFCell cell4 = row.createCell(cellNum++);
            if (excelVO.getPressTitle() != null) {
                cell4.setCellValue(excelVO.getPressTitle());
            }

            // 잘못된 사례 분류(어문규범/어법)
            XSSFCell cell5 = row.createCell(cellNum++);
            String errorCaseType = "";
            if (excelVO.getErrorCaseType() != null) {
                if (excelVO.getErrorCaseType().equals("grammar")) {
                    errorCaseType = "어법";
                } else {
                    errorCaseType = "어문규범";
                }
            }
            cell5.setCellValue(errorCaseType);

            // 잘못된 사례
            XSSFCell cell6 = row.createCell(cellNum++);
            if (excelVO.getErrorCase() != null) {
                String text = excelVO.getErrorCase();
                XSSFRichTextString richTextString = new XSSFRichTextString(text);
                List<int[]> underLineList = excelVO.getUnderLineInfo();

                if (underLineList != null || underLineList.size() != 0) {
                    for (int i = 0; i < underLineList.size(); i++) {
                        int startIndex = underLineList.get(i)[0];
                        int endIndex = underLineList.get(i)[1];
                        richTextString.applyFont(startIndex, endIndex, getFontWithUnderline(workbook));
                    }
                }
                cell6.setCellValue(richTextString);
            }

            // 세부유형
            XSSFCell cell7 = row.createCell(cellNum++);
            String detailType = "";

            if (excelVO.getDetailType() != null) {

                switch (excelVO.getDetailType()) {
                    case "sv-response":
                        detailType = "주어와 서술어의 호응 오류";
                        break;
                    case "ov-response":
                        detailType = "목적어와 서술어의 호응 오류";
                        break;
                    case "adv-response":
                        detailType = "부사어와 서술어의 호응 오류";
                        break;
                    case "connection":
                        detailType = "문장 연결 오류";
                        break;
                    case "essential-component":
                        detailType = "문장의 필수 성분 생략 오류";
                        break;
                    case "voca":
                        detailType = "어휘 사용 오류";
                        break;
                    case "ambiguity":
                        detailType = "중의성 오류";
                        break;
                    case "essencial-voca":
                        detailType = "필수 어휘 누락 오류";
                        break;
                    case "improper":
                        detailType = "부적절한 연결 어미 사용";
                        break;
                    case "conjuntion":
                        detailType = "접속 오류";
                        break;
                    case "word-order":
                        detailType = "어순 오류";
                        break;
                    case "etc":
                        detailType = "기타";
                        break;
                    case "misprint":
                        detailType = "오탈자";
                        break;
                    case "initial":
                        detailType = "두음법칙 미준수";
                        break;
                    case "bracket":
                        detailType = "괄호 뒤 조사 사용 오류";
                        break;
                    case "interpunct":
                        detailType = "가운뎃 점 사용 오류";
                        break;
                    case "loanword":
                        detailType = "외래어 표기법 미준수";
                        break;
                }
            }
            cell7.setCellValue(detailType);

            // 주석 메모
            XSSFCell cell8 = row.createCell(cellNum++);
            if (excelVO.getMemo() != null) {
                cell8.setCellValue(excelVO.getMemo());
            }

            // 잘못된 표현 표기 개수
            XSSFCell cell9 = row.createCell(cellNum++);
            cell9.setCellValue(excelVO.getAnnotCnt());


            // 보도자료 어절 개수
            XSSFCell cell10 = row.createCell(cellNum++);
            cell10.setCellValue(excelVO.getWordCnt());

            // 보도자료 1매 기준 잘못된 표현 개수
            XSSFCell cell11 = row.createCell(cellNum++);
            cell11.setCellValue(excelVO.getErrorForA4());

            // 연구진 검토 의견
            XSSFCell cell12 = row.createCell(cellNum++);
            if (excelVO.getInspectNote() != null) {
                cell12.setCellValue(excelVO.getInspectNote());
            }

        }

        //여기서 부터는 셀에대한 스탈일 및 핗터 적용

        // 컬럼에 필터 추가
        sheet.setAutoFilter(new CellRangeAddress(0, excelVOList.size(), 0, column[0].length - 1));

        // 첫 번째 로우의 컬럼 너비와 높이 설정
        XSSFRow firstRow = sheet.getRow(0);

        // 컬럼 너비 설정
        for (int i = 0; i < column[0].length; i++) {
            int columnIndex = i;
            int width = 17 * 256; // 너비 설정 (17 * 1/256)
            sheet.setColumnWidth(columnIndex, width);
        }

        // 첫 번째 로우의 높이 설정
        short height = 30 * 20; // 높이 설정 (30 * 20 포인트)
        firstRow.setHeight(height);

        // 하늘색 설정
        XSSFColor color = new XSSFColor(new byte[] { (byte) 176, (byte) 224, (byte) 230 }, null);

        // 첫 번째 로우에 대한 셀 스타일 생성
        XSSFCellStyle rowCellStyle = workbook.createCellStyle();

        // 색상 설정
        rowCellStyle.setFillForegroundColor(color);
        rowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 첫 번째 로우에 대한 셀 스타일 적용
        for (int i = 0; i < column[0].length; i++) {
            XSSFCell cell = firstRow.getCell(i);
            cell.setCellStyle(rowCellStyle);
        }


        String filePath = "";
        try {
            // 로컬 파일 경로
            filePath = "/Users/chs/jsonToExcel 프로그램 개발/결과폴더/testAccuracy.xlsx";

            // 엑셀 파일 저장
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();
            System.out.println(filePath + " 파일이 생성되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }


        return filePath;

    }

    public String makeSimplicityExcel(List<ExcelVO> excelVOList) throws ParseException {

        // 새로운 워크북 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 시트 생성
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        // 데이터 입력
        Object[][] column = {
                {"표본명", "기관명+문서번호", "기관명", "보도자료 생산일자", "보도자료 제목", "지적용어", "지적 용어 분류",
                        "어려운 표현 사용 문장", "주석메모", "어려운 표현 노출 개수", "보도자료 어절 개수", "보도자료 1매 기준 \n어려운 표현 개수",
                        "연구진 검토 의견", "국어원 검토 요청"
                }
        };


        int rowNum = 0;
        System.out.println("엑셀 파일 생성 중...");


        for (Object[] rowData : column) {
            XSSFRow row = sheet.createRow(rowNum++);

            int cellNum = 0;
            for (Object field : rowData) {
                XSSFCell cell = row.createCell(cellNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }


        for (ExcelVO excelVO : excelVOList) {
            XSSFRow row = sheet.createRow(rowNum++);
            int cellNum = 0;

            // 표본명
            XSSFCell cell0 = row.createCell(cellNum++);
            if (excelVO.getId() != null) {
                cell0.setCellValue(excelVO.getId());
            }


            // 기관명+문서번호
            XSSFCell cell1 = row.createCell(cellNum++);
            if (excelVO.getDocumentNumber() != null) {
                cell1.setCellValue(excelVO.getDocumentNumber());
            }


            // 기관명
            XSSFCell cell2 = row.createCell(cellNum++);
            if (excelVO.getOrganizationName() != null) {
                cell2.setCellValue(excelVO.getOrganizationName());
            }


            // 보도자료 생산일자
            XSSFCell cell3 = row.createCell(cellNum++);
            if (excelVO.getDate() != null) {
                cell3.setCellValue(excelVO.getDate());
            }


            // 보도자료 제목
            XSSFCell cell4 = row.createCell(cellNum++);
            if (excelVO.getPressTitle() != null) {
                cell4.setCellValue(excelVO.getPressTitle());
            }


            //지적 용어
            XSSFCell cell5 = row.createCell(cellNum++);
            if (excelVO.getText() != null) {
                cell5.setCellValue(excelVO.getText());
            }


            // 지적 용어 분류
            XSSFCell cell6 = row.createCell(cellNum++);
            String markType = "";
            if (excelVO.getMarkType() != null) {
                switch (excelVO.getMarkType()) {
                    case "language":
                        markType = "외국어";
                        break;
                    case "letter":
                        markType = "외국 글자";
                        break;
                    case "discussion":
                        markType = "논의 대상";
                        break;
                }
            }
            cell6.setCellValue(markType);

            // 어려운 표현 사용 문장
            XSSFCell cell7 = row.createCell(cellNum++);
            if (excelVO.getErrorCase() != null) {
                String text = excelVO.getErrorCase();
                XSSFRichTextString richTextString = new XSSFRichTextString(text);
                List<int[]> underLineList = excelVO.getUnderLineInfo();

                if (underLineList != null || underLineList.size() != 0) {
                    for (int i = 0; i < underLineList.size(); i++) {
                        int startIndex = underLineList.get(i)[0];
                        int endIndex = underLineList.get(i)[1];
                        richTextString.applyFont(startIndex, endIndex, getFontWithUnderline(workbook));
                    }
                }
                cell7.setCellValue(richTextString);
            }


            // 주석메모
            XSSFCell cell8 = row.createCell(cellNum++);
            if (excelVO.getMemo() != null) {
                cell8.setCellValue(excelVO.getMemo());
            }

            // 어려운 표현 노출 개수
            XSSFCell cell9 = row.createCell(cellNum++);
            cell9.setCellValue(excelVO.getAnnotCnt());

            // 보도자료 어절 개수
            XSSFCell cell10 = row.createCell(cellNum++);
            cell10.setCellValue(excelVO.getWordCnt());


            // 보도 자료 1매 기준 어려운 표현 개수
            XSSFCell cell11 = row.createCell(cellNum++);
            cell11.setCellValue(excelVO.getErrorForA4());

            //연구진 검토의견
            XSSFCell cell12 = row.createCell(cellNum++);
            if (excelVO.getInspectNote() != null) {
                cell12.setCellValue(excelVO.getInspectNote());
            }
        }

        //컬럼에 필터 추가
        sheet.setAutoFilter(new CellRangeAddress(0, excelVOList.size(), 0, column[0].length - 1));

        // 첫 번째 로우의 컬럼 너비와 높이 설정
        XSSFRow firstRow = sheet.getRow(0);

        // 컬럼 너비 설정
        for (int i = 0; i < column[0].length; i++) {
            int columnIndex = i;
            int width = 17 * 256; // 너비 설정 (17 * 1/256)
            sheet.setColumnWidth(columnIndex, width);
        }

        // 첫 번째 로우의 높이 설정
        short height = 30 * 20; // 높이 설정 (30 * 20 포인트)
        firstRow.setHeight(height);

        // 하늘색 설정
        XSSFColor color = new XSSFColor(new byte[] { (byte) 176, (byte) 224, (byte) 230 }, null);

        // 첫 번째 로우에 대한 셀 스타일 생성
        XSSFCellStyle rowCellStyle = workbook.createCellStyle();

        // 색상 설정
        rowCellStyle.setFillForegroundColor(color);
        rowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 첫 번째 로우에 대한 셀 스타일 적용
        for (int i = 0; i < column[0].length; i++) {
            XSSFCell cell = firstRow.getCell(i);
            cell.setCellStyle(rowCellStyle);
        }


        String filePath = "";
        try {
            // 로컬 파일 경로
            filePath = "/Users/chs/jsonToExcel 프로그램 개발/결과폴더/testSim.xlsx";

            // 엑셀 파일 저장
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();
            System.out.println(filePath + " 파일이 생성되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    private static XSSFFont getFontWithUnderline(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setUnderline(FontUnderline.SINGLE);
        return font;
    }

    public void addPivotTable(String excelFile, String chk) {

        String filePath = excelFile;

        try {
            // 엑셀 파일 읽기
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filePath));

            String sheetName = "";
            sheetName = chk.equals("accuracy") ? "정확성 기관별 통계" : "용이성 기관별 통계";
            log.debug("피벗시트 이름 : {}", sheetName);


            // 피벗 테이블 시트 만들기
            XSSFSheet pivotSheet = workbook.createSheet(sheetName);

            log.debug("피벗시트 체크 : {}", pivotSheet);

            // 데이터 시트 가져오기
            XSSFSheet dataSheet = workbook.getSheetAt(0); // 예시로 첫 번째 시트를 사용하겠습니다.

            // 피벗 테이블 생성을 위한 데이터 영역 설정
            int firstRow = dataSheet.getFirstRowNum();
            int lastRow = dataSheet.getLastRowNum();
            int firstCol = dataSheet.getRow(firstRow).getFirstCellNum();
            int lastCol = dataSheet.getRow(lastRow).getLastCellNum();

            log.debug("firstRow : {}", firstRow);
            log.debug("lastRow : {}", lastRow);
            log.debug("firstCol : {}", firstCol);
            log.debug("lastCol : {}", lastCol);


            // 데이터 영역의 범위
            AreaReference source = new AreaReference(new CellReference("A1"),
                    new CellReference("M" + (lastRow + 1)), SpreadsheetVersion.EXCEL2007);


            // 피벗 테이블 위치 설정
            CellReference position = new CellReference("A1");

            log.debug("널 체크 source : {}", source);
            log.debug("널 체크 position : {}", position);
            log.debug("널 체크 dataSheet : {}", dataSheet);

            // 피벗 테이블 생성
            XSSFPivotTable pivotTable = pivotSheet.createPivotTable(source, position, dataSheet);

            // 행 레이블 추가
            pivotTable.addRowLabel(2); // 첫 번째 열을 행 레이블로 추가

            // 데이터 필드 추가
            pivotTable.addColumnLabel(DataConsolidateFunction.SUM, 9,"합계 : 잘못된 표현 개수");
//            pivotTable.addColumnLabel(DataConsolidateFunction.AVERAGE, 10);
//            pivotTable.addColumnLabel(DataConsolidateFunction.AVERAGE, 11);


            // 엑셀 파일 저장
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();

            System.out.println("피벗 테이블 생성이 완료되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

