package com.convert.json.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MetaDataVO {


    private String date;

    //기관명1
    private String organ_name1;

    //기관명2
    private String organ_name2;

    //번호
    private String organ_num;

    //부서명
    private String organ_part;

    //링크주소
    private String link;

    //엑셀자료에 있는 어절수
    private String original_word_cnt;

    //보도자료 문서명1
    private String press_name;

    private String note;

//    private String organ_class;

    private String title;

//    private String charge;

    //직접 카운트한 어절수
    private String word_cnt;

}
