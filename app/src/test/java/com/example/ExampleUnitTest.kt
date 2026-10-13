package com.example

import com.example.data.SampleData
import com.example.forensics.ForensicAuditEngine
import com.example.model.RiskLevel
import com.example.vpn.DefaultProfiles
import com.example.vpn.VpnConnectionState
import com.example.vpn.VpnProtocol
import com.example.vpn.VpnStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testForensicAuditBatchRules() {
    val seed = SampleData.getInitialSeed()
    assertEquals(8, seed.size)

    val rrn001 = seed.find { it.rrn == "RRN_001" }!!
    assertEquals(0, rrn001.totalFraudScore)
    assertEquals(RiskLevel.SAFE, rrn001.riskLevel)

    val rrn002 = seed.find { it.rrn == "RRN_002" }!!
    assertTrue(rrn002.flagJamTutup)

    val rrn003 = seed.find { it.rrn == "RRN_003" }!!
    val rrn004 = seed.find { it.rrn == "RRN_004" }!!
    assertTrue(rrn003.flagSplitTx)
    assertTrue(rrn004.flagSplitTx)

    val rrn005 = seed.find { it.rrn == "RRN_005" }!!
    assertTrue(rrn005.flagNominalBulat)

    val rrn006 = seed.find { it.rrn == "RRN_006" }!!
    assertTrue(rrn006.flagSelfTx)

    val rrn007 = seed.find { it.rrn == "RRN_007" }!!
    assertTrue(rrn007.flagBenfordAnomali)
  }

  @Test
  fun testBenfordCalculation() {
    val frequencies = ForensicAuditEngine.getExpectedBenfordFrequencies()
    assertEquals(9, frequencies.size)
    // Digit 1 should be around 30.1%
    assertTrue(frequencies[1]!! > 30f)
    // Digit 9 should be around 4.5%
    assertTrue(frequencies[9]!! < 5f)
  }

  @Test
  fun testVpnDatabaseProfilesAndProtocols() {
    assertEquals(3, DefaultProfiles.size)
    val jktProfile = DefaultProfiles.first()
    assertEquals("Jakarta Central DB Server (Primary)", jktProfile.name)
    assertEquals(5432, jktProfile.port)

    val status = VpnStatus(
      state = VpnConnectionState.DISCONNECTED,
      selectedProfile = jktProfile,
      protocol = VpnProtocol.WIREGUARD
    )
    assertEquals(VpnConnectionState.DISCONNECTED, status.state)
    assertEquals("WireGuard Kernel", status.protocol.displayName)
    assertFalse(status.isDatabaseReachable)
  }
}
