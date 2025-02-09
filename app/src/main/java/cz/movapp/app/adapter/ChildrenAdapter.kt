package cz.movapp.app.adapter

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.android.playSound
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.R
import cz.movapp.app.databinding.ChildrenItemBinding
import cz.movapp.app.ui.children.ChildrenData
import java.io.IOException

class ChildrenAdapter (
    private val dataset: List<ChildrenData>
): RecyclerView.Adapter<ChildrenAdapter.ItemViewHolder>() {

    var langPair = LanguagePair.getDefault()

    class ItemViewHolder(binding: ChildrenItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ChildrenItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        val context = holder.binding.root.context

        /* for dark theme, use white color as tint */
        when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                holder.binding.imageChildrenMain.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            }
            else -> {
                holder.binding.imageChildrenMain.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
            }
        }

        holder.binding.apply {
            try {
                val imageStream = context.assets.open(item.image_path)
                imageChildrenMain.setImageDrawable(Drawable.createFromStream(imageStream, null))
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                imageChildrenMain.setImageDrawable(null)
            }

            if (!langPair.isReversed) {
                textChildrenFrom.text = formatTrans(item.main_translation, item.main_transcription)
                textChildrenTo.text = formatTrans(item.source_translation, item.source_transcription)

                imageChildrenFlagFrom.setImageResource(R.drawable.cz)
                imageChildrenFlagTo.setImageResource(R.drawable.ua)

                imagePlaySoundFrom.setOnClickListener {
                    playSound(holder.itemView.context, item.main_sound_local)
                }
                imagePlaySoundTo.setOnClickListener {
                    playSound(holder.itemView.context, item.source_sound_local)
                }
            } else {
                textChildrenFrom.text = formatTrans(item.source_translation, item.source_transcription)
                textChildrenTo.text = formatTrans(item.main_translation, item.main_transcription)

                imageChildrenFlagFrom.setImageResource(R.drawable.ua)
                imageChildrenFlagTo.setImageResource(R.drawable.cz)

                imagePlaySoundFrom.setOnClickListener {
                    playSound(holder.itemView.context, item.source_sound_local)
                }
                imagePlaySoundTo.setOnClickListener {
                    playSound(holder.itemView.context, item.main_sound_local)
                }
            }
        }
    }

    private fun formatTrans(translation: String, transcription: String) : String {
        return "$translation [${transcription}]"
    }
}