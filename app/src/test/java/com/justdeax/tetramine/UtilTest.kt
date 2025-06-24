package com.justdeax.tetramine

import com.justdeax.tetramine.util.getStatistics
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilTest {
    @Test fun getStatisticsTest() {
        val statistics = getStatistics(200, 2_000_000)
        assertEquals(
            """
                Lines:200    
                Score:2000000
            """.trimIndent(),
            statistics
        )
    }

    @Test fun getStatisticsProTest() {
        val statistics = getStatistics(200, 2_000_000, 20)
        assertEquals(
            """
                Lines:200    
                Score:2000000
                Level:Σ(20)  
            """.trimIndent(),
            statistics
        )
    }
}