package org.example.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自动任务 定时创建分表
 */
@Service
@EnableScheduling
public class AutoShardingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 分表的阈值
    private static final int THRESHOLD = 10;

    @PostConstruct
    public void init() {
        try {
            System.out.println("AutoShardingService 正在初始化...");
            ensureThresholdTables(); // 启动时确保表数量满足阈值
            checkAndCreateTable();   // 再次检查是否需要创建额外的表
        } catch (Exception e) {
            System.err.println("AutoShardingService 初始化时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 定时任务，每小时检查一次表数情况
    @Scheduled(cron = "0 0 * * * ?")  // 每小时执行一次
    public void checkAndCreateTable() {
        try {
            System.out.println("正在检查分表状态...");
            // 获取当前的分表情况
            List<String> existingTables = getExistingTables();
            int currentTableCount = existingTables.size();

            System.out.println("当前分表数量：" + currentTableCount);

            if (currentTableCount >= THRESHOLD) {
                String newTableName = "file_info_" + currentTableCount;

                if (!existingTables.contains(newTableName)) {
                    // 创建新的分表
                    createNewShardedTable(newTableName);
                } else {
                    System.out.println("分表 " + newTableName + " 已存在，跳过创建。");
                }
            } else {
                System.out.println("分表数量未达到阈值，无需创建新表。");
            }
        } catch (Exception e) {
            System.err.println("定时检查分表时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 查询当前数据库中已有的分表
    private List<String> getExistingTables() {
        try {
            String sql = "SHOW TABLES LIKE 'file_info_%'";
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (Exception e) {
            System.err.println("查询已有分表时发生错误：" + e.getMessage());
            throw e;
        }
    }

    // 创建新的分表（根据 FileInfo 类字段创建表结构）
    private void createNewShardedTable(String tableName) {
        try {
            // 如果表已经存在，跳过创建
            if (isTableExists(tableName)) {
                System.out.println("分表 " + tableName + " 已存在，跳过创建。");
                return;
            }

            String createTableSql = getCreateTableSql(tableName); // 获取动态创建的表语句
            jdbcTemplate.execute(createTableSql);
            System.out.println("成功创建新分表：" + tableName);
        } catch (Exception e) {
            System.err.println("创建分表 " + tableName + " 时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 检查表是否已存在
    private boolean isTableExists(String tableName) {
        try {
            String sql = "SHOW TABLES LIKE '" + tableName + "'";
            List<String> result = jdbcTemplate.queryForList(sql, String.class);
            return !result.isEmpty(); // 如果结果不为空，说明表已经存在
        } catch (Exception e) {
            System.err.println("检查表 " + tableName + " 是否存在时发生错误：" + e.getMessage());
            return false;
        }
    }

    // 根据 FileInfo 类的字段结构构建 CREATE TABLE 语句
    private String getCreateTableSql(String tableName) {
        // 根据 FileInfo 类的字段手动构建 SQL 语句
        String createTableSql = String.format(
                "CREATE TABLE %s ( " +
                        "id BIGINT PRIMARY KEY, " +
                        "userId BIGINT, " +
                        "fileName VARCHAR(255), " +
                        "fileContent TEXT, " +
                        "fileSize BIGINT, " +
                        "status VARCHAR(50), " +
                        "createTime DATETIME, " +
                        "updateTime DATETIME " +
                        ");", tableName
        );

        return createTableSql;
    }

    // 确保分表数量满足阈值
    private void ensureThresholdTables() {
        try {
            // 查询现有分表
            List<String> existingTables = getExistingTables();
            int currentTableCount = existingTables.size();

            System.out.println("初始化时当前分表数量：" + currentTableCount);

            // 补充创建缺失的分表
            for (int i = currentTableCount; i < THRESHOLD; i++) {
                String newTableName = "file_info_" + i;

                if (!existingTables.contains(newTableName)) {
                    // 创建新表
                    createNewShardedTable(newTableName);
                    System.out.println("初始化时创建分表：" + newTableName);
                }
            }
        } catch (Exception e) {
            System.err.println("初始化时确保分表数量时发生错误：" + e.getMessage());
            throw e;
        }
    }
}
