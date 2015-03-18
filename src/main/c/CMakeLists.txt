cmake_minimum_required(VERSION 2.8.4)
project(github-markdown)

add_library(markdown SHARED java_markdown.c markdown.c autolink.c buffer.c houdini_href_e.c houdini_html_e.c html.c plaintext.c stack.c)

#http://crossgcc.rts-software.org/download/gcc-4.8.0-qt-4.8.4-win32/gcc-4.8.0-qt-4.8.4-for-mingw32.dmg
#http://crossgcc.rts-software.org/doku.php?id=compiling_for_linux