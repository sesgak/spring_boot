package com.example.rest;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestResult {
    
    private int result;

    /** エラー マップ
     *  key: フィールド 名
     *  value: エラーメッセージ
    */ 
    private Map<String, String> errors;

}
