package org.elasticsearch.plugin.policy.sp.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {
    private final static Base64.Decoder decoder = Base64.getDecoder();
    private final static Base64.Encoder encoder = Base64.getEncoder();
    private static String charsetName="UTF-8";;

    // base64 编码
    public static String encodeStr(String string) throws UnsupportedEncodingException {
        byte[] textByte = string.getBytes(charsetName);
        return encoder.encodeToString(textByte);
    }

    // base64 解码
    public static String decodeStr(String encodedStr) throws UnsupportedEncodingException {
        return new String(decoder.decode(encodedStr), charsetName);
    }

}
