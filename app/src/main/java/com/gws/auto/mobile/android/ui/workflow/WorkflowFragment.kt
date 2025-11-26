package com.gws.auto.mobile.android.ui.workflow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import com.gws.auto.mobile.android.ui.theme.ThemeViewModel
import com.gws.auto.mobile.android.ui.workflow.editor.WorkflowEditorActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.gws.auto.mobile.android.R

@AndroidEntryPoint
class WorkflowFragment : Fragment() {

    private val viewModel: WorkflowViewModel by viewModels()
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()
    private val themeViewModel: ThemeViewModel by activityViewModels()

    @Inject
    lateinit var workflowEngine: WorkflowEngine

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val theme by themeViewModel.theme.collectAsStateWithLifecycle()
                val highlightColor by themeViewModel.highlightColor.collectAsStateWithLifecycle()
                val workflowItems by viewModel.filteredItems.collectAsStateWithLifecycle()

                GWSAutoForAndroidTheme(
                    theme = theme,
                    highlightColor = highlightColor
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        WorkflowScreen(
                            workflowItems = workflowItems,
                            onRunClicked = { workflow ->
                                lifecycleScope.launch {
                                    try {
                                        val isSuccess = workflowEngine.executeWorkflow(workflow.id)
                                        Timber.d("Workflow execution requested: ${workflow.name}")
                                        val message = if (isSuccess) {
                                            getString(R.string.workflow_executed_successfully, workflow.name)
                                        } else {
                                            getString(R.string.workflow_execution_failed, workflow.name)
                                        }
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    } catch (e: com.google.android.gms.auth.UserRecoverableAuthException) {
                                        e.intent?.let { startActivity(it) }
                                    } catch (e: Exception) {
                                        Timber.e(e, "Failed to execute workflow: ${workflow.name}")
                                    }
                                }
                            },
                            onEditClicked = { workflow ->
                                val intent = Intent(activity, WorkflowEditorActivity::class.java)
                                intent.putExtra("workflowId", workflow.id)
                                startActivity(intent)
                            },
                            onDeleteClicked = { workflow ->
                                viewModel.deleteWorkflow(workflow)
                            },
                            onFolderDeleteClicked = { folder ->
                                viewModel.deleteWorkflowFolder(folder.id)
                            },
                            onAddClicked = {
                                startActivity(Intent(activity, WorkflowEditorActivity::class.java))
                            },
                            onFavoriteClicked = { workflow ->
                                viewModel.toggleFavorite(workflow)
                            },
                            onFolderClicked = { folder ->
                                viewModel.toggleFolderExpansion(folder.id)
                            },
                            onMoveWorkflowToFolder = { workflowId, folderId ->
                                viewModel.moveWorkflowToFolder(workflowId, folderId)
                            },
                            fabClickFlow = mainSharedViewModel.fabClick,
                            onCreateFolder = { name ->
                                viewModel.createFolder(name)
                            },
                            onReorder = { fromId, toId ->
                                viewModel.reorderWorkflows(fromId, toId)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe search query from MainSharedViewModel
        lifecycleScope.launch {
            mainSharedViewModel.searchQuery.collect { query ->
                viewModel.onQueryChanged(query)
            }
        }
    }
}
