package com.github.shyykoserhiy.gfm.markdown;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bribin.zheng on 2017-01-24.
 */
public class ImageUtil {

    private static Pattern IMG_PATTERN = Pattern.compile("!\\[([^]]*)]\\(([^)]+)\\)");

    public String processImagesUrl(String markdown, String parentFolder) {
        Matcher matcher = IMG_PATTERN.matcher(markdown);
        StringBuffer buf = new StringBuffer();
        while (matcher.find()) {
            String newImg = "![" + matcher.group(1) + "](" + appendParentPath(parentFolder, matcher.group(2)) + ")";
            matcher.appendReplacement(buf, newImg);
        }
        matcher.appendTail(buf);
        return buf.toString();
    }

    private String appendParentPath(String parentFolder, String src) {
        if (src.indexOf("://") > 0) {
            return src;
        }

        src = "file://" + parentFolder + "/" + src;
        src = src.replaceAll("\\\\", "/");
        return src;
    }

}
