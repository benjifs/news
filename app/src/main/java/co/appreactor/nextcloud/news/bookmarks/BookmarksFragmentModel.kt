package co.appreactor.nextcloud.news.bookmarks

import androidx.lifecycle.ViewModel
import co.appreactor.nextcloud.news.feeds.FeedsRepository
import co.appreactor.nextcloud.news.common.Preferences
import co.appreactor.nextcloud.news.db.Feed
import co.appreactor.nextcloud.news.db.FeedItem
import co.appreactor.nextcloud.news.feeditems.FeedItemsAdapterRow
import co.appreactor.nextcloud.news.feeditems.FeedItemsRepository
import co.appreactor.nextcloud.news.podcasts.PodcastsManager
import co.appreactor.nextcloud.news.podcasts.isPodcast
import kotlinx.coroutines.flow.*
import java.text.DateFormat
import java.util.*

class BookmarksFragmentModel(
    private val feedsRepository: FeedsRepository,
    private val feedItemsRepository: FeedItemsRepository,
    private val podcastsManager: PodcastsManager,
    private val prefs: Preferences,
) : ViewModel() {

    suspend fun getBookmarks() = combine(
        feedsRepository.all(),
        feedItemsRepository.starred(),
        getCropFeedImages()
    ) { feeds, feedItems, cropFeedImages ->
        feedItems.map {
            val feed = feeds.single { feed -> feed.id == it.feedId }
            it.toRow(feed, cropFeedImages)
        }
    }

    suspend fun downloadPodcast(id: Long) {
        podcastsManager.downloadPodcast(id)
    }

    suspend fun getFeedItem(id: Long) = feedItemsRepository.byId(id).first()

    private suspend fun getCropFeedImages() = prefs.getBoolean(
        key = Preferences.CROP_FEED_IMAGES,
        default = true
    )

    private fun FeedItem.toRow(feed: Feed, cropFeedImages: Boolean): FeedItemsAdapterRow {
        val dateString = DateFormat.getDateInstance().format(Date(pubDate * 1000))

        return FeedItemsAdapterRow(
            id,
            title,
            feed.title + " · " + dateString,
            summary,
            true,
            openGraphImageUrl,
            cropFeedImages,
            isPodcast(),
            enclosureDownloadProgress
        )
    }
}