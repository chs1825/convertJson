package com.convert.json.vo.json2ExcelVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class AnnotCorrectVO {

    private String errorCaseType; //type
    private List<int[]> underLineIndexList;
    private String memo;
    private String detailType;
    private String inspectNote;
    private String markType;
    private String text;// 지적 용어



}
