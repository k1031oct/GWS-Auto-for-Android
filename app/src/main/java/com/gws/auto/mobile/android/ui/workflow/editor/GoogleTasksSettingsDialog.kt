package com.gws.auto.mobile.android.ui.workflow.editor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.toArgb
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentModuleSettingsBinding
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.service.TasksApiService
import com.gws.auto.mobile.android.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class GoogleTasksSettingsDialog(private val module: Module) : DialogFragment() {

    private var _binding: FragmentModuleSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkflowEditorViewModel by activityViewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    @Inject
    lateinit var tasksApiService: TasksApiService

    private var currentHighlightColor: Int? = null
    private val taskLists = mutableListOf<Pair<String, String>>() // ID, Title

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentModuleSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.moduleType.text = "Google Tasks: Create Task"
        binding.parametersContainer.removeAllViews()

        setupUI()

        binding.cancelButton.setOnClickListener { dismiss() }

        lifecycleScope.launch {
            themeViewModel.highlightColor.collect { colorName ->
                applyHighlightColor(colorName)
            }
        }
    }

    private fun setupUI() {
        val context = requireContext()
        val container = binding.parametersContainer

        // Task List Spinner
        val taskListSpinner = Spinner(context)
        val newTaskListInput = createTextInputLayout("新しいリスト名", null)
        newTaskListInput.visibility = View.GONE

        container.addView(createSectionHeader("タスクリスト"))
        container.addView(taskListSpinner)
        container.addView(newTaskListInput)

        // Fetch Task Lists
        lifecycleScope.launch {
            try {
                val lists = tasksApiService.getTaskLists()
                taskLists.clear()
                taskLists.add("@default" to "My Tasks (Default)")
                lists.forEach { taskLists.add(it.id to it.title) }
                taskLists.add("create_new" to "+ 新しいリストを作成")

                val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, taskLists.map { it.second })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                taskListSpinner.adapter = adapter

                // Set selection
                val currentListId = module.parameters["task_list_id"] ?: "@default"
                val index = taskLists.indexOfFirst { it.first == currentListId }
                if (index >= 0) taskListSpinner.setSelection(index)

            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch task lists")
                Toast.makeText(context, "タスクリストの取得に失敗しました", Toast.LENGTH_SHORT).show()
            }
        }

        taskListSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedId = taskLists[position].first
                newTaskListInput.visibility = if (selectedId == "create_new") View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Task Details
        val titleInput = createTextInputLayout("タスク名", module.parameters["title"])
        val notesInput = createTextInputLayout("メモ", module.parameters["notes"], isMultiLine = true)
        
        container.addView(createSectionHeader("詳細"))
        container.addView(titleInput)
        container.addView(notesInput)

        // Due Date & Time
        val dueDateInput = createTextInputLayout("期限 (YYYY-MM-DD)", module.parameters["due_date"])
        val dueTimeInput = createTextInputLayout("時間 (HH:mm)", module.parameters["due_time"])
        
        dueDateInput.editText?.isFocusable = false
        dueDateInput.editText?.isClickable = true
        dueDateInput.editText?.setOnClickListener { showDatePicker(dueDateInput.editText!!) }

        dueTimeInput.editText?.isFocusable = false
        dueTimeInput.editText?.isClickable = true
        dueTimeInput.editText?.setOnClickListener { showTimePicker(dueTimeInput.editText!!) }

        container.addView(createSectionHeader("期限設定"))
        container.addView(dueDateInput)
        container.addView(dueTimeInput)

        // Save Button
        binding.saveButton.setOnClickListener {
            val selectedPosition = taskListSpinner.selectedItemPosition
            if (selectedPosition < 0) return@setOnClickListener

            val selectedListId = taskLists[selectedPosition].first
            val params = mutableMapOf(
                "task_list_id" to selectedListId,
                "title" to titleInput.editText?.text.toString(),
                "notes" to notesInput.editText?.text.toString(),
                "due_date" to dueDateInput.editText?.text.toString(),
                "due_time" to dueTimeInput.editText?.text.toString()
            )

            if (selectedListId == "create_new") {
                val newListName = newTaskListInput.editText?.text.toString()
                if (newListName.isBlank()) {
                    newTaskListInput.error = "リスト名を入力してください"
                    return@setOnClickListener
                }
                params["new_task_list_name"] = newListName
            }

            viewModel.updateModuleParameters(module.id, params)
            dismiss()
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val dateStr = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
            editText.setText(dateStr)
        }
        DatePickerDialog(requireContext(), dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val timeStr = String.format("%02d:%02d", hourOfDay, minute)
            editText.setText(timeStr)
        }
        TimePickerDialog(requireContext(), timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true).show()
    }

    private fun createTextInputLayout(hint: String, initialValue: String?, isMultiLine: Boolean = false): TextInputLayout {
        val textInputLayout = TextInputLayout(requireContext()).apply {
            this.hint = hint
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            currentHighlightColor?.let { setBoxStrokeColorStateList(ColorStateList.valueOf(it)) }
        }
        val editText = EditText(requireContext()).apply {
            setText(initialValue ?: "")
            if (isMultiLine) {
                inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
                minLines = 3
            }
        }
        textInputLayout.addView(editText)
        return textInputLayout
    }

    private fun createSectionHeader(title: String): TextView {
        return TextView(requireContext()).apply {
            text = title
            setTextAppearance(android.R.style.TextAppearance_Material_Medium)
            setPadding(0, 24, 0, 8)
        }
    }

    private fun applyHighlightColor(colorName: String) {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

        val color = when (colorName) {
            "forest" -> if (isDarkTheme) ForestPrimaryDark else ForestPrimaryLight
            "ocean" -> if (isDarkTheme) OceanPrimaryDark else OceanPrimaryLight
            "sakura" -> if (isDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight
            else -> if (isDarkTheme) MonochromePrimaryDark else MonochromePrimaryLight
        }
        currentHighlightColor = color.toArgb()
        
        val colorStateList = ColorStateList.valueOf(currentHighlightColor!!)
        binding.saveButton.backgroundTintList = colorStateList
        binding.saveButton.setTextColor(android.graphics.Color.BLACK)
        binding.cancelButton.setTextColor(currentHighlightColor!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
