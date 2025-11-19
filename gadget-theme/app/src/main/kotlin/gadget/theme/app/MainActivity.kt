package gadget.theme.app

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import gadget.basic.dp
import gadget.theme.Theme
import gadget.theme.app.databinding.AlbumItemViewBinding
import gadget.theme.app.databinding.MainActivityBinding
import gadget.theme.app.databinding.MusicItemViewBinding
import gadget.theme.app.databinding.StringItemViewBinding
import gadget.theme.app.databinding.ThemeItemViewBinding
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {

    private val viewBinding: MainActivityBinding by lazy {
        MainActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CustomThemeFactory.inject(this)
        setContentView(viewBinding.root)
        viewBinding.Logo.setOnClickListener {
            viewBinding.Drawer.openDrawer(viewBinding.Sidebar)
        }
        viewBinding.AppBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percent = verticalOffset.absoluteValue.toFloat() / appBarLayout.totalScrollRange.toFloat()
            viewBinding.WallpaperMask.alpha = percent

            viewBinding.SearchLayout.let {
                it.setPadding((viewBinding.Logo.width * 1.2F * percent).toInt(), it.paddingTop, it.paddingRight, it.paddingBottom)
            }
        }
        viewBinding.AlbumList.adapter = AlbumListAdapter()
        viewBinding.AlbumList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildAdapterPosition(view)
                outRect.left = if (position <= 0) 48 else 0
                outRect.right = 48
            }
        })
        viewBinding.MusicList.adapter = MusicListAdapter()
        viewBinding.ThemeList.adapter = ThemeListAdapter()
        viewBinding.ThemeList.layoutManager = GridLayoutManager(this, 6, RecyclerView.VERTICAL, false).also {
            it.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = when ((viewBinding.ThemeList.adapter as ThemeListAdapter).getItemViewType(position)) {
                    ThemeListAdapter.ITEM_TYPE_ORIGIN_THEME -> 3
                    ThemeListAdapter.ITEM_TYPE_ASSETS_THEME -> 2
                    ThemeListAdapter.ITEM_TYPE_STR -> 6
                    else -> throw IllegalStateException()
                }
            }
        }
        viewBinding.ThemeList.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                when ((viewBinding.ThemeList.adapter as ThemeListAdapter).getItemViewType(position)) {
                    ThemeListAdapter.ITEM_TYPE_ORIGIN_THEME -> {
                        when (position % 2) {
                            0 -> outRect.set(12.dp.toInt(), 5.dp.toInt(), 6.dp.toInt(), 0)
                            1 -> outRect.set(6.dp.toInt(), 5.dp.toInt(), 12.dp.toInt(), 0)
                        }
                    }
                    ThemeListAdapter.ITEM_TYPE_ASSETS_THEME -> {
                        when (position % 3) {
                            0 -> outRect.set(12.dp.toInt(), 10.dp.toInt(), 4.dp.toInt(), 0)
                            1 -> outRect.set(8.dp.toInt(), 10.dp.toInt(), 8.dp.toInt(), 0)
                            2 -> outRect.set(4.dp.toInt(), 10.dp.toInt(), 12.dp.toInt(), 0)
                        }
                    }
                    ThemeListAdapter.ITEM_TYPE_STR -> {
                        outRect.set(0, 10.dp.toInt(), 0, 0)
                    }
                }
            }
        })
    }

    private class AlbumListAdapter : RecyclerView.Adapter<AlbumListAdapter.AlbumItemVH>() {
        override fun getItemCount(): Int = 10
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumItemVH =
            AlbumItemVH(AlbumItemViewBinding.inflate(LayoutInflater.from(parent.context)))
        override fun onBindViewHolder(holder: AlbumItemVH, position: Int) {
            holder.binding.Name.text = "Album ${position + 1}"
        }
        private class AlbumItemVH(val binding: AlbumItemViewBinding) : RecyclerView.ViewHolder(binding.root)
    }

    private class MusicListAdapter : RecyclerView.Adapter<MusicListAdapter.MusicItemVH>() {
        override fun getItemCount() = 99
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicItemVH =
            MusicItemVH(MusicItemViewBinding.inflate(LayoutInflater.from(parent.context)))
        override fun onBindViewHolder(holder: MusicItemVH, position: Int) {
            holder.binding.tvNumber.text = (position + 1).toString()
        }
        private class MusicItemVH(val binding: MusicItemViewBinding) : RecyclerView.ViewHolder(binding.root)
    }

    private class ThemeListAdapter : RecyclerView.Adapter<ThemeListAdapter.VH>() {
        companion object {
            const val ITEM_TYPE_ORIGIN_THEME = 1
            const val ITEM_TYPE_ASSETS_THEME = 2
            const val ITEM_TYPE_STR = 3
        }
        private val data: List<Any> = listOf(App.INSTANCE.lightTheme, App.INSTANCE.nightTheme, "以下是来自assets的资源") + App.INSTANCE.otherTheme
        override fun getItemCount(): Int = data.size
        override fun getItemViewType(position: Int): Int = when (data[position]) {
            App.INSTANCE.lightTheme, App.INSTANCE.nightTheme -> ITEM_TYPE_ORIGIN_THEME
            is Theme -> ITEM_TYPE_ASSETS_THEME
            is String -> ITEM_TYPE_STR
            else -> throw IllegalStateException()
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = when (viewType) {
            ITEM_TYPE_ORIGIN_THEME,
            ITEM_TYPE_ASSETS_THEME -> ThemeItemVH(ThemeItemViewBinding.inflate(LayoutInflater.from(parent.context)), viewType)
            ITEM_TYPE_STR -> StringItemVH(StringItemViewBinding.inflate(LayoutInflater.from(parent.context)))
            else -> throw IllegalStateException()
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            when (getItemViewType(position)) {
                ITEM_TYPE_ORIGIN_THEME,
                ITEM_TYPE_ASSETS_THEME -> (holder as ThemeItemVH).bind(data[position])
                ITEM_TYPE_STR -> (holder as StringItemVH).bind(data[position])
            }
        }
        abstract class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            abstract fun bind(item: Any)
        }
        class ThemeItemVH(val binding: ThemeItemViewBinding, val viewType: Int) : VH(binding.root) {
            override fun bind(item: Any) {
                val theme = item as? Theme ?: return
                binding.Title.text = theme.id
                if (viewType == ITEM_TYPE_ORIGIN_THEME) {
                    binding.Title.textSize = 15F
                    binding.root.borderWidth = 8.dp
                } else {
                    binding.Title.textSize = 10F
                    binding.root.borderWidth = 3.dp
                }
                binding.root.theme = theme
                binding.root.setOnClickListener {
                    App.INSTANCE.switch(theme)
                }
            }
        }
        class StringItemVH(val binding: StringItemViewBinding) : VH(binding.root) {
            override fun bind(item: Any) {
                binding.text.text = item as? String ?: return
            }
        }
    }
}