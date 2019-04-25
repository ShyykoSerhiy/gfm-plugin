package com.github.shyykoserhiy.gfm.markdown.offline;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public interface MarkdownJna extends Library {
    Buffer markdownToHtml(String markdown, long markdownSize);

    public static class Buffer extends Structure {
        public Pointer data;		/* actual character data */
        public int size;	/* size of the string */
        public int asize;	/* allocated size (0 = volatile buffer) */
        public int unit;	/* reallocation unit size (0 = read-only buffer) */

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"data", "size", "asize", "unit"});
        }

        @Override
        public String toString() {
            long len = size;
            if (len != -1L) {
                if (len > 2147483647L) {
                    throw new OutOfMemoryError("String exceeds maximum length: " + len);
                }
                byte[] data = this.data.getByteArray(0, (int) len);
                String text;
                try {
                    text = new String(data, System.getProperty("jna.encoding", "utf8"));
                } catch (UnsupportedEncodingException e) {
                    text = new String(data);
                }
                return text;
            }
            return data.getString(0);
        }
    }
}
