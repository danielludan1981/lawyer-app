package com.daniellu.lawyer.crawler.service.parser;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.daniellu.lawyer.crawler.constant.SelectorMetadataKey;

/**
 * 数据解析器抽象类
 * 实现DataParser接口，提供默认实现
 *
 * @author Daniel Lu
 * @since 2026-01-17
 */
public abstract class AbstractDataParser implements DataParser {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 默认返回解析器的类名
     */
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * 返回解析器支持的URL前缀
     * 默认返回null
     *
     * @return 解析器支持的URL前缀，null表示支持所有URL
     */
    protected String getUrlPrefix() {
        return null;
    }

    /**
     * 默认的supports方法实现
     * 从metadata中获取url字段，判断是否以getUrlPrefix()返回的前缀开头
     * 忽略http和https，只要url包含前缀即可
     *
     * @param metadata 元数据，包含额外的参考参数
     * @param data 基础爬取数据
     * @return 是否支持当前metadata和数据
     */
    @Override
    public boolean supports(Map<String, Object> metadata, Object data) {
        if (metadata == null) {
            return false;
        }

        // 从metadata中获取url字段
        Object urlObj = metadata.get(SelectorMetadataKey.SYS_URL);
        if (urlObj == null) {
            return false;
        }

        String url = urlObj.toString();
        String urlPrefix = getUrlPrefix();
        if (urlPrefix == null) {
            // 没有指定URL前缀，不支持任何URL
            return false;
        }

        // 忽略http和https，只要url包含前缀即可
        String urlWithoutProtocol = url.replaceFirst("^https?:\\/\\/", "");
        String prefixWithoutProtocol = urlPrefix.replaceFirst("^https?:\\/\\/", "");

        return urlWithoutProtocol.contains(prefixWithoutProtocol);
    }

    /**
     * 获取匹配度
     * 根据传入的metadata传入的url key，如有协议头去除协议头(http或https)，返回与getUrlPrefix()返回的前缀撇配的字符数
     *
     * @param metadata 元数据
     * @param data 数据
     * @return 匹配度, 值越高证明约匹配
     */
    @Override
    public int getMatchScore(Map<String, Object> metadata, Object data) {
        if (metadata == null) {
            return 0;
        }

        // 从metadata中获取url字段
        Object urlObj = metadata.get(SelectorMetadataKey.SYS_URL);
        if (urlObj == null) {
            return 0;
        }

        String url = urlObj.toString();
        String urlPrefix = getUrlPrefix();
        if (urlPrefix == null) {
            // 没有指定URL前缀，返回0匹配度
            return 0;
        }

        // 忽略http和https，计算匹配的字符数
        String urlWithoutProtocol = url.replaceFirst("^https?:\\/\\/", "");
        String prefixWithoutProtocol = urlPrefix.replaceFirst("^https?:\\/\\/", "");

        // 计算匹配的字符数
        int matchScore = 0;
        for (int i = 0; i < Math.min(urlWithoutProtocol.length(), prefixWithoutProtocol.length()); i++) {
            if (urlWithoutProtocol.charAt(i) == prefixWithoutProtocol.charAt(i)) {
                matchScore++;
            } else {
                break;
            }
        }

        return matchScore;
    }

    /**
     * 带有metadata的parse方法，供DataExtractor调用
     *
     * @param data 基础数据，由DataExtractor提取
     * @param metadata 元数据，包含额外的参考参数
     * @return 处理后的结果
     */
    @Override
    public Object parse(Object data, Map<String, Object> metadata) {
        if (data == null) {
            logger.warn("Data is null, returning null");
            return null;
        }

        // 根据data的类型进行解析
        if (data instanceof String) {
            // 如果data是String类型，表示返回的是整个html，直接返回data
            return data;
        } else if (data instanceof Map) {
            // 如果data是Map类型，转型为Map<String, Object>, 调用parseMap方法
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) data;
            return parseMap(dataMap, metadata);
        } else if (data instanceof List) {
            // 如果data是List类型，转型为List<Map<String, Object>>, 调用parseList方法
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) data;
            return parseList(dataList, metadata);
        } else {
            logger.warn("Unsupported data type: {}, returning null", data.getClass().getName());
            return null;
        }
    }

    /**
     * 解析单个Map数据
     * 子类需实现
     *
     * @param data 单个Map数据
     * @param metadata 元数据
     * @return 处理后的结果
     */
    protected abstract Object parseMap(Map<String, Object> data, Map<String, Object> metadata);

    /**
     * 解析List数据
     * 子类需实现
     *
     * @param data List数据
     * @param metadata 元数据
     * @return 处理后的结果
     */
    protected abstract List<Map<String, Object>> parseList(List<Map<String, Object>> data, Map<String, Object> metadata);
}
