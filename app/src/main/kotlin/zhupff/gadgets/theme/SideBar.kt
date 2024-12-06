package zhupff.gadgets.theme

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import zhupff.gadgets.basic.dp
import zhupff.gadgets.theme.databinding.SideBarLayoutBinding
import zhupff.gadgets.theme.databinding.StringDividerItemViewBinding
import zhupff.gadgets.theme.databinding.ThemeItemViewWrapperBinding

class SideBar(
    val activity: MainActivity,
    val binding: SideBarLayoutBinding,
) {

    val themeListAdapter = ThemeListAdapter()

    init {
        binding.AssetsThemeList.layoutManager = GridLayoutManager(activity, 6, RecyclerView.VERTICAL, false).also {
            it.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = when (themeListAdapter.getItemViewType(position)) {
                    ThemeListAdapter.ITEM_TYPE_ORIGIN_THEME -> 3
                    ThemeListAdapter.ITEM_TYPE_ASSETS_THEME -> 2
                    ThemeListAdapter.ITEM_TYPE_STR -> 6
                    else -> throw IllegalStateException()
                }
            }
        }
        binding.AssetsThemeList.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                when (themeListAdapter.getItemViewType(position)) {
                    ThemeListAdapter.ITEM_TYPE_ORIGIN_THEME -> {
                        when (position % 2) {
                            0 -> outRect.set(12.dp, 5.dp, 6.dp, 0)
                            1 -> outRect.set(6.dp, 5.dp, 12.dp, 0)
                        }
                    }
                    ThemeListAdapter.ITEM_TYPE_ASSETS_THEME -> {
                        when (position % 3) {
                            0 -> outRect.set(12.dp, 10.dp, 4.dp, 0)
                            1 -> outRect.set(8.dp, 10.dp, 8.dp, 0)
                            2 -> outRect.set(4.dp, 10.dp, 12.dp, 0)
                        }
                    }
                    ThemeListAdapter.ITEM_TYPE_STR -> {
                        outRect.set(0, 10.dp, 0, 0)
                    }
                }
            }
        })
        binding.AssetsThemeList.adapter = themeListAdapter
        themeListAdapter.update()
    }



    class ThemeListAdapter : RecyclerView.Adapter<ThemeListAdapter.VH>() {
        companion object {
            const val ITEM_TYPE_ORIGIN_THEME = 1
            const val ITEM_TYPE_ASSETS_THEME = 10
            const val ITEM_TYPE_STR = 20
        }

        private val data: MutableList<Any> = ArrayList()
        private var snapshot: List<Pair<String, String>> = emptyList()

        fun update() {
            val newData = listOf<Any>(
                ThemeUtil.LIGHT, ThemeUtil.DARK,
                "以下是来自assets的资源",
            ) + ThemeUtil.assetsThemes
            val newSnapshot = newData.map {
                if (it is Theme) {
                    it.name to it.name
                } else if (it is String) {
                    it to it
                } else if (it is AssetsTheme) {
                    it.name to (it.theme != null).toString()
                } else it.toString() to it.toString()
            }
            val differ = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = snapshot.size
                override fun getNewListSize(): Int = newSnapshot.size
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    snapshot[oldItemPosition].first == newSnapshot[oldItemPosition].first
                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    snapshot[oldItemPosition].second == newSnapshot[oldItemPosition].second
            })
            data.clear()
            data.addAll(newData)
            snapshot = newSnapshot
            differ.dispatchUpdatesTo(this)
        }

        override fun getItemCount(): Int = data.size

        override fun getItemViewType(position: Int): Int = when (val item = data[position]) {
            ThemeUtil.LIGHT, ThemeUtil.DARK -> ITEM_TYPE_ORIGIN_THEME
            is AssetsTheme -> ITEM_TYPE_ASSETS_THEME
            is String -> ITEM_TYPE_STR
            else -> throw IllegalStateException()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = when (viewType) {
            ITEM_TYPE_ORIGIN_THEME -> OriginThemeItemVH(ThemeItemViewWrapperBinding.inflate(LayoutInflater.from(parent.context)))
            ITEM_TYPE_ASSETS_THEME -> AssetsThemeItemVH(ThemeItemViewWrapperBinding.inflate(LayoutInflater.from(parent.context)))
            ITEM_TYPE_STR -> StringDividerItemVH(StringDividerItemViewBinding.inflate(LayoutInflater.from(parent.context)))
            else -> throw IllegalStateException()
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            when (getItemViewType(position)) {
                ITEM_TYPE_ORIGIN_THEME -> (holder as OriginThemeItemVH).bind(data[position])
                ITEM_TYPE_ASSETS_THEME -> (holder as AssetsThemeItemVH).bind(data[position])
                ITEM_TYPE_STR -> (holder as StringDividerItemVH).bind(data[position])
            }
        }

        abstract class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            abstract fun bind(item: Any)
        }
    }

    class OriginThemeItemVH(val binding: ThemeItemViewWrapperBinding) : ThemeListAdapter.VH(binding.root) {
        override fun bind(item: Any) {
            val item = item as? Theme ?: return
            binding.ThemeConstraintLayout.Title.text = item.name
            binding.ThemeConstraintLayout.root.theme = item
            binding.root.setOnClickListener { ThemeUtil.switch(item) }
        }
    }

    class AssetsThemeItemVH(val binding: ThemeItemViewWrapperBinding) : ThemeListAdapter.VH(binding.root) {
        override fun bind(item: Any) {
            val item = item as? AssetsTheme ?: return
            binding.ThemeConstraintLayout.Title.text = item.name
            binding.ThemeConstraintLayout.Mask.text = "${item.name}\n未下载"
            item.theme?.let { binding.ThemeConstraintLayout.root.theme = it }
            binding.root.setOnClickListener {
                if (item.theme == null) {
                    ThemeUtil.downloadFromAssets(item.name)
                    item.theme?.let { theme ->
                        binding.ThemeConstraintLayout.root.theme = theme
                    }
                }
                ThemeUtil.switch(item.theme!!)
            }
        }
    }

    class StringDividerItemVH(val binding: StringDividerItemViewBinding) : ThemeListAdapter.VH(binding.root) {
        override fun bind(item: Any) {
            val item = item as? String ?: return
            binding.text.text = item
        }
    }
}