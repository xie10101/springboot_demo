# SpringBoot_first_demo

Spring Boot 3 入门学习项目，基于 **MyBatis-Plus + MySQL + Flyway** 的 Student CRUD 管理模块，涵盖统一响应格式、全局异常处理、数据库版本迁移等企业级基础实践。

---

## 技术栈

| 技术 | 版本 | 用途 |
|---|---|---|
| Spring Boot | 3.3.1 | 基础框架 |
| Java | 17 | 编程语言 |
| MyBatis-Plus | 3.5.11 | ORM 框架（增强版 MyBatis） |
| MySQL | 8.x | 关系型数据库 |
| Flyway | (managed) | 数据库版本迁移 |
| Lombok | (managed) | 简化 POJO 代码 |
| Maven | 3.x | 项目构建 |

---

## 项目结构

```
demo/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── DemoApplication.java              # 入口
│   │   │   ├── common/
│   │   │   │   ├── Result.java                   # 统一响应包装类
│   │   │   │   ├── GlobalResponseAdvice.java     # 响应拦截器（自动包装）
│   │   │   │   └── GlobalExceptionHandler.java   # 全局异常处理器
│   │   │   └── modules/student/
│   │   │       ├── StudentController/
│   │   │       │   └── Controller.java           # REST 控制器（6个接口）
│   │   │       ├── entity/
│   │   │       │   └── Student.java              # 实体类
│   │   │       ├── mapper/
│   │   │       │   └── StudentMapper.java        # 数据访问层
│   │   │       └── server/
│   │   │           ├── StudentServer.java        # 服务接口
│   │   │           └── impl/
│   │   │               └── StudentServiceImpl.java  # 服务实现
│   │   └── resources/
│   │       ├── application.yml                   # 应用配置
│   │       └── db/migration/
│   │           ├── V1__init_student_table.sql    # Flyway 迁移1
│   │           └── V2__init_teacher_table.sql    # Flyway 迁移2
│   └── test/java/com/example/demo/
│       └── DemoApplicationTests.java             # 启动测试
```

---

## 学习要点

本项目由浅入深演进，按 git 提交顺序（`git log --oneline`）划分为 5 个阶段:

### 阶段1: 项目构建

> **提交:** `f255135` — feat: build

使用 **Spring Initializr** 生成 Maven 骨架项目，引入 Web、DevTools、Test 等基础依赖。学会：
- Maven 项目结构规范
- `pom.xml` 的 parent、dependency、plugin 配置
- Maven Wrapper（`mvnw`）的使用

### 阶段2: CRUD 模块搭建

> **提交:** `ac739fd` — 基础 student 模块创建-接口跑通

借助 **EasyCode** 插件生成 Entity → Mapper → Service → Controller 四层代码，使用 **ApiFox** 调试接口。学会：
- 经典分层架构：Controller（接口层）→ Service（业务层）→ Mapper（持久层）→ Database
- MyBatis-Plus 的 `BaseMapper<T>` 和 `IService<T>` 开箱即用的 CRUD
- `@TableName`、`@TableId(type = IdType.AUTO)` 注解配置表映射
- RESTful 风格接口设计（GET/POST/PUT/DELETE）

### 阶段3: 统一响应格式

> **提交:** `b71e6b2` — 响应拦截器统一返回格式

通过 `ResponseBodyAdvice` 实现 **AOP 风格的响应拦截**，自动将所有返回值包装为 `Result<T>`。学会：
- `Result<T>` 泛型响应类的设计模式（code + message + data）
- `ResponseBodyAdvice` 的 `supports()` / `beforeBodyWrite()` 生命周期
- `@ControllerAdvice` 的包范围控制（只拦截指定模块）
- 特殊类型的处理：`null`、`String`、`byte[]`、`Result`
- 防止重复包装的判断逻辑

### 阶段4: 全局异常处理

> **提交:** `88b049d` — feat: 补充错误响应模版

通过 `@RestControllerAdvice` + `@ExceptionHandler` 实现集中式异常管理。学会：
- Spring Boot 3.x 常见异常类型及其场景:
  - `Exception` → 500（兜底）
  - `RuntimeException` → 500（业务异常）
  - `MethodArgumentNotValidException` → 400（参数校验失败）
  - `HttpMessageNotReadableException` → 400（JSON 格式错误）
  - `HttpRequestMethodNotSupportedException` → 405（请求方法错误）
  - `NoHandlerFoundException` / `NoResourceFoundException` → 404（接口不存在）
- 所有异常统一返回 `Result<?>` 格式，前端只需解析一种结构
- 开启 `spring.mvc.throw-exception-if-no-handler-found=true` 才会触发 404 异常（配合关闭静态资源映射）

### 阶段5: 数据库版本迁移（Flyway）

> **提交:** `676cd95` — feat: flyway使用

引入 Flyway 管理数据库表结构变更，告别手动执行 SQL 脚本。学会：
- Flyway 版本命名规范：`V{版本号}__{描述}.sql`
- 迁移脚本放在 `classpath:db/migration` 下，启动时自动执行
- `flyway_schema_history` 表记录已执行的迁移版本
- `baseline-on-migrate: true` 用于已有数据库的首次接入

