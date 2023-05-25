package com.utils;

import com.convert.json.vo.json2ExcelVO.AnnotCorrectVO;
import com.convert.json.vo.json2ExcelVO.ExcelVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.basc.framework.json.Json;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class MappingExcelVOUtils {


    public List<ExcelVO> mappingJsonToObject(String jsonContent, String jsonType) throws IOException {

        List<ExcelVO> resList = new ArrayList<>();

        //전체 제이슨 노드 생성
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonContent);

        log.debug("제이슨 노드 변환 : {}", jsonNode);

        if (jsonNode.isArray()) {
            log.debug("리스트입니다.");
            return makeExcelVOList(jsonNode, resList, jsonType);
        } else {
            log.debug("리스트가 아닙니다.");

            JsonNode idJasoonNode = jsonNode.get("id");
            log.debug("아이디 노드 찍히나? : {}" , idJasoonNode);
            String id = idJasoonNode.asText();
            log.debug("아이디 찍히나? : {}" , id);

            //metaData 노드
            JsonNode metaDataJsonNode = jsonNode.get("metaData");
            String organ_name1 = metaDataJsonNode.path("organ_name1").asText();
            String organ_name2 = metaDataJsonNode.path("organ_name2").asText();
            String organ_num = metaDataJsonNode.path("organ_num").asText();
            String dateStr = metaDataJsonNode.path("date").asText();
            StringBuilder formattedDate = new StringBuilder(dateStr);
            formattedDate.insert(4, ".");
            formattedDate.insert(7, ".");
            String date = formattedDate.toString();


            String title = metaDataJsonNode.path("title").asText();

            //annotInfos.accuracyInfo 노드
            JsonNode annotInfosJsonNode = null;

            //annotInfos.accuracyInfo 노드
            if (jsonType.equals("simplicity")) {
                annotInfosJsonNode = jsonNode.get("annotInfos").get("simplicityInfo");
            } else if (jsonType.equals("accuracy")) {
                annotInfosJsonNode = jsonNode.get("annotInfos").get("accuracyInfo");

            }

            int wordCnt = Integer.parseInt(annotInfosJsonNode.path("wordCnt").asText());
            int annotCnt = Integer.parseInt(annotInfosJsonNode.path("annotCnt").asText());
            double errorForA4 = (annotInfosJsonNode.path("errorForA4").asDouble());


            //annotCorrect 노드
            JsonNode annotCorrectJsonNode = jsonNode.get("annotCorrect");



            //utterance id 주머니
            Set<Integer> presentIdList = new HashSet<>();
            for (int i = 0; i < annotCorrectJsonNode.size(); i++) {
//            presentIdList.add(Integer.parseInt(annotCorrectJsonNode.get(i).path("utteranceId").asText()));
                presentIdList.add((annotCorrectJsonNode.get(i).path("utteranceId").asInt()));
            }


            //본격적인 vo 만들기
            for (int i = 0; i < jsonNode.get("utterance").size(); i++) {

                JsonNode utteranceJsonNode = jsonNode.get("utterance").get(i);
//            int utteranceId = Integer.parseInt(utteranceJsonNode.path("id").asText());
                int utteranceId = utteranceJsonNode.path("id").asInt();

                if(annotCorrectJsonNode.size() == 0 ){

                    ExcelVO finalExcelVO = new ExcelVO();

                    //아이디 정보
                    finalExcelVO.setId(id);

                    //metaData 정보 만들기
                    finalExcelVO.setDocumentNumber(organ_name1 + "-" + organ_name2 + "-" + organ_num);
                    finalExcelVO.setOrganizationName(organ_name1 + "-" + organ_name2);
                    finalExcelVO.setDate(date);

                    finalExcelVO.setPressTitle(title);

                    //annotInfos.accuracyInfo  정보 만들기
                    finalExcelVO.setWordCnt(0);
                    finalExcelVO.setAnnotCnt(annotCnt);
                    finalExcelVO.setErrorForA4(errorForA4);


                    resList.add(finalExcelVO);
                }else{
                    //utterance에 해당하는게 있는지 검사 해서 없으면 continue;
                    if (!presentIdList.contains(utteranceId)) {
                        continue;
                    }

                    Map<String, AnnotCorrectVO> annotCorrectVOMap = makeAnnotCorrectVOMap(utteranceId, annotCorrectJsonNode, jsonType);

                    for (String key : annotCorrectVOMap.keySet()) {
                        ExcelVO finalExcelVO = new ExcelVO();

                        //아이디 정보
                        finalExcelVO.setId(id);

                        //metaData 정보 만들기
                        finalExcelVO.setDocumentNumber(organ_name1 + "-" + organ_name2 + "-" + organ_num);
                        finalExcelVO.setOrganizationName(organ_name1 + "-" + organ_name2);
                        finalExcelVO.setDate(date);

                        finalExcelVO.setPressTitle(title);

                        //annotInfos.accuracyInfo  정보 만들기
                        finalExcelVO.setWordCnt(wordCnt);
                        finalExcelVO.setAnnotCnt(annotCnt);
                        finalExcelVO.setErrorForA4(errorForA4);
                        //의견란만들기
                        finalExcelVO.setErrorCase(utteranceJsonNode.path("annot_form").asText());

                        //annotCorrect 전체 만들어주기
                        log.debug("최종 key : {}", key);
                        AnnotCorrectVO annotCorrectVO = annotCorrectVOMap.get(key);

                        // errorCaseType
                        String errorCaseType = annotCorrectVO.getErrorCaseType();
                        finalExcelVO.setErrorCaseType(errorCaseType);

                        // memo
                        String memo = annotCorrectVO.getMemo();
                        finalExcelVO.setMemo(memo);

                        // detailType
                        String detailType = annotCorrectVO.getDetailType();
                        finalExcelVO.setDetailType(detailType);

                        // inspectNote
                        String inspectNote = annotCorrectVO.getInspectNote();
                        finalExcelVO.setInspectNote(inspectNote);

                        //markType
                        String markType = annotCorrectVO.getMarkType();
                        finalExcelVO.setMarkType(markType);

                        //underline
                        log.debug("여기가 문제인가? : {}", annotCorrectVO.getUnderLineIndexList());
                        finalExcelVO.setUnderLineInfo(annotCorrectVO.getUnderLineIndexList());
                        log.debug("여기가 문제인가 vo 확인 : {}", finalExcelVO);


                        resList.add(finalExcelVO);
                    }
                }
            }
        }

        return resList;

    }


    private List<ExcelVO> makeExcelVOList(JsonNode BigJsonNode, List<ExcelVO> resList, String jsonType) {

        for (int j = 0; j < BigJsonNode.size(); j++) {
            JsonNode jsonNode = BigJsonNode.get(j);

            JsonNode idJasoonNode = jsonNode.get("id");
            log.debug("아이디 노드 찍히나? : {}" , idJasoonNode);
            String id = idJasoonNode.asText();
            log.debug("아이디 찍히나? : {}" , id);

            //metaData 노드
            JsonNode metaDataJsonNode = jsonNode.get("metaData");
            String organ_name1 = metaDataJsonNode.path("organ_name1").asText();
            String organ_name2 = metaDataJsonNode.path("organ_name2").asText();
            String organ_num = metaDataJsonNode.path("organ_num").asText();
            String dateStr = metaDataJsonNode.path("date").asText();
            StringBuilder formattedDate = new StringBuilder(dateStr);
            formattedDate.insert(4, ".");
            formattedDate.insert(7, ".");
            String date = formattedDate.toString();


            String title = metaDataJsonNode.path("title").asText();

            JsonNode annotInfosJsonNode = null;

            //annotInfos.accuracyInfo 노드
            if (jsonType.equals("simplicity")) {
                annotInfosJsonNode = jsonNode.get("annotInfos").get("simplicityInfo");
            } else if (jsonType.equals("accuracy")) {
                annotInfosJsonNode = jsonNode.get("annotInfos").get("accuracyInfo");

            }

            int wordCnt = Integer.parseInt(annotInfosJsonNode.path("wordCnt").asText());
            int annotCnt = Integer.parseInt(annotInfosJsonNode.path("annotCnt").asText());
            double errorForA4 = (annotInfosJsonNode.path("errorForA4").asDouble());
            //annotCorrect 노드
            JsonNode annotCorrectJsonNode = jsonNode.get("annotCorrect");

            //utterance id 주머니
            Set<Integer> presentIdList = new HashSet<>();
            for (int i = 0; i < annotCorrectJsonNode.size(); i++) {
                presentIdList.add((annotCorrectJsonNode.get(i).path("utteranceId").asInt()));
            }

            //본격적인 vo 만들기
            for (int i = 0; i < jsonNode.get("utterance").size(); i++) {

                JsonNode utteranceJsonNode = jsonNode.get("utterance").get(i);
                int utteranceId = utteranceJsonNode.path("id").asInt();

                if(annotCorrectJsonNode.size() == 0 ){  //annotCorrectJsonNode가 빈 배열일때

                    ExcelVO finalExcelVO = new ExcelVO();

                    //아이디 정보
                    finalExcelVO.setId(id);

                    //metaData 정보 만들기
                    finalExcelVO.setDocumentNumber(organ_name1 + "-" + organ_name2 + "-" + organ_num);
                    finalExcelVO.setOrganizationName(organ_name1 + "-" + organ_name2);
                    finalExcelVO.setDate(date);

                    finalExcelVO.setPressTitle(title);

                    //annotInfos.accuracyInfo  정보 만들기
                    finalExcelVO.setWordCnt(0);
                    finalExcelVO.setAnnotCnt(annotCnt);
                    finalExcelVO.setErrorForA4(errorForA4);


                    resList.add(finalExcelVO);

                }else {
                    //utterance에 해당하는게 있는지 검사 해서 없으면 continue;
                    if (!presentIdList.contains(utteranceId)) {
                        continue;
                    }

                    Map<String, AnnotCorrectVO> annotCorrectVOMap = makeAnnotCorrectVOMap(utteranceId, annotCorrectJsonNode, jsonType);

                    for (String key : annotCorrectVOMap.keySet()) {

                        ExcelVO finalExcelVO = new ExcelVO();

                        //아이디 정보
                        finalExcelVO.setId(id);

                        //metaData 정보 만들기
                        finalExcelVO.setDocumentNumber(organ_name1 + "-" + organ_name2 + "-" + organ_num);
                        finalExcelVO.setOrganizationName(organ_name1 + "-" + organ_name2);
                        finalExcelVO.setDate(date);

                        finalExcelVO.setPressTitle(title);

                        //annotInfos.accuracyInfo  정보 만들기
                        finalExcelVO.setWordCnt(wordCnt);
                        finalExcelVO.setAnnotCnt(annotCnt);
                        finalExcelVO.setErrorForA4(errorForA4);


                        //의견란만들기
                        finalExcelVO.setErrorCase(utteranceJsonNode.path("annot_form").asText());

                        //annotCorrect 전체 만들어주기
                        log.debug("최종 key : {}", key);
                        AnnotCorrectVO annotCorrectVO = annotCorrectVOMap.get(key);

                        // errorCaseType
                        String errorCaseType = annotCorrectVO.getErrorCaseType();
                        finalExcelVO.setErrorCaseType(errorCaseType);

                        // memo
                        String memo = annotCorrectVO.getMemo();
                        finalExcelVO.setMemo(memo);

                        // detailType
                        String detailType = annotCorrectVO.getDetailType();
                        finalExcelVO.setDetailType(detailType);

                        // inspectNote
                        String inspectNote = annotCorrectVO.getInspectNote();
                        finalExcelVO.setInspectNote(inspectNote);

                        //markType
                        String markType = annotCorrectVO.getMarkType();
                        finalExcelVO.setMarkType(markType);

                        //text 지적용어
                        String text = annotCorrectVO.getText();
                        finalExcelVO.setText(text);

                        //underline
                        log.debug("여기가 문제인가? : {}", annotCorrectVO.getUnderLineIndexList());
                        finalExcelVO.setUnderLineInfo(annotCorrectVO.getUnderLineIndexList());
                        log.debug("여기가 문제인가 vo 확인 : {}", finalExcelVO);


                        resList.add(finalExcelVO);
                    }
                }
            }
        }
        return resList;
    }


    private Map<String, AnnotCorrectVO> makeAnnotCorrectVOMap(int chkUtteranceId, JsonNode annotCorrectJsonNode, String jsonType) {

        Map<String, AnnotCorrectVO> annotCorrectInfoMap = new HashMap<>();
        List<String> groupList = new ArrayList<>();
        int noGroupCnt = 1;


        for (int i = 0; i < annotCorrectJsonNode.size(); i++) {

            int utteranceId = annotCorrectJsonNode.get(i).path("utteranceId").asInt();
            AnnotCorrectVO valueVO = new AnnotCorrectVO();

            if (chkUtteranceId == utteranceId) {  //아이디가 일치하면

                if ((annotCorrectJsonNode.get(i).has("group") && annotCorrectJsonNode.get(i).path("group").asText().equals(""))
                        || !annotCorrectJsonNode.get(i).has("group")) {
                    String key = "noGroupId" + noGroupCnt;
                    List<int[]> indexList = new ArrayList<>();
                    int beginIndex = annotCorrectJsonNode.get(i).path("begin").asInt();
                    int endIndex = annotCorrectJsonNode.get(i).path("end").asInt();
                    indexList.add(new int[]{beginIndex, endIndex});

                    valueVO.setErrorCaseType(annotCorrectJsonNode.get(i).path("type").asText());
                    valueVO.setMemo(annotCorrectJsonNode.get(i).path("memo").asText());
                    valueVO.setDetailType(annotCorrectJsonNode.get(i).path("detailType").asText());
                    valueVO.setInspectNote(annotCorrectJsonNode.get(i).path("inspectNote").asText());
                    valueVO.setMarkType(annotCorrectJsonNode.get(i).path("markType").asText());
                    valueVO.setText(annotCorrectJsonNode.get(i).path("text").asText());
                    valueVO.setUnderLineIndexList(indexList);
                    annotCorrectInfoMap.put(key, valueVO);

                    noGroupCnt++;
                } else {
                    String groupId = annotCorrectJsonNode.get(i).path("group").asText();
                    if (!groupList.contains(groupId)) {
                        groupList.add(groupId);
                    }
                }

            }//if문 종료

        }//for문 종료
        if(jsonType.equals("simplicity")){

            return annotCorrectInfoMap;
        }
        return makeFinalAnnotCorrectVOMap(chkUtteranceId, groupList, annotCorrectInfoMap, annotCorrectJsonNode);
    }

    private Map<String, AnnotCorrectVO> makeFinalAnnotCorrectVOMap(int chkUtteranceId, List<String> groupList, Map<String, AnnotCorrectVO> annotCorrectInfoMap, JsonNode annotCorrectJsonNode) {


        for (int i = 0; i < groupList.size(); i++) {

            String groupId = groupList.get(i);
            AnnotCorrectVO annotCorrectVO = new AnnotCorrectVO();
            List<int[]> indexList = new ArrayList<>();
            int chk = 0;
            for (int j = 0; j < annotCorrectJsonNode.size(); j++) {

                if (annotCorrectJsonNode.get(j).has("group")
                        && chkUtteranceId == annotCorrectJsonNode.get(j).path("utteranceId").asInt()
                        && groupId.equals(annotCorrectJsonNode.get(j).path("group").asText())) {
                    if (chk == 0) {
                        annotCorrectVO.setMemo(annotCorrectJsonNode.get(j).path("memo").asText());
                        annotCorrectVO.setErrorCaseType(annotCorrectJsonNode.get(j).path("type").asText());
                        annotCorrectVO.setDetailType(annotCorrectJsonNode.get(j).path("detailType").asText());

                    }
                    if (annotCorrectJsonNode.get(j).has("inspectNote") && !annotCorrectJsonNode.get(j).path("inspectNote").asText().equals("")) {
                        annotCorrectVO.setInspectNote(annotCorrectJsonNode.get(j).path("inspectNote").asText());
                    }
                    int begin = annotCorrectJsonNode.get(j).path("begin").asInt();
                    int end = annotCorrectJsonNode.get(j).path("end").asInt();

                    indexList.add(new int[]{begin, end});

                    chk++;
                }
            }
            annotCorrectVO.setUnderLineIndexList(indexList);
            annotCorrectInfoMap.put(groupId, annotCorrectVO);

        }


        return annotCorrectInfoMap;
    }


}
