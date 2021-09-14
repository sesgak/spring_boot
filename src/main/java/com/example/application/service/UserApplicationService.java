package com.example.application.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;

import com.example.domain.user.model.MUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ResourceLoader resourceLoader;

    private String filePath = "C:\\Users\\shige\\Downloads\\test";

    private static final String SEPARATOR = File.separator;

    public Map<String, Integer> getGenderMap(Locale locale){
        Map<String, Integer> genderMap = new LinkedHashMap<>();
        String male = messageSource.getMessage("male", null, locale);
        String female = messageSource.getMessage("female", null, locale);
        genderMap.put(male,1);
        genderMap.put(female,2);
        return genderMap;
    }

    public void saveUserCsv(List<MUser> userList, String fileName) throws IOException{
        StringBuilder sb = new StringBuilder();
        for (MUser user : userList){
            sb.append(user.toCsv());
        }
        Path path = Paths.get(filePath + SEPARATOR + fileName);

        byte[] bytes = sb.toString().getBytes();

        Files.write(path, bytes);
    }

    public byte[] getCsv(String fileName) throws IOException{

        String path = "file:" + filePath + SEPARATOR + fileName;

        Resource resource = resourceLoader.getResource(path);
        File file = resource.getFile();

        return Files.readAllBytes(file.toPath());
    }

    /** 部署リストのCSVを作成する */
    public void saveDepartmentCsv(List<MUser> userList, String fileName) throws IOException {
        // CSV文字列作成
        StringBuilder sb = new StringBuilder();
        for (MUser user : userList) {
            sb.append(user.getDepartment().toCsv());
        }
        // ファイル保存先パス作成
        Path path = Paths.get(filePath + SEPARATOR + fileName);
        // byte配列作成
        byte[] bytes = sb.toString().getBytes();
        // ファイル書込
        Files.write(path, bytes);
    }
    /** InputStream取得 */
    public InputStream getInputStream(String fileName) throws IOException {
        // パス
        String path = "file:" + filePath + SEPARATOR + fileName;
        // Resource取得
        Resource resource = resourceLoader.getResource(path);
        // InputStream取得
        return resource.getInputStream();
    }

}
