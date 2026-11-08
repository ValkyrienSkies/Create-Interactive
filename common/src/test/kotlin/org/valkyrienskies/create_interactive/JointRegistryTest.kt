package org.valkyrienskies.create_interactive

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JointRegistryTest {
    @Test
    fun `only one joint can be pending or attached per carriage`() {
        val registry = JointRegistry<Any>()
        val carriage = Any()
        val request = registry.begin(carriage)!!
        assertNull(registry.begin(carriage))
        assertTrue(registry.complete(carriage, request, 42, true))
        assertEquals(42, registry.get(carriage))
        assertNull(registry.begin(carriage))
        assertEquals(42, registry.remove(carriage))
        assertNull(registry.get(carriage))
        assertNotNull(registry.begin(carriage))
    }

    @Test
    fun `removal rejects a joint that finishes after re-railing or unloading`() {
        val registry = JointRegistry<Any>()
        val carriage = Any()
        val request = registry.begin(carriage)!!
        assertNull(registry.remove(carriage))
        assertFalse(registry.complete(carriage, request, 42, true))
        assertNull(registry.get(carriage))
    }

    @Test
    fun `stale completion cannot consume a newer derailment request`() {
        val registry = JointRegistry<Any>()
        val carriage = Any()
        val old = registry.begin(carriage)!!
        registry.remove(carriage)
        val current = registry.begin(carriage)!!
        assertFalse(registry.complete(carriage, old, 41, true))
        assertTrue(registry.complete(carriage, current, 42, true))
        assertEquals(42, registry.get(carriage))
    }

    @Test
    fun `invalid owner completion releases the pending request`() {
        val registry = JointRegistry<Any>()
        val carriage = Any()
        val request = registry.begin(carriage)!!
        assertFalse(registry.complete(carriage, request, 42, false))
        assertNull(registry.get(carriage))
        assertNotNull(registry.begin(carriage))
    }
}
