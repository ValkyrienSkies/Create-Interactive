package org.valkyrienskies.create_interactive.ponders;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import org.valkyrienskies.create_interactive.GameContent;

// I couldn't get this class to cooperate in kotlin, since static function are different ig
public class PonderTags {
    public static final PonderTag
    INTERACTIVE_PONDERS = create("interactive_ponders").item(GameContent.INTERACT_ME.get())
            .defaultLang("Create: Interactive", "Mechanics and features of Create: Interactive")
            .addToIndex();

    private static PonderTag create(String id) {
        return new PonderTag(Create.asResource(id));
    }

    public static void register() {
        PonderRegistry.TAGS.forTag(INTERACTIVE_PONDERS)
                .add(GameContent.INTERACT_ME.getId())
                .add(GameContent.INTERACT_ME_NOT.getId())
                .add(GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK.getId());
    }
}
