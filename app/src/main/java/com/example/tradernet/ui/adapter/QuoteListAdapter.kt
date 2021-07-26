package com.example.tradernet.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tradernet.R
import com.example.tradernet.databinding.RecyclerViewItemBinding
import com.example.tradernet.di.Injector
import com.example.tradernet.entity.Quote
import com.example.tradernet.model.IImageLoader
import com.example.tradernet.model.IModelFillQuote
import com.example.tradernet.view.IQuoteListRow
import com.example.tradernet.utils.ViewsUtils
import javax.inject.Inject

class QuoteListAdapter: RecyclerView.Adapter<QuoteListAdapter.ViewHolder>() {
    @Inject
    lateinit var modelFillQuote: IModelFillQuote

    init {
        Injector.getInstance().appComponent().inject(this)
    }

    var quotesList: List<Quote> = ArrayList()
        set(value) {
            if (field.isEmpty()) {
                field = value
                notifyItemRangeInserted(0, value.size)
            } else {
                val result = calcDiff(value)
                field = value
                result.dispatchUpdatesTo(this)
            }
        }

    private fun calcDiff(newValues: List<Quote>): DiffUtil.DiffResult =
        DiffUtil.calculateDiff(object: DiffUtil.Callback() {
            override fun getOldListSize(): Int =
                quotesList.size

            override fun getNewListSize(): Int =
                newValues.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = if (oldItemPosition < quotesList.size) {
                    quotesList[oldItemPosition]
                } else {
                    null
                }
                val newItem = if (newItemPosition < newValues.size) {
                    newValues[newItemPosition]
                } else {
                    null
                }
                return oldItem?.tickerId == newItem?.tickerId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = if (oldItemPosition < quotesList.size) {
                    quotesList[oldItemPosition]
                } else {
                    null
                }
                val newItem = if (newItemPosition < newValues.size) {
                    newValues[newItemPosition]
                } else {
                    null
                }

                return oldItem?.changePriceLastDeal == newItem?.changePriceLastDeal &&
                        oldItem?.lastTradePrice == newItem?.lastTradePrice &&
                        oldItem?.lastTradeExchange == newItem?.lastTradeExchange &&
                        oldItem?.rounding == newItem?.rounding &&
                        oldItem?.name == newItem?.name &&
                        oldItem?.diffPercentClosePrevSession == newItem?.diffPercentClosePrevSession &&
                        oldItem?.pricePrevClosed == newItem?.pricePrevClosed &&
                        oldItem?.priceOpenCurrentSession == newItem?.priceOpenCurrentSession &&
                        oldItem?._vector == newItem?._vector
            }
        })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerViewItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.reset()

        val item = quotesList[position]
        modelFillQuote.prepare(item, holder)
    }

    override fun getItemCount(): Int =
        quotesList.size


    inner class ViewHolder(private val binding: RecyclerViewItemBinding)
        : RecyclerView.ViewHolder(binding.root), IQuoteListRow {

        @Inject
        lateinit var imageLoader: IImageLoader<ImageView>

        init {
            Injector.getInstance().appComponent().inject(this)
        }

        fun reset() {
            ViewsUtils.goneViews(binding.logoView)
            ViewsUtils.clearImageView(binding.logoView)
            ViewsUtils.clearTextViews(
                binding.tickerView,
                binding.descriptionView,
                binding.percentView,
                binding.diffCountView
            )
            binding.percentView.setTextColor(Color.BLACK)
            binding.percentView.background = null
        }

        override fun setLogo(url: String) {
            imageLoader.loadInto(
                url = url,
                container = binding.logoView,
                successAction = {
                    ViewsUtils.showViews(binding.logoView)
                },
                failAction = {
                    ViewsUtils.goneViews(binding.logoView)
                }
            )
        }

        override fun setTicker(ticker: String) {
            binding.tickerView.text = ticker
        }

        override fun setDescription(description: String) {
            binding.descriptionView.text = description
        }

        override fun setPercent(percent: String) {
            binding.percentView.text = percent
        }

        override fun setColorPercentPositiveValue() {
            binding.percentView.setTextColor(
                ContextCompat.getColor(binding.percentView.context, R.color.color75BF44)
            )
        }

        override fun setColorPercentNegativeValue() {
            binding.percentView.setTextColor(
                ContextCompat.getColor(binding.percentView.context, R.color.colorFE2C59)
            )
        }

        override fun setDifference(difference: String) {
            binding.diffCountView.text = difference
        }

        override fun setBgPercentPositiveValue() {
            binding.percentView.setTextColor(Color.WHITE)
            binding.percentView.setBackgroundResource(R.drawable.bg_percent_positive)
        }

        override fun setBgPercentNegativeValue() {
            binding.percentView.setTextColor(Color.WHITE)
            binding.percentView.setBackgroundResource(R.drawable.bg_percent_negative)
        }
    }
}