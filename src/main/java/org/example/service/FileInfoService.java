package org.example.service;

import org.example.entity.FileInfo;
import org.example.mapper.FileInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileInfoService {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    // 分表的规则：假设每 10 个用户分一个表
    private static final int SHARD_SIZE = 10;

    // 根据 userId 获取对应的分表名
    private String getShardedTableName(Long userId) {
        int tableIndex = (int) (userId % SHARD_SIZE);
        String tableName = "file_info_" + tableIndex;
        validateTableName(tableName); // 校验表名合法性
        return tableName;
    }

    // 校验表名合法性，避免 SQL 注入
    private void validateTableName(String tableName) {
        if (!tableName.matches("^file_info_\\d+$")) {
            throw new IllegalArgumentException("非法的表名: " + tableName);
        }
    }

    // 使用用户ID和文件名前缀查询
    public List<FileInfo> findByUserIdAndFileNamePrefix(Long userId, String fileNamePrefix) {
        String tableName = getShardedTableName(userId);
        return fileInfoMapper.selectListFromTable(tableName, userId, fileNamePrefix + "%", null);
    }

    // 使用用户ID和文件内容词匹配查询
    public List<FileInfo> findByUserIdAndFileContent(Long userId, String fileContent) {
        String tableName = getShardedTableName(userId);
        return fileInfoMapper.selectListFromTable(tableName, userId, null, "%" + fileContent + "%");
    }

    // 使用用户ID、文件名和文件内容前缀查询
    public List<FileInfo> findByUserIdFileNameAndContentPrefix(Long userId, String fileNamePrefix, String contentPrefix) {
        String tableName = getShardedTableName(userId);
        return fileInfoMapper.selectListFromTable(tableName, userId, fileNamePrefix + "%", contentPrefix + "%");
    }
}
