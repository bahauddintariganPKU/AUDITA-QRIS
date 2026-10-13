package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.forensics.ForensicAuditEngine
import com.example.model.TransactionItem
import com.example.ui.theme.PastelHoney
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelMintDark
import com.example.ui.theme.PastelMintPrimary
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelRose
import com.example.ui.theme.RiskHighText
import com.example.ui.theme.RiskMediumText
import com.example.ui.theme.RiskSafeText
import java.util.Calendar

@Composable
fun BenfordChartCard(
  items: List<TransactionItem>,
  modifier: Modifier = Modifier
) {
  val expectedFrequencies = ForensicAuditEngine.getExpectedBenfordFrequencies()
  val actualFrequencies = ForensicAuditEngine.calculateActualDigitFrequencies(items)

  Card(
    modifier = modifier,
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Hukum Benford (Digit Pertama)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Deteksi manipulasi angka buatan pada nominal transaksi",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Visual Chart Canvas
      val maxPercent = 45f

      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(150.dp)
      ) {
        val width = size.width
        val height = size.height
        val barGroupWidth = width / 9f
        val barWidth = barGroupWidth * 0.38f

        for (d in 1..9) {
          val expected = expectedFrequencies[d] ?: 0f
          val actual = actualFrequencies[d] ?: 0f

          val xCenter = (d - 1) * barGroupWidth + barGroupWidth / 2f

          // Expected height
          val expectedH = (expected / maxPercent).coerceIn(0f, 1f) * (height - 30.dp.toPx())
          // Actual height
          val actualH = (actual / maxPercent).coerceIn(0f, 1f) * (height - 30.dp.toPx())

          val baselineY = height - 20.dp.toPx()

          // Draw expected (gray pastel outline or bar)
          drawRoundRect(
            color = Color(0xFFD4DDD7),
            topLeft = Offset(xCenter - barWidth - 2.dp.toPx(), baselineY - expectedH),
            size = Size(barWidth, expectedH),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
          )

          // Color actual bar: if d in [7,8,9] and actual > expected * 1.5, mark with pastel rose
          val isAnomaly = (d in listOf(7, 8, 9)) && (actual > expected)
          val actualColor = if (isAnomaly) PastelRose else PastelMintPrimary

          drawRoundRect(
            color = actualColor,
            topLeft = Offset(xCenter + 2.dp.toPx(), baselineY - actualH),
            size = Size(barWidth, actualH),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
          )
        }

        // Draw baseline
        drawLine(
          color = Color(0xFFE0E0E0),
          start = Offset(0f, height - 20.dp.toPx()),
          end = Offset(width, height - 20.dp.toPx()),
          strokeWidth = 1.dp.toPx()
        )
      }

      // X-Axis Labels 1..9
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        for (d in 1..9) {
          Text(
            text = "$d",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (d in listOf(7, 8, 9)) PastelRose else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Legend
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .background(Color(0xFFD4DDD7), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Teoritis Alami", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.width(18.dp))

        Box(
          modifier = Modifier
            .size(10.dp)
            .background(PastelMintPrimary, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Aktual QRIS", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.width(18.dp))

        Box(
          modifier = Modifier
            .size(10.dp)
            .background(PastelRose, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Anomali Digit 7-9", fontSize = 11.sp, color = PastelRose, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun HourlyDistributionCard(
  items: List<TransactionItem>,
  modifier: Modifier = Modifier
) {
  val hourlyCounts = IntArray(24) { 0 }
  for (item in items) {
    val cal = Calendar.getInstance().apply { timeInMillis = item.timestamp }
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    if (hour in 0..23) {
      hourlyCounts[hour]++
    }
  }

  val maxCount = (hourlyCounts.maxOrNull() ?: 1).coerceAtLeast(1)

  Card(
    modifier = modifier,
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Distribusi Waktu Transaksi (24 Jam)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Sorotan merah pastel menandai transaksi saat toko tutup (23:00 - 05:00)",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(90.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        for (hour in 0..23) {
          val count = hourlyCounts[hour]
          val isClosedHours = hour >= 23 || hour <= 5
          val heightFraction = (count.toFloat() / maxCount).coerceIn(0.1f, 1f)

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .width(8.dp)
                .fillMaxHeight(heightFraction)
                .clip(RoundedCornerShape(3.dp))
                .background(
                  if (count > 0 && isClosedHours) PastelRose
                  else if (count > 0) PastelMintPrimary
                  else Color(0xFFE5EDE8)
                )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("00:00", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("06:00", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("12:00", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("18:00", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("23:00", fontSize = 10.sp, color = PastelRose, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun RiskBreakdownMeter(
  items: List<TransactionItem>,
  modifier: Modifier = Modifier
) {
  val total = items.size.coerceAtLeast(1)
  val safe = items.count { it.totalFraudScore == 0 }
  val low = items.count { it.totalFraudScore == 1 }
  val high = items.count { it.totalFraudScore >= 2 }

  val safePct = (safe * 100f / total)
  val lowPct = (low * 100f / total)
  val highPct = (high * 100f / total)

  Column(modifier = modifier) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "Rasio Integritas Transaksi",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "${"%.0f".format(safePct)}% Valid",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = RiskSafeText
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Multi-segment progress bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(10.dp)
        .clip(RoundedCornerShape(5.dp))
    ) {
      if (safe > 0) {
        Box(
          modifier = Modifier
            .weight(safePct.coerceAtLeast(1f))
            .fillMaxHeight()
            .background(PastelMintPrimary)
        )
      }
      if (low > 0) {
        Box(
          modifier = Modifier
            .weight(lowPct.coerceAtLeast(1f))
            .fillMaxHeight()
            .background(PastelHoney)
        )
      }
      if (high > 0) {
        Box(
          modifier = Modifier
            .weight(highPct.coerceAtLeast(1f))
            .fillMaxHeight()
            .background(PastelRose)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text("Aman: $safe", fontSize = 11.sp, color = RiskSafeText, fontWeight = FontWeight.Medium)
      Text("Pantau: $low", fontSize = 11.sp, color = RiskMediumText, fontWeight = FontWeight.Medium)
      Text("Risiko Tinggi: $high", fontSize = 11.sp, color = RiskHighText, fontWeight = FontWeight.Bold)
    }
  }
}
