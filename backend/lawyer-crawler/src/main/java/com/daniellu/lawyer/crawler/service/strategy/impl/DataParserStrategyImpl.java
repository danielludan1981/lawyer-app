package com.daniellu.lawyer.crawler.service.strategy.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daniellu.lawyer.crawler.constant.SelectorMetadataKey;
import com.daniellu.lawyer.crawler.service.parser.DataParser;
import com.daniellu.lawyer.crawler.service.strategy.DataParserStrategy;

/**
 * 数据解析器策略实现类
 * 实现根据元数据选择合适的数据解析器的逻辑
 *
 * @author Daniel Lu
 * @since 2026-01-17
 */
@Component
public class DataParserStrategyImpl implements DataParserStrategy {

    private final List<DataParser> parsers;

    /**
     * 注入所有DataParser实现类
     *
     * @param parsers 所有DataParser实现类的列表
     */
    @Autowired
    public DataParserStrategyImpl(List<DataParser> parsers) {
        this.parsers = parsers;
    }

    @Override
    public DataParser selectParser(Map<String, Object> metadata, Object data) {
        // 存储匹配的解析器
        List<DataParser> candidateParsers = new ArrayList<>();

        // 先判断是否有指定数据解析器名称
        String specifiedParserName = metadata != null ?
            (String) metadata.get(SelectorMetadataKey.USER_DATA_PARSER) : null;

        if (specifiedParserName != null && !specifiedParserName.trim().isEmpty()) {
            // 有指定解析器名称，仅根据名称选择解析器
            for (DataParser parser : parsers) {
                if (specifiedParserName.equals(parser.getName())) {
                    candidateParsers.add(parser);
                }
            }
        } else {
            // 没有指定解析器名称，根据解析器的support方法筛选
            for (DataParser parser : parsers) {
                if (parser.supports(metadata, data)) {
                    candidateParsers.add(parser);
                }
            }
        }

        // 根据候选解析器列表，判断该如何返回
        if (candidateParsers.isEmpty()) {
            // 候选解析器列表为空，抛出异常，提示用户检查解析器配置
            throw new IllegalStateException(
                    "No matching parser found for metadata: " + metadata + " and data: " + data + ". " +
                    "Please check your parser configuration."
            );
        } else if (candidateParsers.size() == 1) {
            // 候选解析器列表只有一个解析器，直接返回该解析器
            return candidateParsers.get(0);
        } else {
            // 候选解析器列表有多个解析器，通过匹配度进一步筛选
            return selectParserByMatchScore(candidateParsers, metadata, data);
        }
    }

    /**
     * 通过匹配度选择解析器
     * 当筛选出多个DataParser后，判断每个Parser返回的getMatchScore值
     * 候选列表仅保留匹配度最高的解析器
     *
     * @param candidateParsers 候选解析器列表
     * @param metadata 元数据
     * @param data 数据
     * @return 选择的解析器
     */
    private DataParser selectParserByMatchScore(List<DataParser> candidateParsers, Map<String, Object> metadata, Object data) {
        DataParser bestParser = null;
        int bestScore = -1;

        // 遍历所有候选的解析器，找到匹配度最高的
        for (DataParser parser : candidateParsers) {
            int score = parser.getMatchScore(metadata, data);
            if (score > bestScore) {
                bestScore = score;
                bestParser = parser;
            }
        }

        // 检查是否有多个解析器具有相同的最高匹配度
        int sameScoreCount = 0;
        for (DataParser parser : candidateParsers) {
            if (parser.getMatchScore(metadata, data) == bestScore) {
                sameScoreCount++;
            }
        }

        // 如果有多个解析器具有相同的最高匹配度，抛出异常
        if (sameScoreCount > 1) {
            throw new IllegalStateException(
                    "Found multiple parsers with the same highest match score (" + bestScore + ") for metadata: " + metadata + " and data: " + data + ". " +
                    "Matched parsers: " + candidateParsers + ". " +
                    "Please check your parser configuration to ensure unique URL prefixes."
            );
        }

        return bestParser;
    }
}
