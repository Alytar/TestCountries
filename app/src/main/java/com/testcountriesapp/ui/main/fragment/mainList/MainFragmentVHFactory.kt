package com.testcountriesapp.ui.main.fragment.mainList

import android.app.Activity
import android.net.Uri
import android.view.View
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener
import com.testcountriesapp.general.adapter.BaseViewHolder
import com.testcountriesapp.repository.model.Country
import kotlinx.android.synthetic.main.item_list.view.*
import timber.log.Timber


class MainFragmentVHFactory : BaseViewHolder.Factory<Country> {

    override fun create(itemView: View): BaseViewHolder<Country> = ListVH(itemView)

    class ListVH(itemView: View) : BaseViewHolder<Country>(itemView) {

        override fun bind(item: Country, position: Int) {
            val uri = Uri.parse(item.flagPictureLink)
            GlideToVectorYou
                .init()
                .with(context as Activity?)
                .withListener(object : GlideToVectorYouListener {
                    override fun onLoadFailed() {
                        Timber.e("MainFragmentVHFactory onLoadImageFailed")
                    }

                    override fun onResourceReady() {
                        Timber.e("MainFragmentVHFactory onResourceReady")
                    }

                })
                .load(uri, itemView.country_flag)
            itemView.country_name.text = item.countryName
            itemView.country_code.text = item.countryCode
        }
    }
}