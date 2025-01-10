package org.valkyrienskies.create_interactive.config;

import com.simibubi.create.foundation.config.ConfigBase;

@SuppressWarnings("unused")
public class CClient extends ConfigBase {

    public final ConfigBool disableChatWarning = b(false, "disableChatWarning", Comments.disableChatWarning);

    @Override
    public String getName() {
        return "interactive_client";
    }

    private static class Comments {
        static String disableChatWarning = "Disable warning message in chat on loading a world";
    }
}
