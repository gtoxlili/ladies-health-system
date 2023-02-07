package com.system.ladiesHealth.utils.deserializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

public class EmptyToNull extends StringDeserializer {
    @Override
    public String deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        String result = super.deserialize(parser, ctx);
        if (StrUtil.isBlank(result)) {
            return null;
        }
        return result;
    }
}
