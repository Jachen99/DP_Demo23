package org.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("file_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {

    @TableId
    private Long id;

    private Long userId;

    private String fileName;

    private String fileContent;

    private Long fileSize;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
