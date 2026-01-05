package dev.hossain.power.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * Floating power button composable with tap and long-press handling.
 *
 * @param onTap Callback invoked when the button is tapped (for opening power panel)
 * @param onLongPress Callback invoked when the button is long-pressed (for quick lock)
 * @param modifier Modifier for the button
 */
@Composable
fun FloatingPowerButton(
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(60.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = false,
                ).background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                ).pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onTap() },
                        onLongPress = { onLongPress() },
                    )
                },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Power,
            contentDescription = "Power Button",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(32.dp),
        )
    }
}
