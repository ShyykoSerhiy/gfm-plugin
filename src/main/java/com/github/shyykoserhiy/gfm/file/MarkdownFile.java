package com.github.shyykoserhiy.gfm.file;

public class MarkdownFile {
    public static final String[] MARKDOWN_EXTENSIONS = {
            "markdown",
            "mdown",
            "mkdn",
            "md",
            "mkd",
            "mdwn",
            "mdtxt",
            "mdtext",
            "text"
    };

    public static boolean isMarkdown(String extension){
        if (extension == null){
            return false;
        }
        for (String markdownExtension : MARKDOWN_EXTENSIONS) {
            if ( extension.equalsIgnoreCase(markdownExtension)){
                return true;
            }
        }
        return false;
    }
}