---

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.x（本地运行，默认端口 3306）
- Maven 3.x

### 1. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS sring_stu_demo
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;
```

### 2. 修改数据库连接

编辑 `src/main/resources/application.yml`，修改数据库用户名和密码：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sring_stu_demo
    username: root
    password: your_password
```

### 3. 启动项目

```bash
mvn spring-boot:run
```

启动后 Flyway 会自动执行 `db/migration/` 下的 SQL 脚本创建表结构。

### 4. 接口测试

项目运行在 `http://localhost:8080`，接口列表：

| 方法 | URL | 说明 |
|---|---|---|
| `POST` | `/student` | 新增学生 |
| `GET` | `/student/{id}` | 根据ID查询 |
| `GET` | `/student/list` | 查询全部 |
| `GET` | `/student/page?pageNum=1&pageSize=3` | 分页查询 |
| `PUT` | `/student` | 更新学生 |
| `DELETE` | `/student/{id}` | 删除学生 |

请求/响应示例:

```json
// POST /student 请求体
{
    "stuName": "张三",
    "age": 20,
    "clazz": "计算机一班",
    "phone": "13800138000"
}

// 统一响应格式
{
    "code": 200,
    "message": "success",
    "data": { ... }
}
```

---

## 数据库表结构

### student 表

| 字段 | 类型 | 说明 |
|---|---|---|
| id | INT (PK, AUTO_INCREMENT) | 主键 |
| fa_name | VARCHAR(50) | 学生姓名 |
| age | INT | 年龄 |
| clazz | VARCHAR(50) | 班级 |
| phone | VARCHAR(20) | 手机号 |

### teacher 表（已创建迁移，Java 代码待实现）

| 字段 | 类型 | 说明 |
|---|---|---|
| id | INT (PK, AUTO_INCREMENT) | 主键 |
| name | VARCHAR(50) | 教师姓名 |
| age | INT | 年龄 |
| clazz | VARCHAR(50) | 班级 |
| phone | VARCHAR(20) | 手机号 |

---

## 配置说明

```yaml
# Flyway 数据库迁移
spring.flyway:
  enabled: true                        # 启用 Flyway
  locations: classpath:db/migration    # 迁移脚本路径
  baseline-on-migrate: true            # 已有数据库时设置基线

# MyBatis-Plus
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL 日志输出
  global-config:
    db-config:
      id-type: auto                    # 全局主键自增

# 日志
logging:
  level:
    com.example.demo: debug            # 项目日志级别
    com.example.demo.modules.student.mapper: debug  # Mapper SQL 日志
  file:
    name: logs/demo.log                # 日志文件输出路径
```

---

## 架构图

```
┌──────────────────────────────────────────────────┐
│                    HTTP Request                   │
└──────────────────────┬───────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────┐
│  Controller  (@RestController)                   │
│  /student/*  请求分发                             │
└──────────────────────┬───────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────┐
│  Service  (extends IService<T>)                  │
│  业务逻辑层                                       │
└──────────────────────┬───────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────┐
│  Mapper  (extends BaseMapper<T>)                 │
│  数据访问层（MyBatis-Plus）                       │
└──────────────────────┬───────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────┐
│  MySQL  (via Flyway 管理表结构)                   │
└──────────────────────────────────────────────────┘

横切关注点:
  ├── GlobalResponseAdvice   → 拦截成功响应, 统一包装为 Result<T>
  └── GlobalExceptionHandler → 拦截异常, 统一转换为 Result<?>
```

---

## 常见问题

### Q: 启动报错 "Unknown column 'stu_name' in 'field list'"？

实体类 `Student` 的 `stuName` 字段与数据库列 `fa_name` 不匹配。MyBatis-Plus 默认将驼峰 `stuName` 转为下划线 `stu_name`，需要在字段上添加 `@TableField("fa_name")` 注解。

### Q: Flyway 报错 "Found non-empty schema without metadata table"？

加上 `spring.flyway.baseline-on-migrate: true` 配置，允许在非空数据库上初始化 Flyway。

### Q: DELETE 接口一直返回 500 错误？

Controller 中 `delete()` 方法硬编码了 `throw new RuntimeException("删除失败")`，这是学习用示例，实际使用时应改为 `studentServer.removeById(id)`。

---

## Git 提交历史

```
676cd95 feat: flyway使用
88b049d feat: 补充错误响应模版
b71e6b2 响应拦截器统一返回格式
ac739fd 基础student模块创建-接口跑通——easyCode+apifox
f255135 feat: build
```

---

## 待完善

- [ ] `teacher` 表的 Java 模块代码（Entity、Mapper、Service、Controller）
- [ ] 修复 `Student.stuName` 与数据库列 `fa_name` 的映射
- [ ] 移除 Controller 中的调试异常代码
- [ ] 添加参数校验（`@Valid`）
- [ ] 数据库密码外部化（环境变量 / Jasypt 加密）