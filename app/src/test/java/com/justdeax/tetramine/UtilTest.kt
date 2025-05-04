package com.justdeax.tetramine

import com.justdeax.tetramine.util.getStatistics
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilTest {
    @Test fun getStatisticsTest() {
        val statistics = getStatistics(20, 2459)
        assertEquals(
            """
                Lines:020   
                Score:002459
            """.trimIndent(),
            statistics
        )
    }
}