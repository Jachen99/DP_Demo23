# Spring Boot + MyBatis Plus + ShardingSphere + Elasticsearch + Nacos + Redisson

## 技术栈

1. **Spring Boot**: 版本 3.3 及以上
2. **MyBatis-Plus**: 简化 MyBatis 的持久层操作
3. **Nacos**: 用作注册中心和配置中心
4. **ShardingSphere**: 最新版本，支持分库分表
5. **JDK**: 版本 21
6. **OpenFeign**: 实现微服务之间的 HTTP 调用
7. **Elasticsearch**: 版本 8.12.2，用于全文检索和数据查询
8. **Redisson**: 实现分布式锁和缓存

---

## 应用说明

### 数据模型
**文件信息表字段：**
- `id` (主键)
- `userId` (用户ID)
- `fileName` (文件名)
- `fileContent` (文件内容)
- `fileSize` (文件大小)
- `status` (状态)
- `createTime` (创建时间)
- `updateTime` (更新时间)

---

## API 查询需求

1. **根据用户ID和文件名前缀查询**
    - 输入：`userId`, `fileNamePrefix`
    - 功能：查询指定用户所有匹配指定文件名前缀的文件信息。

2. **根据用户ID和文件内容词匹配查询**
    - 输入：`userId`, `fileContentKeyword`
    - 功能：查询指定用户所有包含特定关键词的文件内容。

3. **根据用户ID、文件名和文件内容前缀查询**
    - 输入：`userId`, `fileNamePrefix`, `fileContentPrefix`
    - 功能：查询指定用户所有符合文件名和文件内容前缀条件的文件信息。

---

## 脚手架搭建步骤

### 1. 项目初始化

#### 使用 Spring Initializr 创建项目：
- 添加以下依赖：
    - `Spring Boot Starter Web`
    - `Spring Boot Starter OpenFeign`
    - `Spring Boot Starter Data Elasticsearch`
    - `Spring Boot Starter Data Redis`
    - `MyBatis-Plus Boot Starter`
    - `Nacos Discovery`
    - `Nacos Config`
    - `ShardingSphere-JDBC`

### 2. 配置环境

#### **1. 数据库配置（ShardingSphere）**
- 数据库分片配置示例：
```yaml
spring:
  shardingsphere:
    datasource:
      names: ds0, ds1
      ds0:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        jdbc-url: jdbc:mysql://localhost:3306/database0
        username: root
        password: password
      ds1:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        jdbc-url: jdbc:mysql://localhost:3306/database1
        username: root
        password: password
    rules:
      sharding:
        tables:
          file_info:
            actual-data-nodes: ds$->{0..1}.file_info_$->{0..9}
            table-strategy:
              inline:
                sharding-column: userId
                algorithm-expression: file_info_$->{userId % 10}
```

#### **2. Elasticsearch 配置**
```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
```

#### **3. Nacos 配置**
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml
```

#### **4. Redisson 配置**
```yaml
spring:
  redis:
    host: localhost
    port: 6379

redisson:
  config: |
    singleServerConfig:
      address: "redis://localhost:6379"
```

---

### 3. 实现 API

#### **1. 文件信息服务接口**
```java
@RestController
@RequestMapping("/file-info")
public class FileInfoController {

    @Autowired
    private FileInfoService fileInfoService;

    @GetMapping("/query-by-name-prefix")
    public List<FileInfo> queryByNamePrefix(@RequestParam Long userId, @RequestParam String fileNamePrefix) {
        return fileInfoService.queryByNamePrefix(userId, fileNamePrefix);
    }

    @GetMapping("/query-by-content-keyword")
    public List<FileInfo> queryByContentKeyword(@RequestParam Long userId, @RequestParam String keyword) {
        return fileInfoService.queryByContentKeyword(userId, keyword);
    }

    @GetMapping("/query-by-name-and-content-prefix")
    public List<FileInfo> queryByNameAndContentPrefix(@RequestParam Long userId, @RequestParam String fileNamePrefix, @RequestParam String fileContentPrefix) {
        return fileInfoService.queryByNameAndContentPrefix(userId, fileNamePrefix, fileContentPrefix);
    }
}
```

#### **2. 文件信息服务实现**
```java
@Service
public class FileInfoService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public List<FileInfo> queryByNamePrefix(Long userId, String fileNamePrefix) {
        // Elasticsearch 查询实现
    }

    public List<FileInfo> queryByContentKeyword(Long userId, String keyword) {
        // Elasticsearch 查询实现
    }

    public List<FileInfo> queryByNameAndContentPrefix(Long userId, String fileNamePrefix, String fileContentPrefix) {
        // Elasticsearch 查询实现
    }
}
```

---

## 项目运行

1. 启动 Nacos 服务：
   ```bash
   sh startup.sh -m standalone
   ```

2. 启动 Elasticsearch：
   ```bash
   ./bin/elasticsearch
   ```

3. 配置数据库分片表结构并导入初始数据。

4. 启动 Spring Boot 应用。

---

## 项目目录结构
```
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── org.example
│   │   │   │   ├── controller
│   │   │   │   ├── service
│   │   │   │   ├── mapper
│   │   │   │   ├── entity
│   │   │   │   ├── config
│   │   │   │   └── util
│   │   ├── resources
│   │   │   ├── application.yml
│   │   │   ├── static
│   │   │   └── templates
├── pom.xml
```

---

## 依赖版本管理
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.3.0</spring-boot.version>
    <mybatis-plus.version>3.5.4.1</mybatis-plus.version>
    <shardingsphere.version>5.3.0</shardingsphere.version>
    <elasticsearch.version>8.12.2</elasticsearch.version>
    <nacos.version>2.3.0</nacos.version>
    <redisson.version>3.20.0</redisson.version>
</properties>
```

