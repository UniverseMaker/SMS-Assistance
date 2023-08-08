package com.dspark.smsassistance.utility;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public class ProcessString {
    public static String UTF8Cutter(String s, int n) {
        try {
            final Charset CHARSET = Charset.forName("UTF-8"); // or any other charset
            final byte[] bytes = s.getBytes(CHARSET);
            final CharsetDecoder decoder = CHARSET.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.IGNORE);
            decoder.reset();
            final CharBuffer decoded = decoder.decode(ByteBuffer.wrap(bytes, 0, n));
            final String outputString = decoded.toString();
            return outputString;
        }catch (Exception e){
            return null;
        }
    }
}
