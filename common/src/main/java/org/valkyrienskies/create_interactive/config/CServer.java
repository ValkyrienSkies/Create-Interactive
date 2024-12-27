package org.valkyrienskies.create_interactive.config;

import com.simibubi.create.foundation.config.ConfigBase;

@SuppressWarnings("unused")
public class CServer extends ConfigBase {

    //Just to explain the modes. I've put 1 in front of readforinfo to get it to appear first
    public final ConfigInt readForInfo = i(123, "1readForInfo", Comments.readForInfo);


    public final ConfigEnum<InteractiveHandling> trainHandling = e(InteractiveHandling.WITH_STICKER, "trainHandling", Comments.trainHandling);
    public final ConfigEnum<InteractiveHandling> translatingHandling = e(InteractiveHandling.WITH_STICKER, "translatingHandling", Comments.translatingHandling);
    public final ConfigEnum<InteractiveHandling> bearingHandling = e(InteractiveHandling.WITH_STICKER, "bearingHandling", Comments.bearingHandling);
    public final ConfigEnum<InteractiveHandling> clockworkHandling = e(InteractiveHandling.WITH_STICKER, "clockworkHandling", Comments.clockworkHandling);
    public final ConfigEnum<InteractiveHandling> mountedHandling = e(InteractiveHandling.WITH_STICKER, "mountedHandling", Comments.mountedHandling);
    public final ConfigEnum<InteractiveHandling> otherHandling = e(InteractiveHandling.NEVER, "otherHandling", Comments.otherHandling);



    @Override
    public String getName() {
        return "interactive_server";
    }

    private static class Comments {
        static String readForInfo = "ALWAYS will ignore all stickers and always be interactive. " +
                "WITHOUT STICKER will be interactive unless an interact-me-not is used. " +
                "WITH STICKER will only be interactive if an interact-me is used. " +
                "NEVER will ignore all stickers and never be interactive. "+
                "(Note: this config value doesn't do anything)";

        static String trainHandling = "How trains are handled for interactivity";
        static String translatingHandling = "How translating contraptions are handled for interactivity (Piston, Gantry, Pulley)";
        static String bearingHandling = "How bearing contraptions are handled for interactivity";
        static String clockworkHandling = "How clockwork contraptions are handled for interactivity (Flaps, etc)";
        static String mountedHandling = "How mounted contraptions are handled for interactivity (minecart)";
        static String otherHandling = "How any other contraptions are handled for interactivity (create addons, etc)";

    }
}
