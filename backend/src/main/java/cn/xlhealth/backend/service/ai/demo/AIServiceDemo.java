package cn.xlhealth.backend.service.ai.demo;

import cn.xlhealth.backend.service.ai.AIServiceManager;
import cn.xlhealth.backend.service.ai.dto.AIRequest;
import cn.xlhealth.backend.service.ai.dto.AIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Scanner;

/**
 * AI服务演示类
 * 提供命令行交互界面来演示AI服务的功能
 * 
 * 启用方式：在application.yml中设置 ai.demo.enabled=true
 */
@Component
@ConditionalOnProperty(name = "ai.demo.enabled", havingValue = "true")
public class AIServiceDemo implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AIServiceDemo.class);

    @Autowired
    private AIServiceManager aiServiceManager;

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== XLHealth AI服务演示程序 ===");
        logger.info("输入 'help' 查看可用命令，输入 'quit' 退出程序");

        Scanner scanner = new Scanner(System.in);
        String userId = "demo-user-" + System.currentTimeMillis();
        String conversationId = "demo-conv-" + System.currentTimeMillis();

        while (true) {
            System.out.print("\nAI演示> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            if ("quit".equalsIgnoreCase(input) || "exit".equalsIgnoreCase(input)) {
                System.out.println("再见！");
                break;
            }

            try {
                handleCommand(input, userId, conversationId);
            } catch (Exception e) {
                System.err.println("处理命令时发生错误: " + e.getMessage());
                logger.error("命令处理错误", e);
            }
        }

        scanner.close();
    }

    private void handleCommand(String input, String userId, String conversationId) {
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();

        switch (command) {
            case "help":
                showHelp();
                break;

            case "chat":
                if (parts.length < 2) {
                    System.out.println("用法: chat <消息内容>");
                    return;
                }
                handleChat(parts[1], userId, conversationId);
                break;

            case "emotion":
                if (parts.length < 2) {
                    System.out.println("用法: emotion <情感状态> <消息内容>");
                    return;
                }
                handleEmotionChat(parts[1], userId, conversationId);
                break;

            case "health":
                showHealthStatus();
                break;

            case "stats":
                showStats();
                break;

            case "config":
                showConfig();
                break;

            case "summary":
                showContextSummary(userId, conversationId);
                break;

            case "clear":
                clearContext(userId, conversationId);
                break;

            case "reset":
                resetStats();
                break;

            case "test":
                runTestScenarios(userId, conversationId);
                break;

            default:
                // 默认作为聊天消息处理
                handleChat(input, userId, conversationId);
                break;
        }
    }

    private void showHelp() {
        System.out.println("\n=== 可用命令 ===");
        System.out.println("chat <消息>        - 发送聊天消息");
        System.out.println("emotion <情感> <消息> - 带情感状态的聊天");
        System.out.println("health            - 显示服务健康状态");
        System.out.println("stats             - 显示服务统计信息");
        System.out.println("config            - 显示服务配置");
        System.out.println("summary           - 显示对话摘要");
        System.out.println("clear             - 清除对话上下文");
        System.out.println("reset             - 重置统计信息");
        System.out.println("test              - 运行测试场景");
        System.out.println("help              - 显示此帮助信息");
        System.out.println("quit/exit         - 退出程序");
        System.out.println("\n注意：也可以直接输入消息内容进行聊天");
    }

    private void handleChat(String message, String userId, String conversationId) {
        handleChatWithEmotion(message, "中性", userId, conversationId);
    }

    private void handleEmotionChat(String input, String userId, String conversationId) {
        String[] parts = input.split("\\s+", 2);
        if (parts.length < 2) {
            System.out.println("用法: emotion <情感状态> <消息内容>");
            System.out.println("可用情感状态: 焦虑, 抑郁, 愤怒, 恐惧, 平静, 快乐, 中性");
            return;
        }

        String emotion = parts[0];
        String message = parts[1];
        handleChatWithEmotion(message, emotion, userId, conversationId);
    }

    private void handleChatWithEmotion(String message, String emotion, String userId, String conversationId) {
        System.out.println("\n用户 [" + emotion + "]: " + message);

        AIRequest request = new AIRequest();
        // 将String类型的userId和conversationId转换为Long类型
        try {
            request.setUserId(Long.parseLong(userId.replaceAll("[^0-9]", "")));
        } catch (NumberFormatException e) {
            request.setUserId(1L); // 默认用户ID
        }

        try {
            request.setConversationId(Long.parseLong(conversationId.replaceAll("[^0-9]", "")));
        } catch (NumberFormatException e) {
            request.setConversationId(1L); // 默认对话ID
        }

        request.setUserMessage(message);
        request.setEmotionalState(emotion);
        request.setTimestamp(System.currentTimeMillis());

        long startTime = System.currentTimeMillis();
        AIResponse response = aiServiceManager.processRequest(request);
        long endTime = System.currentTimeMillis();

        if (response.isSuccess()) {
            System.out.println("AI [" + response.getReplyType() + "]: " + response.getContent());
            System.out.println("质量评分: " + response.getQualityScore() + "/100");
            System.out.println("响应时间: " + (endTime - startTime) + "ms");
            System.out.println("服务提供商: " + response.getProvider());
        } else {
            System.err.println("AI回复失败: " + response.getContent());
        }
    }

    private void showHealthStatus() {
        System.out.println("\n=== 服务健康状态 ===");
        boolean healthy = aiServiceManager.isHealthy();
        System.out.println("整体健康状态: " + (healthy ? "健康" : "不健康"));

        java.util.Map<String, cn.xlhealth.backend.service.ai.monitor.ServiceStats> stats = aiServiceManager
                .getServiceStats();
        for (java.util.Map.Entry<String, cn.xlhealth.backend.service.ai.monitor.ServiceStats> entry : stats
                .entrySet()) {
            cn.xlhealth.backend.service.ai.monitor.ServiceStats stat = entry.getValue();
            System.out.println("\n提供商: " + entry.getKey());
            System.out.println("  总调用次数: " + stat.getTotalCalls());
            System.out.println("  成功率: " + String.format("%.2f%%", stat.getSuccessRate() * 100));
            System.out.println("  平均响应时间: " + String.format("%.0fms", stat.getAverageResponseTime()));
            System.out.println("  错误率: " + String.format("%.2f%%", stat.getErrorRate() * 100));
        }
    }

    private void showStats() {
        System.out.println("\n=== 服务统计信息 ===");
        java.util.Map<String, cn.xlhealth.backend.service.ai.monitor.ServiceStats> stats = aiServiceManager
                .getServiceStats();

        if (stats.isEmpty()) {
            System.out.println("暂无统计数据");
            return;
        }

        for (java.util.Map.Entry<String, cn.xlhealth.backend.service.ai.monitor.ServiceStats> entry : stats
                .entrySet()) {
            cn.xlhealth.backend.service.ai.monitor.ServiceStats stat = entry.getValue();
            System.out.println("\n=== " + entry.getKey() + " 提供商统计 ===");
            System.out.println("总调用次数: " + stat.getTotalCalls());
            System.out.println("成功调用: " + stat.getSuccessfulCalls());
            System.out.println("失败调用: " + stat.getFailedCalls());
            System.out.println("成功率: " + String.format("%.2f%%", stat.getSuccessRate() * 100));
            System.out.println("错误率: " + String.format("%.2f%%", stat.getErrorRate() * 100));
            System.out.println("平均响应时间: " + String.format("%.0fms", stat.getAverageResponseTime()));
            System.out.println("最大响应时间: " + stat.getMaxResponseTime() + "ms");
            System.out.println("最小响应时间: " + stat.getMinResponseTime() + "ms");
            System.out.println("平均质量评分: " + String.format("%.1f", stat.getAverageQualityScore()));
            System.out.println("最后更新时间: " + new java.util.Date(stat.getLastUpdated()));
        }
    }

    private void showConfig() {
        System.out.println("\n=== 服务配置信息 ===");
        Map<String, Object> config = aiServiceManager.getConfigInfo();

        for (Map.Entry<String, Object> entry : config.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void showContextSummary(String userId, String conversationId) {
        System.out.println("\n=== 对话摘要 ===");
        String summary = aiServiceManager.getContextSummary(userId, conversationId);

        if (summary != null && !summary.trim().isEmpty()) {
            System.out.println(summary);
        } else {
            System.out.println("暂无对话历史");
        }
    }

    private void clearContext(String userId, String conversationId) {
        aiServiceManager.clearUserContext(userId, conversationId);
        System.out.println("\n对话上下文已清除");
    }

    private void resetStats() {
        aiServiceManager.resetServiceStats(null);
        System.out.println("\n统计信息已重置");
    }

    private void runTestScenarios(String userId, String conversationId) {
        System.out.println("\n=== 运行测试场景 ===");

        String[] testMessages = {
                "我感到很焦虑，不知道该怎么办",
                "最近工作压力很大，经常失眠",
                "我觉得自己很失败，什么都做不好",
                "有什么方法可以帮助我放松吗？",
                "谢谢你的建议，我感觉好多了"
        };

        String[] emotions = { "焦虑", "压力", "抑郁", "求助", "感谢" };

        for (int i = 0; i < testMessages.length; i++) {
            System.out.println("\n--- 测试场景 " + (i + 1) + " ---");
            handleChatWithEmotion(testMessages[i], emotions[i], userId, conversationId);

            // 添加延迟以模拟真实对话
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("\n=== 测试完成 ===");
        showStats();
    }
}