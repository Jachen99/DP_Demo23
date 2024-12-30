package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.FileInfo;

import java.util.List;

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    // 查询分表数据
    List<FileInfo> selectListFromTable(
            @Param("tableName") String tableName,
            @Param("userId") Long userId,
            @Param("fileName") String fileName,
            @Param("fileContent") String fileContent
    );
}
