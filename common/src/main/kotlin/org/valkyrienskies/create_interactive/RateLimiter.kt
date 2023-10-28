package org.valkyrienskies.create_interactive

import java.time.Duration
import java.time.Instant

/**
 * Rate limiter that simply ignores calls if they are happening too fast
 */
class RateLimiter(private val minTimeSincePrev: Duration) {
    private var lastCall: Instant = Instant.MIN

    fun maybeRun(block: Runnable) {
        val now = Instant.now()
        if (Duration.between(lastCall, now) > minTimeSincePrev) {
            block.run()
            lastCall = now
        }
    }
}
