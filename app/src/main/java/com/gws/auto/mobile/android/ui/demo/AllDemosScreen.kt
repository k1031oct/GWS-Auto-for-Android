package com.gws.auto.mobile.android.ui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.components.Accordion
import com.gws.auto.mobile.android.ui.components.AccordionItem
import com.gws.auto.mobile.android.ui.components.AlertVariant
import com.gws.auto.mobile.android.ui.components.AppAlert
import com.gws.auto.mobile.android.ui.components.AppAlertDialog
import com.gws.auto.mobile.android.ui.components.AppAvatar
import com.gws.auto.mobile.android.ui.components.AppBadge
import com.gws.auto.mobile.android.ui.components.AppButton
import com.gws.auto.mobile.android.ui.components.AppCalendar
import com.gws.auto.mobile.android.ui.components.AppCard
import com.gws.auto.mobile.android.ui.components.AppCarousel
// import com.gws.auto.mobile.android.ui.components.AppChart
import com.gws.auto.mobile.android.ui.components.AppCheckbox
import com.gws.auto.mobile.android.ui.components.AppCollapsible
import com.gws.auto.mobile.android.ui.components.AppDialog
import com.gws.auto.mobile.android.ui.components.AppDropdownMenu
import com.gws.auto.mobile.android.ui.components.AppFormItem
import com.gws.auto.mobile.android.ui.components.AppFormLabel
import com.gws.auto.mobile.android.ui.components.AppInput
import com.gws.auto.mobile.android.ui.components.AppMenubar
import com.gws.auto.mobile.android.ui.components.AppPopover
import com.gws.auto.mobile.android.ui.components.AppRadioGroup
import com.gws.auto.mobile.android.ui.components.AppSelect
import com.gws.auto.mobile.android.ui.components.AppSeparator
import com.gws.auto.mobile.android.ui.components.AppSheet
import com.gws.auto.mobile.android.ui.components.AppSkeleton
import com.gws.auto.mobile.android.ui.components.AppSlider
import com.gws.auto.mobile.android.ui.components.BadgeVariant
import com.gws.auto.mobile.android.ui.components.ButtonVariant
import com.gws.auto.mobile.android.ui.components.CardHeader
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

