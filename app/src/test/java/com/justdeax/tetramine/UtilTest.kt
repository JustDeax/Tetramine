package com.justdeax.tetramine

import com.justdeax.tetramine.util.getStatistics
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilTest {
    @Test fun test1() {
        val statistics = getStatistics(200, 2_000_000)
        assertEquals(
            """
                Lines:200    
                Score:2000000
            """.trimIndent(),
            statistics
        )
    }

    @Test fun test2() {
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

    @Test fun test3() {
        val statistics = getStatistics(52, 45091)
        assertEquals(
            """
                Lines:052   
                Score:045091
            """.trimIndent(),
            statistics
        )
    }

    @Test fun test4() {
        val statistics = getStatistics(52, 45091, 9)
        assertEquals(
            """
                Lines:052   
                Score:045091
                Level:09    
            """.trimIndent(),
            statistics
        )
    }
}