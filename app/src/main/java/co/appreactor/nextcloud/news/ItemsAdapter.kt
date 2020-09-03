package co.appreactor.nextcloud.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import co.appreactor.nextcloud.news.db.NewsFeed
import co.appreactor.nextcloud.news.db.NewsItem
import kotlinx.android.synthetic.main.row_item.view.*
import kotlinx.datetime.*
import java.text.DateFormat
import java.util.*

class ItemsAdapter(
    private val items: MutableList<NewsItem>,
    private val feeds: MutableList<NewsFeed>,
    var onClick: ((NewsItem) -> Unit)? = null
) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    class ViewHolder(private val view: View, private val onClick: ((NewsItem) -> Unit)?) :
        RecyclerView.ViewHolder(view) {
        fun bind(item: NewsItem, feed: NewsFeed, isFirst: Boolean) {
            view.apply {
                topOffset.isVisible = isFirst

                image.isVisible = false // TODO

                primaryText.text = item.title

                val instant = Instant.fromEpochSeconds(item.pubDate)
                val dateString = DateFormat.getDateInstance().format(Date(instant.toEpochMilliseconds()))
                secondaryText.text = resources.getString(R.string.s_s_s, feed.title, "·", dateString)

                supportingText.text = item.summary

                primaryText.isEnabled = item.unread
                secondaryText.isEnabled = item.unread
                supportingText.isEnabled = item.unread

                card.setOnClickListener {
                    onClick?.invoke(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_item,
            parent, false
        )

        return ViewHolder(view, onClick)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            item = items[position],
            feed = feeds.find { it.id == items[position].feedId }!!,
            isFirst = position == 0
        )
    }

    fun swapItems(items: List<NewsItem>, feeds: List<NewsFeed>) {
        this.items.clear()
        this.items += items

        this.feeds.clear()
        this.feeds += feeds

        notifyDataSetChanged()
    }
}