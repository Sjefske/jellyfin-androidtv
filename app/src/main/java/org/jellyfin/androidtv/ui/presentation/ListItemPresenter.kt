package org.jellyfin.androidtv.ui.presentation

import android.view.KeyEvent
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.findViewTreeCompositionContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.leanback.widget.Presenter
import androidx.lifecycle.findViewTreeLifecycleOwner
import coil3.compose.AsyncImage
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.flow.MutableStateFlow
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.constant.ImageType
import org.jellyfin.androidtv.ui.base.Icon
import org.jellyfin.androidtv.ui.base.Text
import org.jellyfin.androidtv.ui.itemhandling.BaseRowItem
import org.jellyfin.androidtv.util.apiclient.getUrl
import org.jellyfin.androidtv.util.getActivity
import org.jellyfin.design.Tokens
import org.jellyfin.sdk.api.client.ApiClient
import org.koin.compose.koinInject
import java.util.Locale
import kotlin.math.roundToLong

/**
 * Presents items as full-width rows: a small 16:9 thumbnail, the name and the runtime.
 * Used by the library browser when the "List" image type is selected.
 *
 * @param rowHeight Height of a single row in dp.
 */
class ListItemPresenter(
	val rowHeight: Int,
) : Presenter() {
	override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
		val view = ComposeView(parent.context).apply {
			setParentCompositionContext(parent.findViewTreeCompositionContext())
			setViewTreeLifecycleOwner(parent.findViewTreeLifecycleOwner())
			setViewTreeSavedStateRegistryOwner(parent.findViewTreeSavedStateRegistryOwner())
			layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
			isFocusable = true
			isFocusableInTouchMode = true

			setOnLongClickListener {
				context.getActivity()?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MENU)) ?: false
			}
		}

		return ListItemViewHolder(view)
	}

	override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
		if (viewHolder !is ListItemViewHolder) return
		if (item !is BaseRowItem) return

		viewHolder.bind(item)
	}

	override fun onUnbindViewHolder(viewHolder: ViewHolder) {
		if (viewHolder !is ListItemViewHolder) return

		viewHolder.unbind()
	}

	private inner class ListItemViewHolder(composeView: ComposeView) : ViewHolder(composeView) {
		private val _item = MutableStateFlow<BaseRowItem?>(null)
		private val _focused = MutableStateFlow(false)

		init {
			composeView.setContent {
				val item by _item.collectAsState()
				val focused by _focused.collectAsState()

				ListItemContent(
					item = item,
					focused = focused,
					rowHeight = rowHeight,
				)
			}

			_focused.value = view.isFocused
			composeView.onFocusChangeListener = { _, focused -> _focused.value = focused }
		}

		fun bind(item: BaseRowItem) {
			_item.value = item
			_focused.value = view.isFocused
		}

		fun unbind() {
			_item.value = null
			_focused.value = false
		}
	}
}

private const val TICKS_PER_SECOND = 10_000_000L
private const val THUMB_ASPECT_RATIO = 16f / 9f

