package com.daniellu.lawyer.crawler.service.strategy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.daniellu.lawyer.crawler.constant.SelectorMetadataKey;
import com.daniellu.lawyer.crawler.service.parser.DataParser;
import com.daniellu.lawyer.crawler.service.parser.impl.TestDataParser;
import com.daniellu.lawyer.crawler.service.parser.impl.NoopDataParserImpl;

/**
 * DataParserStrategyImpl 简单测试类
 * 不依赖JUnit和Mockito，使用main方法进行测试
 *
 * @author Daniel Lu
 * @since 2026-01-25
 */
public class DataParserStrategyImplSimpleTest {

    public static void main(String[] args) {
        System.out.println("=== DataParserStrategyImpl 增强功能测试 ===\n");

        // 创建测试用的解析器，不同的URL前缀
        List<DataParser> parsers = new ArrayList<>();
        parsers.add(new TestDataParser("example.com", "ExampleParser"));
        parsers.add(new TestDataParser("test.example.com", "TestExampleParser"));
        parsers.add(new TestDataParser("api.example.com", "ApiExampleParser"));
        parsers.add(new TestDataParser("other.com", "OtherParser"));
        parsers.add(new NoopDataParserImpl());

        DataParserStrategyImpl dataParserStrategy = new DataParserStrategyImpl(parsers);

        // 测试1: 精确匹配 - 最高分数
        testExactMatch(dataParserStrategy);

        // 测试2: 部分匹配 - 中等分数
        testPartialMatch(dataParserStrategy);

        // 测试3: 通用匹配 - 最低分数
        testGeneralMatch(dataParserStrategy);

        // 测试4: 无匹配
        testNoMatch(dataParserStrategy);

        // 测试5: 相同匹配度异常
        testSameMatchScoreException();

        // 测试6: 匹配度计算
        testMatchScoreCalculation();

        // 测试7: 指定解析器名称匹配
        testSpecifiedParserNameMatch(dataParserStrategy);

        // 测试8: 指定解析器名称无匹配
        testSpecifiedParserNameNoMatch(dataParserStrategy);

        // 测试9: 指定noop解析器
        testSpecifiedNoopParser(dataParserStrategy);

        System.out.println("=== 所有测试完成 ===");
    }

    private static void testExactMatch(DataParserStrategyImpl dataParserStrategy) {
        System.out.println("测试1: 精确匹配 - 最高分数");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(SelectorMetadataKey.SYS_URL, "https://test.example.com/path");
        Object data = "test data";

        DataParser result = dataParserStrategy.selectParser(metadata, data);

        System.out.println("URL: https://test.example.com/path");
        System.out.println("选择的解析器: " + (result != null ? result.getName() : "null"));
        System.out.println("预期: TestExampleParser");
        System.out.println("结果: " + ("TestExampleParser".equals(result.getName()) ? "✓ 通过" : "✗ 失败"));
        System.out.println();
    }

    private static void testPartialMatch(DataParserStrategyImpl dataParserStrategy) {
        System.out.println("测试2: 部分匹配 - 中等分数");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(SelectorMetadataKey.SYS_URL, "https://api.example.com/path");
        Object data = "test data";

        DataParser result = dataParserStrategy.selectParser(metadata, data);

        System.out.println("URL: https://api.example.com/path");
        System.out.println("选择的解析器: " + (result != null ? result.getName() : "null"));
        System.out.println("预期: ApiExampleParser");
        System.out.println("结果: " + ("ApiExampleParser".equals(result.getName()) ? "✓ 通过" : "✗ 失败"));
        System.out.println();
    }

    private static void testGeneralMatch(DataParserStrategyImpl dataParserStrategy) {
        System.out.println("测试3: 通用匹配 - 最低分数");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(SelectorMetadataKey.SYS_URL, "https://example.com/path");
        Object data = "test data";

        DataParser result = dataParserStrategy.selectParser(metadata, data);

        System.out.println("URL: https://example.com/path");
        System.out.println("选择的解析器: " + (result != null ? result.getName() : "null"));
        System.out.println("预期: ExampleParser");
        System.out.println("结果: " + ("ExampleParser".equals(result.getName()) ? "✓ 通过" : "✗ 失败"));
        System.out.println();
    }