@Composable
fun AllDemosScreen() {
    var showAlertDialog by remember { mutableStateOf(false) }
    var showGenericDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (showAlertDialog) {
        AppAlertDialog(
            onDismissRequest = { showAlertDialog = false },
            onConfirmation = { showAlertDialog = false },
            dialogTitle = stringResource(R.string.delete_item_title),
            dialogText = stringResource(R.string.delete_item_message),
            confirmButtonText = stringResource(R.string.delete),
            dismissButtonText = stringResource(R.string.cancel)
        )
    }

    if (showGenericDialog) {
        AppDialog(
            onDismissRequest = { showGenericDialog = false },
            dialogTitle = stringResource(R.string.generic_dialog_title)
        ) {
            Column {
                Text(stringResource(R.string.generic_dialog_message))
                AppButton(onClick = { showGenericDialog = false }, text = stringResource(R.string.close))
            }
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppSheet(
        drawerState = drawerState,
        sheetContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.sheet_content))
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(onClick = { scope.launch { drawerState.close() } }, text = stringResource(R.string.close_sheet))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val menus = mapOf(
                "File" to listOf("New", "Open", "Save"),
                "Edit" to listOf("Cut", "Copy", "Paste")
            )
            AppMenubar(menus = menus, onMenuItemClick = { menu, item ->
                println("Clicked $item from $menu")
            })

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                AppButton(onClick = { scope.launch { drawerState.open() } }, text = stringResource(R.string.open_sheet))
                Spacer(modifier = Modifier.height(32.dp))
                AppButton(onClick = { }, text = stringResource(R.string.default_button))
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(
                    onClick = { showAlertDialog = true },
                    text = stringResource(R.string.destructive),
                    variant = ButtonVariant.Destructive
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(onClick = { }, text = stringResource(R.string.secondary), variant = ButtonVariant.Secondary)

                Spacer(modifier = Modifier.height(32.dp))

                var text by remember { mutableStateOf("") }
                AppInput(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    label = stringResource(R.string.email_label),
                    placeholder = "user@example.com"
                )

                Spacer(modifier = Modifier.height(32.dp))

                Accordion {
                    AccordionItem(title = stringResource(R.string.section_title, 1)) {
                        Text(stringResource(R.string.section_content, 1))
                    }
                    AccordionItem(title = stringResource(R.string.section_title, 2)) {
                        Text(stringResource(R.string.section_content, 2))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                AppAlert(
                    title = stringResource(R.string.default_alert_title),
                    description = stringResource(R.string.default_alert_message)
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppAlert(
                    variant = AlertVariant.Destructive,
                    title = stringResource(R.string.destructive_alert_title),
                    description = stringResource(R.string.destructive_alert_message)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppAvatar(imageUrl = "https://i.pravatar.cc/150?u=a042581f4e29026704d", fallbackText = "JD")
                    Spacer(modifier = Modifier.width(16.dp))
                    AppAvatar(imageUrl = null, fallbackText = "GWS")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppBadge(text = stringResource(R.string.badge_default))
                    AppBadge(text = stringResource(R.string.badge_secondary), variant = BadgeVariant.Secondary)
                    AppBadge(text = stringResource(R.string.badge_destructive), variant = BadgeVariant.Destructive)
                    AppBadge(text = stringResource(R.string.badge_outline), variant = BadgeVariant.Outline)
                }

                Spacer(modifier = Modifier.height(32.dp))

                var selectedDate by remember { mutableStateOf<Long?>(null) }
                AppCalendar(onDateSelected = { newDate -> selectedDate = newDate })
                Text(stringResource(R.string.selected_date_label, selectedDate?.toString() ?: stringResource(R.string.none)))

                Spacer(modifier = Modifier.height(32.dp))

                AppCard(
                    header = {
                        CardHeader(title = stringResource(R.string.card_title), description = stringResource(R.string.card_description))
                    },
                    content = {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Text(stringResource(R.string.card_content))
                        }
                    },
                    footer = {
                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                            AppButton(onClick = { }, text = stringResource(R.string.action))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                val carouselItems = listOf(Color.Red, Color.Green, Color.Blue)
                AppCarousel(items = carouselItems) {item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(item),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.item), style = MaterialTheme.typography.headlineMedium)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // AppChart()

                Spacer(modifier = Modifier.height(32.dp))

                var checkedState1 by remember { mutableStateOf(true) }
                var checkedState2 by remember { mutableStateOf(false) }
                var checkedState3 by remember { mutableStateOf(true) }

                Column(modifier = Modifier.fillMaxWidth()) {
                    AppCheckbox(
                        checked = checkedState1,
                        onCheckedChange = { isChecked -> checkedState1 = isChecked },
                        label = stringResource(R.string.option_label, 1)
                    )
                    AppCheckbox(
                        checked = checkedState2,
                        onCheckedChange = { isChecked -> checkedState2 = isChecked },
                        label = stringResource(R.string.option_label, 2)
                    )
                    AppCheckbox(
                        checked = checkedState3,
                        onCheckedChange = null, // Disabled checkbox
                        label = stringResource(R.string.option_disabled, 3),
                        enabled = false
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AppCollapsible(
                    trigger = { expanded ->
                        Text(
                            text = if (expanded) stringResource(R.string.collapsible_hide) else stringResource(R.string.collapsible_show),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                    },
                    content = {
                        Text(stringResource(R.string.collapsible_content))
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppButton(onClick = { showGenericDialog = true }, text = stringResource(R.string.show_generic_dialog))

                Spacer(modifier = Modifier.height(32.dp))

                AppDropdownMenu(
                    trigger = { AppButton(onClick = { }, text = stringResource(R.string.open_dropdown)) },
                    menuItems = listOf(stringResource(R.string.dropdown_item, 1), stringResource(R.string.dropdown_item, 2), stringResource(R.string.dropdown_item, 3)),
                    onMenuItemClick = { item -> println("Dropdown item clicked: $item") }
                )

                Spacer(modifier = Modifier.height(32.dp))

                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                Column(modifier = Modifier.fillMaxWidth()) {
                    AppFormItem(
                        label = { AppFormLabel(text = stringResource(R.string.username)) },
                        content = { AppInput(value = username, onValueChange = { newUsername -> username = newUsername }) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AppFormItem(
                        label = { AppFormLabel(text = stringResource(R.string.password)) },
                        content = { AppInput(value = password, onValueChange = { newPassword -> password = newPassword }) },
                        errorMessage = if (password.length < 6 && password.isNotEmpty()) stringResource(R.string.password_error) else null
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AppButton(onClick = { /* Handle form submission */ }, text = stringResource(R.string.submit))
                }

                Spacer(modifier = Modifier.height(32.dp))

                AppPopover(
                    trigger = { AppButton(onClick = { }, text = stringResource(R.string.open_popover)) },
                    content = {
                        Column {
                            Text(stringResource(R.string.popover_content))
                            AppButton(onClick = { }, text = stringResource(R.string.action_in_popover))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                val radioOptions = listOf(stringResource(R.string.option_label, 1), stringResource(R.string.option_label, 2), stringResource(R.string.option_label, 3))
                var selectedRadio by remember { mutableStateOf(radioOptions[0]) }
                AppRadioGroup(
                    options = radioOptions,
                    selectedOption = selectedRadio,
                    onOptionSelected = { newSelection -> selectedRadio = newSelection }
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppSeparator()

                Spacer(modifier = Modifier.height(32.dp))

                val selectOptions = listOf(stringResource(R.string.option_a), stringResource(R.string.option_b), stringResource(R.string.option_c))
                var selectedOption by remember { mutableStateOf(selectOptions[0]) }
                AppSelect(
                    options = selectOptions,
                    selectedOption = selectedOption,
                    onOptionSelected = { newSelection -> selectedOption = newSelection },
                    label = stringResource(R.string.select_option_label)
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppSkeleton(modifier = Modifier.height(100.dp).fillMaxWidth())

                Spacer(modifier = Modifier.height(32.dp))

                var sliderValue by remember { mutableStateOf(0.5f) }
                Text(text = stringResource(R.string.slider_value, sliderValue))
                AppSlider(
                    value = sliderValue,
                    onValueChange = { newValue -> sliderValue = newValue }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
