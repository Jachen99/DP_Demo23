package org.example.controller;

import org.example.entity.FileInfo;
import org.example.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/file-info")
public class FileInfoController {

    @Autowired
    private FileInfoService fileInfoService;

    // 使用用户ID和文件名前缀查询
    @GetMapping("/search/by-user-and-filename-prefix")
    public List<FileInfo> findByUserIdAndFileNamePrefix(@RequestParam Long userId, @RequestParam String fileNamePrefix) {
        return fileInfoService.findByUserIdAndFileNamePrefix(userId, fileNamePrefix);
    }

    // 使用用户ID和文件内容词匹配查询
    @GetMapping("/search/by-user-and-filecontent")
    public List<FileInfo> findByUserIdAndFileContent(@RequestParam Long userId, @RequestParam String fileContent) {
        return fileInfoService.findByUserIdAndFileContent(userId, fileContent);
    }

    // 使用用户ID、文件名和文件内容前缀查询
    @GetMapping("/search/by-user-filename-content-prefix")
    public List<FileInfo> findByUserIdFileNameAndContentPrefix(@RequestParam Long userId, @RequestParam String fileNamePrefix, @RequestParam String contentPrefix) {
        return fileInfoService.findByUserIdFileNameAndContentPrefix(userId, fileNamePrefix, contentPrefix);
    }
}
