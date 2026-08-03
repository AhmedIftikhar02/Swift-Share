package com.example.swiftshare.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.example.swiftshare.R
import com.example.swiftshare.common.extensions.gone
import com.example.swiftshare.common.extensions.visible
import com.google.android.material.button.MaterialButton

class StateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val progressBar: ProgressBar

    private val errorContainer: LinearLayout
    private val tvErrorMessage: TextView
    private val btnRetry: MaterialButton

    private val emptyContainer: LinearLayout
    private val ivEmpty: ImageView
    private val tvEmptyTitle: TextView
    private val tvEmptySubtitle: TextView
    private val btnEmptyRetry: MaterialButton

    private var contentView: View? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_state_layout, this, true)

        progressBar = findViewById(R.id.progressBar)

        errorContainer = findViewById(R.id.errorContainer)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)
        btnRetry = findViewById(R.id.btnRetry)

        emptyContainer = findViewById(R.id.emptyContainer)
        ivEmpty = findViewById(R.id.ivEmpty)
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle)
        tvEmptySubtitle = findViewById(R.id.tvEmptySubtitle)
        btnEmptyRetry = findViewById(R.id.btnEmptyRetry)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (
                child.id != R.id.progressBar &&
                child.id != R.id.errorContainer &&
                child.id != R.id.emptyContainer
            ) {
                contentView = child
                break
            }
        }
    }

    fun showLoading() {
        progressBar.visible()

        errorContainer.gone()
        emptyContainer.gone()

        contentView?.gone()
    }

    fun showContent() {
        progressBar.gone()

        errorContainer.gone()
        emptyContainer.gone()

        contentView?.visible()
    }

    fun showError(
        message: String?,
        onRetry: (() -> Unit)? = null
    ) {

        progressBar.gone()
        emptyContainer.gone()
        contentView?.gone()

        errorContainer.visible()

        tvErrorMessage.text =
            message ?: context.getString(R.string.error_generic)

        if (onRetry != null) {
            btnRetry.visible()
            btnRetry.setOnClickListener { onRetry() }
        } else {
            btnRetry.gone()
        }
    }

    fun showEmpty(
        title: String,
        subtitle: String,
        retryLabel: String? = null,
        @DrawableRes imageRes: Int = R.drawable.ic_empty_state,
        onRetry: (() -> Unit)? = null
    ) {

        progressBar.gone()
        errorContainer.gone()
        contentView?.gone()

        emptyContainer.visible()

        ivEmpty.setImageResource(imageRes)

        tvEmptyTitle.text = title
        tvEmptySubtitle.text = subtitle

        if (retryLabel != null && onRetry != null) {
            btnEmptyRetry.visible()
            btnEmptyRetry.text = retryLabel
            btnEmptyRetry.setOnClickListener {
                onRetry()
            }
        } else {
            btnEmptyRetry.gone()
        }
    }
}