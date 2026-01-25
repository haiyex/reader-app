package com.reader.app.presentation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reader.app.presentation.ui.bookshelf.BookshelfScreen
import com.reader.app.presentation.ui.bookshelf.BookshelfViewModel
import com.reader.app.presentation.ui.reader.ReaderScreen
import com.reader.app.presentation.ui.reader.ReaderViewModel
import com.reader.app.presentation.ui.search.SearchScreen
import com.reader.app.presentation.ui.search.SearchViewModel
import com.reader.app.presentation.ui.settings.SettingsScreen
import com.reader.app.presentation.ui.settings.SettingsViewModel
import com.reader.app.presentation.ui.source.SourceScreen
import com.reader.app.presentation.ui.source.SourceViewModel
import com.reader.app.presentation.theme.ReaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val bookshelfViewModel: BookshelfViewModel by viewModels()
    
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            bookshelfViewModel.importBook(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        handleIntent(intent)
        
        setContent {
            ReaderAppContent(
                onImportBook = { openFilePicker() }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.data?.let { uri ->
            bookshelfViewModel.importBook(uri)
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/epub+zip",
                "application/x-mobipocket-ebook",
                "text/plain"
            ))
        }
        filePickerLauncher.launch(intent)
    }
}

@Composable
fun ReaderAppContent(
    onImportBook: () -> Unit
) {
    val navController = rememberNavController()
    
    ReaderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "bookshelf"
            ) {
                composable("bookshelf") {
                    val viewModel: BookshelfViewModel = viewModel()
                    BookshelfScreen(
                        viewModel = viewModel,
                        onBookClick = { bookId ->
                            navController.navigate("reader/$bookId")
                        },
                        onSearchClick = {
                            navController.navigate("search")
                        },
                        onSettingsClick = {
                            navController.navigate("settings")
                        },
                        onSourceClick = {
                            navController.navigate("sources")
                        },
                        onImportBook = onImportBook
                    )
                }
                
                composable("reader/{bookId}") { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
                    val viewModel: ReaderViewModel = viewModel()
                    ReaderScreen(
                        viewModel = viewModel,
                        bookId = bookId,
                        onBack = { navController.popBackStack() }
                    )
                }
                
                composable("search") {
                    val viewModel: SearchViewModel = viewModel()
                    SearchScreen(
                        viewModel = viewModel,
                        onBookClick = { bookId ->
                            navController.navigate("reader/$bookId")
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                
                composable("settings") {
                    val viewModel: SettingsViewModel = viewModel()
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                
                composable("sources") {
                    val viewModel: SourceViewModel = viewModel()
                    SourceScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