    private static void testNoMatch(DataParserStrategyImpl dataParserStrategy) {
        System.out.println("测试4: 无匹配");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(SelectorMetadataKey.SYS_URL, "https://nomatch.com/path");
        Object data = "test data";

        DataParser result = dataParserStrategy.selectParser(metadata, data);

        System.out.println("URL: https://nomatch.com/path");
        System.out.println("选择的解析器: " + (result != null ? result.getName() : "null"));
        System.out.println("预期: null");
        System.out.println("结果: " + (result == null ? "✓ 通过" : "✗ 失败"));
        System.out.println();
    }

    private static void testSameMatchScoreException() {
        System.out.println("测试5: 相同匹配度异常");

        // 创建两个具有相同URL前缀的解析器
        List<DataParser> samePrefixParsers = new ArrayList<>();
        samePrefixParsers.add(new TestDataParser("same.com", "SameParser1"));
        samePrefixParsers.add(new TestDataParser("same.com", "SameParser2"));

        DataParserStrategyImpl samePrefixStrategy = new DataParserStrategyImpl(samePrefixParsers);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(SelectorMetadataKey.SYS_URL, "https://same.com/path");
        Object data = "test data";

        try {
            DataParser result = samePrefixStrategy.selectParser(metadata, data);
            System.out.println("URL: https://same.com/path");
            System.out.println("结果: 应该抛出异常，但没有抛出");
            System.out.println("结果: ✗ 失败");
        } catch (IllegalStateException e) {
            System.out.println("URL: https://same.com/path");
            System.out.println("异常信息: " + e.getMessage());
            System.out.println("结果: ✓ 通过 (正确抛出异常)");
        }
        System.out.println();
    }

    private static void testMatchScoreCalculation() {
        System.out.println("测试6: 匹配度计算");

        TestDataParser parser = new TestDataParser("example.com", "TestParser");

        // 测试不同的URL
        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put(SelectorMetadataKey.SYS_URL, "https://example.com/path");
        int score1 = parser.getMatchScore(metadata1, "test data");

        Map<String, Object> metadata2 = new HashMap<>();
        metadata2.put(SelectorMetadataKey.SYS_URL, "https://test.example.com/path");
        int score2 = parser.getMatchScore(metadata2, "test data");

        Map<String, Object> metadata3 = new HashMap<>();
        metadata3.put(SelectorMetadataKey.SYS_URL, "https://example.org/path");
        int score3 = parser.getMatchScore(metadata3, "test data");

        Map<String, Object> metadata4 = new HashMap<>();
        metadata4.put(SelectorMetadataKey.SYS_URL, "http://example.com/path");
        int score4 = parser.getMatchScore(metadata4, "test data");

        System.out.println("URL: https://example.com/path, 匹配度: " + score1 + " (预期: 11)");
        System.out.println("URL: https://test.example.com/path, 匹配度: " + score2 + " (预期: 0)");
        System.out.println("URL: https://example.org/path, 匹配度: " + score3 + " (预期: 8)");
        System.out.println("URL: http://example.com/path, 匹配度: " + score4 + " (预期: 11)");

        boolean allCorrect = score1 == 11 && score2 == 0 && score3 == 8 && score4 == 11;
        System.out.println("结果: " + (allCorrect ? "✓ 通过" : "✗ 失败"));
        System.out.println();
    }

    private static void testSpecifiedParserNameMatch(DataParserStrategyImpl dataParserStrategy) {
        System.out.println("测试7: 指定解析器名称匹配");

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
        System.out.println("测试8: 指定解析器名称无匹配");

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
        System.out.println("测试9: 指定noop解析器");

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
}