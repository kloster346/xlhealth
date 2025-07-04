@echo off
echo ========================================
echo AI功能测试脚本
echo ========================================
echo.

echo 1. 运行AI服务单元测试...
echo ----------------------------------------
call mvn test -Dtest="AIServiceManagerTest" -q
if %errorlevel% neq 0 (
    echo [错误] AI服务单元测试失败
    pause
    exit /b 1
)
echo [成功] AI服务单元测试通过
echo.

echo 2. 运行AI服务集成测试...
echo ----------------------------------------
call mvn test -Dtest="AIServiceIntegrationTest" -q
if %errorlevel% neq 0 (
    echo [错误] AI服务集成测试失败
    pause
    exit /b 1
)
echo [成功] AI服务集成测试通过
echo.

echo 3. 运行AI接口测试...
echo ----------------------------------------
call mvn test -Dtest="MessageControllerAITest" -q
if %errorlevel% neq 0 (
    echo [错误] AI接口测试失败
    pause
    exit /b 1
)
echo [成功] AI接口测试通过
echo.

echo 4. 运行所有AI相关测试...
echo ----------------------------------------
call mvn test -Dtest="cn.xlhealth.backend.service.ai.*Test,MessageControllerAITest" -q
if %errorlevel% neq 0 (
    echo [错误] 部分AI测试失败
    pause
    exit /b 1
)
echo [成功] 所有AI测试通过
echo.

echo 5. 生成测试覆盖率报告...
echo ----------------------------------------
call mvn jacoco:report -q
if %errorlevel% neq 0 (
    echo [警告] 测试覆盖率报告生成失败
) else (
    echo [成功] 测试覆盖率报告已生成
    echo 报告位置: target\site\jacoco\index.html
)
echo.

echo ========================================
echo AI功能测试完成！
echo ========================================
echo.
echo 测试结果总结:
echo - AI服务单元测试: 通过
echo - AI服务集成测试: 通过  
echo - AI接口测试: 通过
echo - 所有AI测试: 通过
echo.
echo 接下来你可以:
echo 1. 查看测试覆盖率报告: target\site\jacoco\index.html
echo 2. 启动应用进行手动测试: mvn spring-boot:run -Dspring-boot.run.profiles=test
echo 3. 查看AI功能测试指南: docs\AI功能测试指南.md
echo.
pause