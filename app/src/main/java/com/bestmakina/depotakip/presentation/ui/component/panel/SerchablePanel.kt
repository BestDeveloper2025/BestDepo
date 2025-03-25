package com.bestmakina.depotakip.presentation.ui.component.panel

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.bestmakina.depotakip.R
import com.bestmakina.depotakip.common.model.TransferItemModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchablePanel(
    items: List<TransferItemModel>,
    selectedId: String?,
    onItemSelected: (TransferItemModel) -> Unit,
    onDismiss: () -> Unit
) {
    val initialSearchText = selectedId ?: ""
    var searchQuery by remember { mutableStateOf(initialSearchText) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(selectedId) {
        selectedId?.let {
            searchQuery = it
        }
    }

    val filteredItems by remember(searchQuery, items) {
        derivedStateOf {
            Log.d("SearchablePanel", "filtreleniyor: searchQuery: $searchQuery")

            if (searchQuery.isEmpty()) {
                items
            } else {
                items.filter { item ->
                    item.id.contains(searchQuery, ignoreCase = true) ||
                            item.name.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }


    AlertDialog(
        containerColor = MaterialTheme.colorScheme.primary,
        onDismissRequest = onDismiss,
        title = { Text(text = "Seç: ") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it.trim() },
                    placeholder = { Text("Ara... (ID veya isim)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp)),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    if (filteredItems.isEmpty()) {
                        item {
                            Text(
                                text = "Seçili öğe bulunamadı",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    } else {
                        items(filteredItems, key = { it.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemSelected(item)
                                        searchQuery = ""
                                        focusManager.clearFocus()
                                    }
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(text = item.name)
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                focusManager.clearFocus()
                onDismiss()
            }) {
                Text("İptal")
            }
        }
    )
}