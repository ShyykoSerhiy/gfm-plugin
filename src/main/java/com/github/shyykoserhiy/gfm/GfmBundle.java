package com.github.shyykoserhiy.gfm;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

public class GfmBundle {
    private static final String BUNDLE_NAME = "com.github.shyykoserhiy.gfm.localization.en";

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * Load a {@link String} from the {@link #BUNDLE} {@link ResourceBundle}.
     *
     * @param key    the key of the resource.
     * @param params the optional parameters for the specific resource.
     * @return the {@link String} value or {@code null} if no resource found for the key.
     */
    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return CommonBundle.message(BUNDLE, key, params);
    }
}
