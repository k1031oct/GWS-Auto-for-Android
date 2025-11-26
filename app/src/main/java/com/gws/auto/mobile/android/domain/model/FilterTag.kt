package com.gws.auto.mobile.android.domain.model

data class FilterTag(
    override val displayName: String,
    val type: FilterType,
    val isActive: Boolean = false
) : DisplayTag {
    override val isFilter: Boolean = true
}

enum class FilterType {
    FAVORITE,
    BOOKMARK
}
