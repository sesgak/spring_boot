package com.example.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.example.application.service.UserApplicationService;
import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import com.example.form.UserListForm;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserListController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserApplicationService appService;

    @GetMapping("/list")
    public String getUserList(@ModelAttribute UserListForm form, Model model){

        MUser user = modelMapper.map(form, MUser.class);

        List<MUser> userList = userService.getUsers(user);

        model.addAttribute("userList", userList);

        return "user/list";
    }

    @PostMapping("/list")
    public String postUserList(@ModelAttribute UserListForm form, Model model){

        MUser user = modelMapper.map(form, MUser.class);

        List<MUser> userList = userService.getUsers(user);

        model.addAttribute("userList", userList);

        return "user/list";
    }

    @PostMapping("/list/download")
    public ResponseEntity<byte[]> downloadUserList(@ModelAttribute UserListForm form) throws IOException {
        // form???MUser??????????????????
        MUser user = modelMapper.map(form, MUser.class);
        // ??????????????????
        List<MUser> userList = userService.getUsers(user);
        // CSV??????????????????
        String fileName = "user.csv";
        appService.saveUserCsv(userList, fileName);
        // CSV??????????????????
        byte[] bytes = appService.getCsv(fileName);
        HttpHeaders header = new HttpHeaders();
        // HTTP?????????????????????
        header.add("Content-Type", MediaType.ALL_VALUE + "; charset=utf-8");
        header.setContentDispositionFormData("filename", fileName);
        return new ResponseEntity<>(bytes, header, HttpStatus.OK);
    }
    /** zip???????????????????????????????????? */
    @PostMapping("/list/download/zip")
    public void downloadZip(@ModelAttribute UserListForm form, HttpServletResponse response) throws IOException {
        // form???MUser??????????????????
        MUser user = modelMapper.map(form, MUser.class);
        // ??????????????????
        List<MUser> userList = userService.getUsers(user);
        List<String> fileNameList = new ArrayList<>();
        // ????????????CSV??????????????????
        String userFileName = "user.csv";
        appService.saveUserCsv(userList, userFileName);
        fileNameList.add(userFileName);
        // ??????CSV??????????????????
        String departmentFileName = "department.csv";
        appService.saveDepartmentCsv(userList, departmentFileName);
        fileNameList.add(departmentFileName);
        // ??????????????????
        String zipFileName = "sample.zip";
        response.setHeader(HttpHeaders.CONTENT_TYPE,
        MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + zipFileName);
        // zip??????????????????????????????
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (String fileName : fileNameList) {
                try (InputStream is = appService.getInputStream(fileName)) {
                    // zip?????????????????????
                    zos.putNextEntry(new ZipEntry(fileName));
                    StreamUtils.copy(is, zos);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }    
}