private fun formatRuntime(ticks: Long): String? {
	val totalMinutes = ticks / TICKS_PER_SECOND / 60
	if (totalMinutes <= 0) return null

	val hours = totalMinutes / 60
	val minutes = totalMinutes % 60
	return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

private const val BYTES_PER_MB = 1024.0 * 1024.0
private const val BYTES_PER_GB = BYTES_PER_MB * 1024.0

/**
 * Format a file size as whole megabytes below 1 GB and as gigabytes with one decimal above that.
 */
private fun formatFileSize(bytes: Long): String? {
	if (bytes <= 0) return null

	return if (bytes >= BYTES_PER_GB) String.format(Locale.getDefault(), "%.1f GB", bytes / BYTES_PER_GB)
	else String.format(Locale.getDefault(), "%d MB", (bytes / BYTES_PER_MB).roundToLong().coerceAtLeast(1))
}

@Composable
private fun ListItemContent(
	item: BaseRowItem?,
	focused: Boolean,
	rowHeight: Int,
) {
	if (item == null) return

	val context = LocalContext.current
	val density = LocalDensity.current
	val baseItem = item.baseItem

	val title = remember(item, context) { item.getCardName(context) ?: item.getFullName(context).orEmpty() }
	val image = remember(item) { item.getImage(ImageType.THUMB) }
	val runtime = remember(baseItem) { baseItem?.runTimeTicks?.let(::formatRuntime) }
	val fileSize = remember(baseItem) {
		baseItem?.mediaSources?.firstOrNull()?.size?.let(::formatFileSize)
	}
	val isFolder = baseItem?.isFolder == true
	val unplayedCount = baseItem?.userData?.unplayedItemCount?.takeIf { isFolder && it > 0 }
	val played = baseItem?.userData?.played == true
	val progress = baseItem?.userData?.playedPercentage?.takeIf { it > 0.0 && it < 100.0 }

	val height = rowHeight.dp
	val thumbWidth = height * THUMB_ASPECT_RATIO
	val titleSize = (rowHeight * 0.3f).coerceIn(14f, 30f).sp
	val metaSize = (rowHeight * 0.24f).coerceIn(12f, 24f).sp
	val metaColumnWidth = (rowHeight * 1.5f).coerceIn(64f, 140f).dp
	val checkSize = (rowHeight * 0.4f).coerceIn(16f, 32f).dp

	val shape = RoundedCornerShape(6.dp)
	val background = if (focused) Tokens.Color.colorWhite.copy(alpha = 0.2f)
	else Tokens.Color.colorBluegrey900.copy(alpha = 0.55f)

	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(Tokens.Space.spaceMd),
		modifier = Modifier
			.fillMaxWidth()
			.height(height)
			.clip(shape)
			.background(background, shape)
			.padding(end = Tokens.Space.spaceMd)
	) {
		// Thumbnail
		Box(
			modifier = Modifier
				.width(thumbWidth)
				.fillMaxHeight()
				.clipToBounds()
				.background(Tokens.Color.colorBluegrey800)
		) {
			if (image != null) {
				val api = koinInject<ApiClient>()
				// Coil's AsyncImage always fills the box and crops to 16:9, whatever the source aspect ratio
				AsyncImage(
					model = image.getUrl(
						api,
						maxWidth = with(density) { thumbWidth.roundToPx() },
						maxHeight = with(density) { height.roundToPx() },
					),
					contentDescription = null,
					contentScale = ContentScale.Crop,
					modifier = Modifier.fillMaxSize(),
				)
			} else {
				Image(
					painter = painterResource(if (isFolder) R.drawable.ic_folder else R.drawable.ic_clapperboard),
					contentDescription = null,
					modifier = Modifier
						.fillMaxSize(0.5f)
						.align(Alignment.Center),
				)
			}

			if (progress != null) {
				Box(
					modifier = Modifier
						.align(Alignment.BottomStart)
						.fillMaxWidth((progress / 100.0).toFloat())
						.height(3.dp)
						.background(Tokens.Color.colorBlue500)
				)
			}
		}

		// Name
		val marquee = if (focused) Modifier.basicMarquee(
			iterations = Int.MAX_VALUE,
			initialDelayMillis = 500,
		) else Modifier

		Text(
			text = title,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			color = Tokens.Color.colorWhite,
			fontSize = titleSize,
			fontWeight = if (focused) FontWeight.SemiBold else FontWeight.Normal,
			modifier = Modifier
				.weight(1f)
				.then(marquee),
		)

		// Folder: number of unplayed items
		if (unplayedCount != null) {
			Text(
				text = unplayedCount.toString(),
				maxLines = 1,
				color = Tokens.Color.colorGrey300,
				fontSize = metaSize,
			)
		}

		// Runtime and file size as fixed-width columns, so they line up between rows
		Text(
			text = runtime.orEmpty(),
			maxLines = 1,
			color = Tokens.Color.colorGrey300,
			fontSize = metaSize,
			textAlign = TextAlign.End,
			modifier = Modifier.width(metaColumnWidth),
		)

		Text(
			text = fileSize.orEmpty(),
			maxLines = 1,
			color = Tokens.Color.colorGrey300,
			fontSize = metaSize,
			textAlign = TextAlign.End,
			modifier = Modifier.width(metaColumnWidth),
		)

		// Watched (space is always reserved to keep the columns aligned)
		Box(modifier = Modifier.size(checkSize)) {
			if (played) {
				Icon(
					painter = painterResource(R.drawable.ic_check),
					contentDescription = null,
					tint = Tokens.Color.colorGrey300,
					modifier = Modifier.fillMaxSize(),
				)
			}
		}
	}
}
