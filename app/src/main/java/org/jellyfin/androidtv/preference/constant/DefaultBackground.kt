package org.jellyfin.androidtv.preference.constant

import org.jellyfin.androidtv.R
import org.jellyfin.preference.PreferenceEnum

/**
 * What to show as background when the focused item has no backdrop of its own.
 */
enum class DefaultBackground(
	override val nameRes: Int,
) : PreferenceEnum {
	/**
	 * The background of the selected app theme (plain black for the default theme).
	 */
	THEME(R.string.default_background_theme),

	/**
	 * The splash screen image configured on the Jellyfin server (Dashboard → Branding).
	 */
	SERVER_SPLASHSCREEN(R.string.default_background_server_splashscreen),
}
