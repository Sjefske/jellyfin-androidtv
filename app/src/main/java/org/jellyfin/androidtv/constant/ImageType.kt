package org.jellyfin.androidtv.constant

import org.jellyfin.androidtv.R
import org.jellyfin.preference.PreferenceEnum

enum class ImageType(
	override val nameRes: Int,
) : PreferenceEnum {
	/**
	 * Poster.
	 */
	POSTER(R.string.image_type_poster),

	/**
	 * Thumbnail.
	 */
	THUMB(R.string.image_type_thumbnail),

	/**
	 * Banner.
	 */
	BANNER(R.string.image_type_banner),

	/**
	 * Text list with one item per row and a small thumbnail.
	 */
	LIST(R.string.image_type_list),
}
