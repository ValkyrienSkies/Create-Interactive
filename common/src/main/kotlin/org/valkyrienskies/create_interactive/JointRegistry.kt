package org.valkyrienskies.create_interactive

import java.util.WeakHashMap

/** Accessed on the game thread, including completions dispatched from the physics thread. */
internal class JointRegistry<T : Any> {
    private val joints = WeakHashMap<T, Int>()
    private val pending = WeakHashMap<T, Any>()

    fun get(owner: T): Int? = joints[owner]

    fun begin(owner: T): Any? {
        if (owner in joints || owner in pending) return null
        return Any().also { pending[owner] = it }
    }

    /** Returns false when the created joint must be deleted rather than attached to this owner. */
    fun complete(owner: T, request: Any, id: Int, keep: Boolean): Boolean {
        if (pending[owner] !== request) return false
        pending.remove(owner)
        if (keep) joints[owner] = id
        return keep
    }

    fun remove(owner: T): Int? {
        pending.remove(owner)
        return joints.remove(owner)
    }
}
