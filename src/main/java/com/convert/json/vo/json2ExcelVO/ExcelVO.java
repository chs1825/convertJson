package com.convert.json.vo.json2ExcelVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString

public class ExcelVO {

    private String id; // 표본명
    private String documentNumber; // 문서번호
    private String organizationName; // 기관명
    private String date; // 보도자료 생산일자
    private String pressTitle; // 보도자료 제목
    private String errorCaseType; // 잘못된 사례 분류
    private String errorCase; // 잘못된 사례
    private String memo; // 의견란
    private String markType; // 지적용어분류
    private String detailType; // 어법지적이유
    private String inspectNote; // 연구진 검토 의견
    private int annotCnt; // 잘못된 표기 개수
    private int wordCnt; // 보도자료 어절 개수
    private Double errorForA4; // 보도자료 1매 기준 잘못된 표현 개수
    private List<int[]> underLineInfo; //밑줄 그을 정보
    private String text;// 지적 용어




}
