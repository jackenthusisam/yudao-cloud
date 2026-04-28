# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Project Overview

yudao-cloud is a Spring Cloud Alibaba microservices-based enterprise admin system. The `master` branch uses JDK 8 + Spring Boot 2.7, while `master-jdk17` uses JDK 17/21 + Spring Boot 3.2.

## Build Commands

```bash
# Full build (skip tests for speed)
mvn clean install -DskipTests

# Build specific module
mvn clean install -DskipTests -pl yudao-module-system/yudao-module-system-server -am

# Run tests for a module
mvn test -pl yudao-module-system/yudao-module-system-server

# Run single test class
mvn test -pl yudao-module-system/yudao-module-system-server -Dtest=AdminUserServiceImplTest

# Package without tests
mvn package -DskipTests -pl yudao-server -am
```

## Architecture

### Module Structure
```
yudao-module-xxx/
├── yudao-module-xxx-api/      # Interfaces, DTOs, enums - published as dependency
└── yudao-module-xxx-server/   # Implementation: controller, service, dal, framework
```

### Each Module Follows Three-Tier Architecture
- **controller**: REST endpoints (`/admin-api/` prefix for admin APIs)
- **service**: Business logic
- **dal**: Data Access Layer (MyBatis Plus mappers, DO entities)

### Key Modules
- `yudao-dependencies` - Maven BOM for version management
- `yudao-framework` - Spring Boot starters (security, redis, mybatis, job, mq, etc.)
- `yudao-server` - Main application aggregating all modules
- `yudao-gateway` - Spring Cloud Gateway

## Code Conventions

### Package Structure
```
cn.iocoder.yudao.module.{system}.{controller,service,dal,convert,framework}
```

### Common Patterns
- **VO/DTO/BO**: Controller receive VO, return DTO, internal uses BO
- **MapStruct**: Bean mapping via `*ConvertImpl` classes in `convert` package
- **Logical Deletion**: Entities use `deleted` column (1=deleted, 0=normal)
- **Multi-tenancy**: Enabled by default, tenant_id on all tenant tables
- **Lombok**: All entities use `@Data`, `@Builder`, etc.

### REST API Prefix
- Admin APIs: `/admin-api/{module}/{resource}/`
- App APIs: `/app-api/{module}/{resource}/`

### Testing
- Base test class: `cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest`
- Uses H2 embedded database for unit tests
- Mock external dependencies with `@MockBean`

## Database

### SQL Scripts Location
- `sql/mysql/ruoyi-vue-pro.sql` - Main schema
- `sql/mysql/quartz.sql` - Quartz scheduler tables

### Configuration
- Default: MySQL at `127.0.0.1:3306`
- Also supports PostgreSQL, Oracle, SQL Server, DM, Kingbase, OpenGauss
- Uses dynamic-datasource for multi-datasource support
