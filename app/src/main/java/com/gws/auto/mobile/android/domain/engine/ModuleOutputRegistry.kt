package com.gws.auto.mobile.android.domain.engine

import com.gws.auto.mobile.android.domain.model.Module

data class OutputVariable(
    val name: String,
    val description: String
)

object ModuleOutputRegistry {
    fun getOutputVariables(module: Module): List<OutputVariable> {
        return when (module.type) {
            "drive_detect_file" -> listOf(
                OutputVariable("detectedFileId", "検出されたファイルのID"),
                OutputVariable("detectedFileName", "検出されたファイルの名前"),
                OutputVariable("detectedFileUrl", "検出されたファイルのリンク (URL)"),
                OutputVariable("detectedFileMimeType", "検出されたファイルのMIMEタイプ")
            )
            "DEFINE_VARIABLE" -> {
                val varName = module.parameters["variableName"]
                if (!varName.isNullOrBlank()) {
                    listOf(OutputVariable(varName, "定義された変数: ${module.parameters["value"] ?: ""}"))
                } else {
                    emptyList()
                }
            }
            "GET_RELATIVE_DATE" -> {
                val varName = module.parameters["variableName"]
                if (!varName.isNullOrBlank()) {
                    listOf(OutputVariable(varName, "計算された日付 (${module.parameters["amount"]} ${module.parameters["unit"]} ${module.parameters["direction"]})"))
                } else {
                    emptyList()
                }
            }
            "create_gmail_draft" -> listOf(
                OutputVariable("draftId", "作成された下書きのID")
            )
            "drive_copy_file" -> {
                val outputVar = module.parameters["outputFileId"]
                val list = mutableListOf(OutputVariable("newFileId", "コピーされたファイルのID"))
                if (!outputVar.isNullOrBlank()) {
                    list.add(OutputVariable(outputVar, "コピーされたファイルのID (カスタム変数名)"))
                }
                list
            }
            "drive_create_folder" -> {
                val outputVar = module.parameters["outputFolderId"]
                val list = mutableListOf(OutputVariable("newFolderId", "作成されたフォルダのID"))
                if (!outputVar.isNullOrBlank()) {
                    list.add(OutputVariable(outputVar, "作成されたフォルダのID (カスタム変数名)"))
                }
                list
            }
            else -> emptyList()
        }
    }
}
