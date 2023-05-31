package com.convert.json.vo.json2ExcelVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
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
    private String original_utterance;  //원문

    public void setId(String id) {
        this.id = id;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPressTitle(String pressTitle) {
        this.pressTitle = pressTitle;
    }

    public void setErrorCaseType(String errorCaseType) {

        if(errorCaseType != null){
            switch (errorCaseType){
                case "grammar" :
                    this.errorCaseType = "어법";
                    break;
                case "norm":
                    this.errorCaseType = "어문규범";
                    break;
            }
        }else {
            this.errorCaseType = "";
        }

    }

    public void setErrorCase(String errorCase) {
        this.errorCase = errorCase;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setMarkType(String markType) {

        if(markType != null){

            switch (markType) {
                case "language":
                    this.markType = "외국어";
                    break;
                case "letter":
                    this.markType = "외국 글자";
                    break;
                case "discussion":
                    this.markType = "논의 대상";
                    break;
            }
        }else{
            this.markType ="";
        }



    }

    public void setDetailType(String detailType) {

        if (detailType != null) {

            switch (detailType) {
                //CONST_GRAMMAR_TYPES
                case "sv-response":
                    detailType = "주어와 서술어의 호응 오류";
                    break;
                case "ov-response":
                    detailType = "목적어와 서술어의 호응 오류";
                    break;
                case "modifier-response":
                    detailType = "수식어와 피수식어의 호응 오류";
                    break;
                case "conjuntion":
                    detailType = "접속 오류";
                    break;
                case "omit":
                    detailType = "생략 오류";
                    break;
                case "voca":
                    detailType = "어휘 사용 오류";
                    break;
                case "ambiguity":
                    detailType = "중의성 오류";
                    break;
                case "ending":
                    detailType = "어미 오류";
                    break;
                case "postposition":
                    detailType = "조사 오류";
                    break;
                case "word-order":
                    detailType = "어순 오류";
                    break;
                case "etc":
                    detailType = "기타";
                    break;

//                case "essential-component":
//                    detailType = "문장의 필수 성분 생략 오류";
//                    break;
//                case "etc-response":
//                    detailType = "기타 호응 오류";
//                    break;
//                case "adv-response":
//                    detailType = "부사어와 서술어의 호응 오류";
//                    break;
//                case "essencial-voca":
//                    detailType = "필수 어휘 누락 오류";
//                    break;
//                case "improper":
//                    detailType = "부적절한 연결 어미 사용";
//                    break;
//                case "connection":
//                    detailType = "문장 연결 오류";
//                    break;

                // CONST_NORM_TYPE
                case "initial":
                    detailType = "두음법칙 미준수";
                    break;
                case "siot":
                    detailType = "사이시옷 원칙 미준수";
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
                case "misprint":
                    detailType = "오탈자";
                    break;

            }
        }

        this.detailType = detailType;
    }

    public void setInspectNote(String inspectNote) {
        this.inspectNote = inspectNote;
    }

    public void setAnnotCnt(int annotCnt) {
        this.annotCnt = annotCnt;
    }

    public void setWordCnt(int wordCnt) {
        this.wordCnt = wordCnt;
    }

    public void setErrorForA4(Double errorForA4) {
        this.errorForA4 = errorForA4;
    }

    public void setUnderLineInfo(List<int[]> underLineInfo) {
        this.underLineInfo = underLineInfo;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOriginal_utterance(String original_utterance) {
        this.original_utterance = original_utterance;
    }
}
