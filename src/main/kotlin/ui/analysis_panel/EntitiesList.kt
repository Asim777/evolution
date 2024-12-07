package ui.analysis_panel

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.Simulation

@Composable
fun EntitiesList() {
    Text(
        text = "Sample:",
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        fontSize = 18.sp,
    )

    LazyColumn {
        items(Simulation.selectedEntityList.value) { entity ->
            EntityListItem(
                entity = entity,
                isSelected = entity.id == Simulation.selectedEntity.value?.id,
                onClick = {
                    Simulation.selectedEntity.value = entity
                },
                onDelete = { entityToRemove ->
                    Simulation.selectedEntityList.value =
                        Simulation.selectedEntityList.value.filterNot { it.id == entityToRemove.id }
                }
            )
        }
    }
}