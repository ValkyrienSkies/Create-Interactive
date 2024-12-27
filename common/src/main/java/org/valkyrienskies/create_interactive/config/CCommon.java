package org.valkyrienskies.create_interactive.config;

import com.simibubi.create.foundation.config.ConfigBase;

@SuppressWarnings("unused")
public class CCommon extends ConfigBase {

    public final ConfigBool doesNothing = b(false, "doesNothing", Comments.doesNothing);

    @Override
    public String getName() {
        return "interactive_common";
    }

    private static class Comments {
        static String doesNothing = "Placeholder";
    }
}
