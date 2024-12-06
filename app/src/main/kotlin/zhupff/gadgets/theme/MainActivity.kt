package zhupff.gadgets.theme

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import zhupff.gadgets.theme.databinding.AlbumItemViewBinding
import zhupff.gadgets.theme.databinding.MainActivityBinding
import zhupff.gadgets.theme.databinding.MusicItemViewBinding
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity(), ThemeDispatcher {

    private val binding by lazy { MainActivityBinding.inflate(layoutInflater) }
    private val windowInsetsControllerCompat by lazy { WindowInsetsControllerCompat(window, window.decorView) }

    private lateinit var sideBar: SideBar

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtil.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sideBar = SideBar(this, binding.SideBar)
        binding.Logo.setOnClickListener {
            binding.Drawer.openDrawer(binding.SideBar.root)
        }

        binding.AppBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percent = verticalOffset.absoluteValue.toFloat() / appBarLayout.totalScrollRange.toFloat()
            binding.WallpaperMask.alpha = percent

            binding.SearchLayout.let {
                it.setPadding((binding.Logo.width * 1.2F * percent).toInt(), it.paddingTop, it.paddingRight, it.paddingBottom)
            }
        }

        binding.AlbumList.adapter = AlbumListAdapter()
        binding.AlbumList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildAdapterPosition(view)
                outRect.left = if (position <= 0) 48 else 0
                outRect.right = 48
            }
        })
        binding.MusicList.adapter = MusicListAdapter()

        observableTheme().observe(this) { theme ->
            val isDark = ThemeUtil.isDarkTheme(theme)
            windowInsetsControllerCompat.isAppearanceLightStatusBars = !isDark
        }
    }

    override fun observableTheme(): LiveData<Theme> = ThemeUtil.current



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
}