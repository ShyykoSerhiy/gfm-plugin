package com.github.shyykoserhiy.gfm.markdown;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by bribin.zheng on 2017-01-24.
 */
public class ImageUtilTest {

    ImageUtil imageUtil = new ImageUtil();

    @Test
    public void processImagesUrl() throws Exception {
        String text = imageUtil.processImagesUrl("## embed images in markdown\n"
                + "\n"
                + "Local image:\n"
                + "![mountain on sea](images\\test.png)\n"
                + "\n"
                + "Network image:\n"
                + "![google online image](https://www.google.com.hk/images/branding/googlelogo/2x/googlelogo_color_120x44dp.png)\n",
            "D:\\WS\\Demo");

        assertEquals("## embed images in markdown\n"
            + "\n"
            + "Local image:\n"
            + "![mountain on sea](file://D:/WS/Demo/images/test.png)\n"
            + "\n"
            + "Network image:\n"
            + "![google online image](https://www.google.com.hk/images/branding/googlelogo/2x/googlelogo_color_120x44dp.png)\n", text);


        text = imageUtil.processImagesUrl("## embed images in markdown\n"
            + "\n"
            + "Local image:\n"
            + "[mountain on sea](images\\test.png)\n"
            + "\n"
            + "Network image:\n"
            + "![google online image](https://www.google.com.hk/images/branding/googlelogo/2x/googlelogo_color_120x44dp.png)\n", "D:\\WS\\Demo");

        assertEquals("## embed images in markdown\n"
            + "\n"
            + "Local image:\n"
            + "[mountain on sea](images\\test.png)\n"
            + "\n"
            + "Network image:\n"
            + "![google online image](https://www.google.com.hk/images/branding/googlelogo/2x/googlelogo_color_120x44dp.png)\n", text);
    }

}