package com.example.shoppinglistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalContext
import com.example.shoppinglistapp.data.ShoppingDatabase
import com.example.shoppinglistapp.data.ShoppingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = ShoppingDatabase.getDatabase(this)
        val dao = db.shoppingDao()

        setContent {
            val coroutineScope = rememberCoroutineScope()
            var items by remember { mutableStateOf(listOf<ShoppingItem>()) }
            var showDialog by remember { mutableStateOf(false) }
            var newItemName by remember { mutableStateOf("") }
            val keyboardController = LocalSoftwareKeyboardController.current
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                items = dao.getAllItems()
            }

            Scaffold(
                topBar = {
                    TopAppBar(title = { Text(stringResource(R.string.app_name)) })
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { showDialog = true }) {
                        Text(stringResource(R.string.add_item))
                    }
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (item.isBought) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            coroutineScope.launch(Dispatchers.IO) {
                                                dao.updateItem(item.copy(isBought = !item.isBought))
                                                items = dao.getAllItems()
                                            }
                                        }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item.name)
                            }
                            IconButton(onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    dao.deleteItem(item)
                                    items = dao.getAllItems()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete)
                                )
                            }
                        }
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDialog = false
                            newItemName = ""
                        },
                        title = { Text(stringResource(R.string.add_item_title)) },
                        text = {
                            TextField(
                                value = newItemName,
                                onValueChange = { newItemName = it },
                                label = { Text(stringResource(R.string.item_name)) },
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                keyboardController?.hide()
                                val nameToSave = newItemName.trim()
                                if (nameToSave.isNotBlank()) {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        dao.insertItem(ShoppingItem(name = nameToSave))
                                        items = dao.getAllItems()
                                    }
                                    showDialog = false
                                    newItemName = ""
                                }
                            }) {
                                Text(stringResource(R.string.save))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDialog = false
                                newItemName = ""
                            }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }
            }
        }
    }
}
