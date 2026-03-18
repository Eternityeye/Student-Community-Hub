package com.ruoyi.common.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

/**
 * 字符串转列表反序列化器
 * 用于将逗号分隔的字符串或数组转换为 List<String>
 */
public class StringToListDeserializer extends JsonDeserializer<List<String>>
{
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        if (p.currentToken().isStructStart()) {
            // 如果已经是数组，直接使用默认反序列化
            return ctxt.readValue(p, List.class);
        }

        // 如果是字符串，按逗号分隔
        String value = p.getValueAsString();
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        return Arrays.stream(value.split(","))
            .filter(StringUtils::isNotEmpty)
            .map(String::trim)
            .collect(Collectors.toList());
    }
}
