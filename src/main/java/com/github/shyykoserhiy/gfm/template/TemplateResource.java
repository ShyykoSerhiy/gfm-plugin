package com.github.shyykoserhiy.gfm.template;

enum TemplateResource {
    GFM("/com/github/shyykoserhiy/gfm/html/markdown.html"), ERROR("/com/github/shyykoserhiy/gfm/html/error.html");
    private String resource;

    TemplateResource(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
