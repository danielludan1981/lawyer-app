package com.daniellu.lawyer.common.util;

import java.net.URI;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * URI工具类，提供URI相关的工具方法
 * <p>
 * 根据JDK25的特点进行了重构，使用了更现代的Java API和异常处理方式
 * </p>
 *
 * @author Daniel Lu
 * @since JDK25
 */
public final class URIUtil {

    private static final Logger logger = Logger.getLogger(URIUtil.class.getName());

    /**
     * 私有构造方法，防止实例化
     */
    private URIUtil() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    /**
     * 解析相对URL，将其与基础URL组合成完整的URL
     * <p>
     * 该方法会处理以下情况：
     * 1. 如果相对URL为null或空，则返回基础URL
     * 2. 如果相对URL已经是完整的URL（以http://或https://开头），则直接返回
     * 3. 否则，将相对URL与基础URL组合成完整的URL
     * </p>
     *
     * @param baseUrl     基础URL
     * @param relativeUrl 相对URL
     * @return 解析后的完整URL
     */
    public static String resolveUrl(String baseUrl, String relativeUrl) {
        // 使用Objects.requireNonNullElse确保baseUrl不为null
        String normalizedBaseUrl = Objects.requireNonNullElse(baseUrl, "");

        // 如果相对URL为null或空，则返回基础URL
        if (relativeUrl == null || relativeUrl.isBlank()) {
            return normalizedBaseUrl;
        }

        // 如果已经是完整的url，直接返回
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }

        try {
            // 使用URI类的create和resolve方法组合URL
            URI baseUri = URI.create(normalizedBaseUrl);
            URI resolvedUri = baseUri.resolve(relativeUrl);
            return resolvedUri.toString();
        } catch (IllegalArgumentException _) {
            // 使用Logger记录警告日志，包含基础URL和相对URL
            logger.warning(() -> String.format("Failed to resolve url: baseUrl=%s, relativeUrl=%s", normalizedBaseUrl, relativeUrl));
            // 返回基础URL作为回退
            return normalizedBaseUrl;
        }
    }
}
