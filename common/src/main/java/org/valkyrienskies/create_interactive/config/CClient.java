package org.valkyrienskies.create_interactive.config;

import net.createmod.catnip.config.ConfigBase;

@SuppressWarnings("unused")
public class CClient extends ConfigBase {

    public final ConfigBool doesNothing = b(false, "doesNothing", Comments.doesNothing);

    @Override
    public String getName() {
        return "interactive_client";
    }

    private static class Comments {
        static String doesNothing = "Placeholder";

    }
}
