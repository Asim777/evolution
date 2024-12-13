package ui.world

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.AppColors

@Composable
fun World() {
    Row(modifier = Modifier.padding(top = 20.dp)) {
        Canvas(modifier = Modifier.size(800.dp)) {
            drawRect(color = AppColors.AshGray, size = size)
        }
    }
}