package com.woocommerce.android.ui.orders

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.Callback
import androidx.recyclerview.widget.RecyclerView
import com.woocommerce.android.R
import com.woocommerce.android.extensions.isEqualTo
import com.woocommerce.android.model.Order
import com.woocommerce.android.model.Refund
import com.woocommerce.android.widgets.WooClickableSpan
import java.math.BigDecimal

class OrderDetailRefundListAdapter(
    private val formatCurrency: (BigDecimal) -> String,
    private val onRefundDetailsClicked: (Long, Long) -> Unit,
    private val order: Order
) : RecyclerView.Adapter<OrderDetailRefundListAdapter.ViewHolder>() {
    private var items = ArrayList<Refund>()

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): ViewHolder {
        return ViewHolder(parent, order, formatCurrency, onRefundDetailsClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<Refund>) {
        val diffResult = DiffUtil.calculateDiff(RefundModelDiffCallback(items, newItems))
        items = ArrayList(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(
        parent: ViewGroup,
        private val order: Order,
        private val formatCurrency: (BigDecimal) -> String,
        private val onRefundDetailsClicked: (Long, Long) -> Unit
    ) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.order_detail_refund_payment_item, parent, false)
    ) {
        private val amount: TextView = itemView.findViewById(R.id.refundsList_refundAmount)
        private val method: TextView = itemView.findViewById(R.id.refundsList_refundMethod)

        @SuppressLint("SetTextI18n") fun bind(refund: Refund) {
            amount.text = "-${formatCurrency(refund.amount)}"

            val linkText = itemView.resources.getString(R.string.orderdetail_refund_view_details)
            val methodText = itemView.resources.getString(R.string.orderdetail_refund_detail).format(
                    DateFormat.getMediumDateFormat(itemView.context).format(refund.dateCreated),
                    order.paymentMethodTitle,
                    linkText
            )
            val spannable = SpannableString(methodText)
            val start = methodText.indexOf(linkText)
            spannable.setSpan(
                    WooClickableSpan {
                        onRefundDetailsClicked(order.remoteId, refund.id)
                    },
                    start,
                    start + linkText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            method.setText(spannable, TextView.BufferType.SPANNABLE)
            method.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    class RefundModelDiffCallback(
        private val oldList: List<Refund>,
        private val newList: List<Refund>
    ) : Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val old = oldList[oldItemPosition]
            val new = newList[newItemPosition]
            return old.amount isEqualTo new.amount &&
                    old.dateCreated == new.dateCreated &&
                    old.reason == new.reason &&
                    old.items == new.items
        }
    }
}