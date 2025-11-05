package org.valkyrienskies.create_interactive.mixin_logic.deployer

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions
import com.simibubi.create.foundation.gui.AllIcons
import net.createmod.catnip.lang.Lang
import org.valkyrienskies.create_interactive.CreateInteractiveIcons
import org.valkyrienskies.create_interactive.services.NoOptimize

enum class DeployerActorMode(icon: AllIcons) : INamedIconOptions {
    ACTOR_ON(CreateInteractiveIcons.I_COMEDY),
    ACTOR_OFF(CreateInteractiveIcons.I_TRAGEDY);

    private var translationKey: String
    private var icon: AllIcons

    init {
        this.icon = icon
        translationKey = "deployer.actor_mode." + Lang.asId(name)
    }

    @NoOptimize
    override fun getIcon(): AllIcons? {
        return icon
    }

    @NoOptimize
    override fun getTranslationKey(): String? {
        return translationKey
    }
}