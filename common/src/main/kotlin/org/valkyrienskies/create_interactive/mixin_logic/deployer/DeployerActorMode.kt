package org.valkyrienskies.create_interactive.mixin_logic.deployer

import com.simibubi.create.content.contraptions.IControlContraption
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions
import com.simibubi.create.foundation.gui.AllIcons
import com.simibubi.create.foundation.utility.Lang
import org.valkyrienskies.create_interactive.CreateInteractiveIcons

enum class DeployerActorMode(icon: AllIcons) : INamedIconOptions {
    ACTOR_ON(AllIcons.I_CONFIRM),
    ACTOR_OFF(AllIcons.I_DISABLE),
    ;

    private var translationKey: String
    private var icon: AllIcons

    init {
        this.icon = icon
        translationKey = "deployer.actor_mode." + Lang.asId(name)
    }

    override fun getIcon(): AllIcons? {
        return icon
    }

    override fun getTranslationKey(): String? {
        return translationKey
    }
}