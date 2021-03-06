package app.itgungnir.kwa.support.search_result

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import app.itgungnir.kwa.common.WebActivity
import app.itgungnir.kwa.support.R
import kotlinx.android.synthetic.main.list_item_search_article.view.*
import my.itgungnir.grouter.api.Router
import my.itgungnir.ui.easy_adapter.BaseDelegate
import my.itgungnir.ui.easy_adapter.EasyAdapter
import my.itgungnir.ui.html
import my.itgungnir.ui.onAntiShakeClick

class SearchResultDelegate : BaseDelegate<SearchResultState.SearchResultArticleVO>() {

    override fun layoutId(): Int = R.layout.list_item_search_article

    override fun onCreateVH(container: View) {
    }

    @SuppressLint("SetTextI18n")
    override fun onBindVH(
        item: SearchResultState.SearchResultArticleVO,
        holder: EasyAdapter.VH,
        position: Int,
        payloads: MutableList<Bundle>
    ) {

        holder.render(item) {

            this.onAntiShakeClick(2000L) {
                Router.instance.with(context)
                    .target(WebActivity)
                    .addParam("id", item.id)
                    .addParam("originId", item.originId)
                    .addParam("title", item.title)
                    .addParam("url", item.link)
                    .go()
            }

            authorView.text = "${context.getString(R.string.icon_author)} ${item.author}"

            titleView.text = html(item.title)

            if (payloads.isNotEmpty()) {
                val payload = payloads[0]
                payload.getString("PL_DATE")?.let {
                    dateView.text = it
                }
            } else {
                dateView.text = item.date
            }
        }
    }
}