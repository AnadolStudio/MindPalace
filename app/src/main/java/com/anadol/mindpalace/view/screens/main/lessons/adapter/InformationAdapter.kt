package com.anadol.mindpalace.view.screens.main.lessons.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.anadol.mindpalace.R
import com.anadol.mindpalace.databinding.ItemLessonBinding

class InformationAdapter : RecyclerView.Adapter<InformationViewHolder>() {

    val data = InformationData.values().toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder = InformationViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_lesson, parent, false)
    )

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size
}

class InformationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemLessonBinding.bind(view)

    fun bind(data: InformationData) {

        binding.lessonContainer.setOnClickListener {
            data.detailIsVisible =  !binding.detailsContainer.isVisible
            binding.detailsContainer.isVisible = data.detailIsVisible
        }

        binding.detailsContainer.isVisible = data.detailIsVisible
        binding.icon.setImageDrawable(ContextCompat.getDrawable(itemView.context, data.iconId))
        binding.text.text = itemView.context.getString(data.textId)

        binding.detailsContainer.removeAllViews()
        binding.detailsContainer.addView(
            LayoutInflater.from(itemView.context).inflate(data.detailLayoutId, binding.detailsContainer, false)
        )
    }
}

enum class InformationData(
    @LayoutRes val detailLayoutId: Int,
    @DrawableRes val iconId: Int,
    @StringRes val textId: Int,
    var detailIsVisible: Boolean = false
) {
    BASICS(R.layout.layout_lesson_basics, R.drawable.ic_lesson_1, R.string.basics),
    ASSOCIATION(R.layout.layout_lesson_association, R.drawable.ic_lesson_2, R.string.what_is_an_association),
    REPEAT(R.layout.layout_lesson_repeating, R.drawable.ic_lesson_3, R.string.how_to_repeat_correctly),
    NUMBERS(R.layout.layout_lesson_numbers, R.drawable.ic_lesson_4, R.string.numbers),
    TEXTS(R.layout.layout_lesson_texts, R.drawable.ic_lesson_5, R.string.texts),
    DATES(R.layout.layout_lesson_dates, R.drawable.ic_lesson_6, R.string.dates),
}
