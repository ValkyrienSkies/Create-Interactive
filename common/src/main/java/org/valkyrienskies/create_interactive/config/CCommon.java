package org.valkyrienskies.create_interactive.config;


import net.createmod.catnip.config.ConfigBase;

@SuppressWarnings("unused")
public class CCommon extends ConfigBase {

    public final ConfigBool disableChatWarning = b(false, "disableChatWarning", Comments.disableChatWarning);


    @Override
    public String getName() {
        return "interactive_common";
    }

    private static class Comments {
        static String disableChatWarning = "Disable warning message in chat on loading a world";

    }
}
