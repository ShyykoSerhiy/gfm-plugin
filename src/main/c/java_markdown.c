#include <ctype.h>
#include <stdio.h>
#include "markdown.h"
#include "html.h"
#include "houdini.h"

static struct {
    struct sd_markdown *md;
    struct html_renderopt render_opts;
} g_markdown;

/* Init the default pipeline */
static void ghmd_init_md(void);

static struct buf *ghmd_to_html(char const *text, const size_t textSize);

/**
* returns html. NOTE that buffer NEEDS to be deallocated using bufrelease(output_buf);!!!
*/
struct buf *markdownToHtml(const char *text, const size_t textSize) {
    if (g_markdown.md == NULL) {
        ghmd_init_md();
    }
    return ghmd_to_html(text, textSize);
}

/* Max recursion nesting when parsing Markdown documents */
static const int GITHUB_MD_NESTING = 32;

/* Default flags for all Markdown pipelines:
 *
 *	- NO_INTRA_EMPHASIS: disallow emphasis inside of words
 *	- LAX_SPACING: Do spacing like in Markdown 1.0.0 (i.e.
 *		do not require an empty line between two different
 *		blocks in a paragraph)
 *	- STRIKETHROUGH: strike out words with `~~`, same semantics
 *		as emphasis
 *	- TABLES: the tables extension from PHP-Markdown extra
 *	- FENCED_CODE: the fenced code blocks extension from
 *		PHP-Markdown extra, but working with ``` besides ~~~.
 *	- AUTOLINK: Well. That. Link stuff automatically.
 */
static const unsigned int GITHUB_MD_FLAGS =
        MKDEXT_NO_INTRA_EMPHASIS |
                MKDEXT_LAX_SPACING |
                MKDEXT_STRIKETHROUGH |
                MKDEXT_TABLES |
                MKDEXT_FENCED_CODE |
                MKDEXT_AUTOLINK;

static void rndr_blockcode_github(struct buf *ob, const struct buf *text, const struct buf *lang, void *opaque) {
    if (ob->size)
        bufputc(ob, '\n');

    if (!text || !text->size) {
        BUFPUTSL(ob, "<pre><code></code></pre>");
        return;
    }

    if (lang && lang->size) {
        size_t i = 0, lang_size;
        const char *lang_name = NULL;

        while (i < lang->size && !isspace(lang->data[i]))
            i++;

        if (lang->data[0] == '.') {
            lang_name = (char const *) (lang->data + 1);
            lang_size = i - 1;
        } else {
            lang_name = (char const *) lang->data;
            lang_size = i;
        }

        BUFPUTSL(ob, "<pre lang=\"");
        houdini_escape_html0(ob, (uint8_t const *) lang_name, lang_size, 0);
        BUFPUTSL(ob, "\"><code>");

    } else {
        BUFPUTSL(ob, "<pre><code>");
    }

    houdini_escape_html0(ob, text->data, text->size, 0);
    BUFPUTSL(ob, "</code></pre>\n");
}

/* Init the default pipeline */
static void ghmd_init_md(void) {
    struct sd_callbacks callbacks;

    /* No extra flags to the Markdown renderer */
    sdhtml_renderer(&callbacks, &g_markdown.render_opts, 0);
    callbacks.blockcode = &rndr_blockcode_github;

    g_markdown.md = sd_markdown_new(
            GITHUB_MD_FLAGS,
            GITHUB_MD_NESTING,
            &callbacks,
            &g_markdown.render_opts
    );
}

/**
* returns html. NOTE that buffer NEEDS to be deallocated using bufrelease(output_buf);!!!
*/
static struct buf *ghmd_to_html(char const *text, const size_t textSize) {
    struct buf *output_buf;
    struct sd_markdown *md = g_markdown.md;

    /* initialize buffers */
    output_buf = bufnew(256);

    /* render the magic */
    sd_markdown_render(output_buf, text, textSize, md);

    //printf((char const *) output_buf->data);
    //printf("size %d\n", (int) output_buf->size);
    //printf("asize %d\n", (int) output_buf->asize);

    return output_buf;
}