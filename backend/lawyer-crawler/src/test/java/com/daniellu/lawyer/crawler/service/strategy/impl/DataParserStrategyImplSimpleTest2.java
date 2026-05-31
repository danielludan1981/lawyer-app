package com.daniellu.lawyer.crawler.service.strategy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.daniellu.lawyer.crawler.constant.SelectorMetadataKey;
import com.daniellu.lawyer.crawler.service.parser.DataParser;

/**
 * DataParserStrategyImpl 增强功能测试（简化版）
 * 不依赖自定义类，直接测试核心逻辑
 *
 * @author Daniel Lu
 * @since 2026-01-25
 */
public class DataParserStrategyImplSimpleTest2 {

    public static void main(String[] args) {
        System.out.println("=== DataParserStrategyImpl 增强功能测试（简化版）===\n");

        // 创建模拟的解析器
        List<DataParser> parsers = new ArrayList<>();

        // 创建模拟解析器1 - 支持example.com
        parsers.add(new DataParser() {
            @Override
            public String getName() {
                return "ExampleParser";
            }

            @Override
            public boolean supports(Map<String, Object> metadata, Object data) {
                if (metadata == null) return false;
                String url = (String) metadata.get(SelectorMetadataKey.SYS_URL);
                return url != null && url.contains("example.com");
            }

            @Override
            public int getMatchScore(Map<String, Object> metadata, Object data) {
                if (metadata == null) return 0;
                String url = (String) metadata.get(SelectorMetadataKey.SYS_URL);
                if (url == null || !url.contains("example.com")) return 0;
                return "example.com".length(); // 简化匹配度计算
            }

            @Override
            public Object parse(Object data, Map<String, Object> metadata) {
                return "Parsed by " + getName();
            }
        });

        // 创建模拟解析器2 - 支持test.example.com
        parsers.add(new DataParser() {
            @Override
            public String getName() {
                return "TestExampleParser";
            }

            @Override
            public boolean supports(Map<String, Object> metadata, Object data) {
                if (metadata == null) return false;
                String url = (String) metadata.get(SelectorMetadataKey.SYS_URL);
                return url != null && url.contains("test.example.com");
            }

            @Override
            public int getMatchScore(Map<String, Object> metadata, Object data) {
                if (metadata == null) return 0;
                String url = (String) metadata.get(SelectorMetadataKey.SYS_URL);
                if (url == null || !url.contains("test.example.com")) return 0;
                return "test.example.com".length(); // 简化匹配度计算
            }

            @Override
            public Object parse(Object data, Map<String, Object> metadata) {
                return "Parsed by " + getName();
            }
        });

        // 创建模拟解析器3 - noop解析器
        parsers.add(new DataParser() {
            @Override
            public String getName() {
                return "noop";
            }

            @Override
            public boolean supports(Map<String, Object> metadata, Object data) {
                if (metadata == null) return false;
                String parserName = (String) metadata.get(SelectorMetadataKey.USER_DATA_PARSER);
                return "noop".equals(parserName);
            }

            @Override
            public int getMatchScore(Map<String, Object> metadata, Object data) {
                return 0; // noop解析器返回0
            }

            @Override
            public Object parse(Object data, Map<String, Object> metadata) {
                return data; // noop解析器直接返回原数据
            }
        });

        DataParserStrategyImpl dataParserStrategy = new DataParserStrategyImpl(parsers);

        // 测试1: 指定解析器名称匹配
        testSpecifiedParserNameMatch(dataParserStrategy);

        // 测试2: 指定解析器名称无匹配
        testSpecifiedParserNameNoMatch(dataParserStrategy);

        // 测试3: 指定noop解析器
        testSpecifiedNoopParser(dataParserStrategy);

        // 测试4: 无指定解析器名称，使用support方法
        testSupportMethodSelection(dataParserStrategy);

        System.out.println("=== 所有测试完成 ===");
    }

    private static void testSpecifiedParserNameMatch(DataParserStrategyImpl dataParserStrategy) {
        System.out.println("测试1: 指定解析器名称匹配");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(SelectorMetadataKey.USER_DATA_PARSER, "TestExampleParser");
        Object data = "test data";

        DataParser result = dataParserStrategy.selectParser(metadata, data);

        System.out.println("指定解析器名称: TestExampleParser");
        System.out.println("选择的解析器: " + (result != null ? result.getName() : "null"));
        System.out.println("预期: TestExampleParser");
        System.out.println("结果: " + ("TestExampleParser".equals(result.getName()) ? "✓ 通过" : "✗ 失败"));
        System.out.println();
    }

    private static void testSpecifiedParserNameNoMatch(DataParserStrategyImpl dataParserStrategy) {
        System.out.println("测试2: 指定解析器名称无匹配");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(SelectorMetadataKey.USER_DATA_PARSER, "NonExistentParser");
        Object data = "test data";

        try {
            DataParser result = dataParserStrategy.selectParser(metadata, data);
            System.out.println("指定解析器名称: NonExistentParser");
            System.out.println("结果: 应该抛出异常，但没有抛出");
            System.out.println("结果: ✗ 失败");
        } catch (IllegalStateException e) {
            System.out.println("指定解析器名称: NonExistentParser");
            System.out.println("异常信息: " + e.getMessage());
            System.out.println("结果: ✓ 通过 (正确抛出异常)");
        }
        System.out.println();
    }

    private static void testSpecifiedNoopParser(DataParserStrategyImpl dataParserStrategy) {
        System.out.println("测试3: 指定noop解析器");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(SelectorMetadataKey.USER_DATA_PARSER, "noop");
        Object data = "test data";

        DataParser result = dataParserStrategy.selectParser(metadata, data);

        System.out.println("指定解析器名称: noop");
        System.out.println("选择的解析器: " + (result != null ? result.getName() : "null"));
        System.out.println("预期: noop");
        System.out.println("结果: " + ("noop".equals(result.getName()) ? "✓ 通过" : "✗ 失败"));
        System.out.println();
    }

    private static void testSupportMethodSelection(DataParserStrategyImpl dataParserStrategy) {
        System.out.println("测试4: 无指定解析器名称，使用support方法");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(SelectorMetadataKey.SYS_URL, "https://test.example.com/path");
        Object data = "test data";

        DataParser result = dataParserStrategy.selectParser(metadata, data);

        System.out.println("URL: https://test.example.com/path");
        System.out.println("选择的解析器: " + (result != null ? result.getName() : "null"));
        System.out.println("预期: TestExampleParser (最高匹配度)");
        System.out.println("结果: " + ("TestExampleParser".equals(result.getName()) ? "✓ 通过" : "✗ 失败"));
        System.out.println();
    }
}