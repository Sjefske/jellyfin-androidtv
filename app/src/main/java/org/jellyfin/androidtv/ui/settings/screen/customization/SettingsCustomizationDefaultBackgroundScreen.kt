package org.jellyfin.androidtv.ui.settings.screen.customization

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.data.service.BackgroundService
import org.jellyfin.androidtv.preference.UserPreferences
import org.jellyfin.androidtv.preference.constant.DefaultBackground
import org.jellyfin.androidtv.ui.base.Text
import org.jellyfin.androidtv.ui.base.form.RadioButton
import org.jellyfin.androidtv.ui.base.list.ListButton
import org.jellyfin.androidtv.ui.base.list.ListSection
import org.jellyfin.androidtv.ui.navigation.LocalRouter
import org.jellyfin.androidtv.ui.navigation.focus.focusKey
import org.jellyfin.androidtv.ui.settings.compat.rememberPreference
import org.jellyfin.androidtv.ui.settings.composable.SettingsColumn
import org.koin.compose.koinInject

@Composable
fun SettingsCustomizationDefaultBackgroundScreen() {
	val router = LocalRouter.current
	val userPreferences = koinInject<UserPreferences>()
	val backgroundService = koinInject<BackgroundService>()
	var defaultBackground by rememberPreference(userPreferences, UserPreferences.defaultBackground)

	SettingsColumn {
		item {
			ListSection(
				overlineContent = { Text(stringResource(R.string.pref_customization).uppercase()) },
				headingContent = { Text(stringResource(R.string.pref_default_background)) },
				captionContent = { Text(stringResource(R.string.pref_default_background_description)) },
			)
		}

		items(DefaultBackground.entries) { entry ->
			ListButton(
				headingContent = { Text(stringResource(entry.nameRes)) },
				trailingContent = { RadioButton(checked = defaultBackground == entry) },
				onClick = {
					defaultBackground = entry
					userPreferences[UserPreferences.defaultBackground] = entry
					backgroundService.refreshDefaultBackground()
					router.back()
				},
				modifier = Modifier
					.focusKey("default_background_${entry.name}", initialFocus = defaultBackground == entry)
			)
		}
	}
}
