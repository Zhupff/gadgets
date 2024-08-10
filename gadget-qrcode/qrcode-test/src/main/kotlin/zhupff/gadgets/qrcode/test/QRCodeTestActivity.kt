package zhupff.gadgets.qrcode.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView
import zhupff.gadgets.logger.logD
import zhupff.gadgets.logger.logI
import zhupff.gadgets.qrcode.test.databinding.QrcodeItemViewBinding
import zhupff.gadgets.qrcode.test.databinding.QrcodeTestActivityBinding

class QRCodeTestActivity : AppCompatActivity() {

    private val TAG = "QRCodeTestActivity(${hashCode()})"

    private val binding by lazy { QrcodeTestActivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.QrCodeList.adapter = AD(listOf(
            "ブルーアイズ·ホワイト·ドラゴン",
            "空山无人，遥望星辰",
            "https://www.google.com",
            "1two三よん",
        ))

        binding.CaptureAndParse.setOnClickListener { captureAndParse() }
        binding.ParseLocal.setOnClickListener { parseLocal() }
        binding.Scan.setOnClickListener { scan() }
    }

    private fun captureAndParse() {
        val capture = binding.root.drawToBitmap()
        val results = QrCodeUtil.decodeMultiple(capture)
        val points = results?.flatMap { it.resultPoints.toList() } ?: emptyList()
        binding.QrCodePointView.points = points
        TAG.logI("capture and parse: ${results?.map { it.text }}")
    }

    private fun parseLocal() {
        listOf(
            ResourcesCompat.getDrawable(resources, R.drawable.qr1, null),
            ResourcesCompat.getDrawable(resources, R.drawable.qr2, null),
            ResourcesCompat.getDrawable(resources, R.drawable.qr3, null),
            ResourcesCompat.getDrawable(resources, R.drawable.qr4, null),
        ).forEach { drawable ->
            val bitmap = drawable!!.toBitmap()
            val result = QrCodeUtil.decode(bitmap)
            TAG.logD(result?.text)
        }
    }

    private fun scan() {
        supportFragmentManager.beginTransaction()
            .add(binding.fragmentContainer.id, ScanFragment())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }


    private class VH(val binding: QrcodeItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(content: String) {
            binding.OriginContent.text = content
            val qrCode = QrCodeUtil.encode(content)
            binding.QrCode.setImageBitmap(qrCode)
            val result = QrCodeUtil.decode(qrCode)
            if (result == null) {
                binding.DecodeContent.text = ""
                binding.Info.text = ""
            } else {
                binding.DecodeContent.text = result.text ?: ""
                binding.Info.text = StringBuilder()
                    .appendLine("")
                    .toString()
            }
        }
    }

    private class AD(val contents: List<String>) : RecyclerView.Adapter<VH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(QrcodeItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(contents[position])
        }
        override fun getItemCount(): Int = contents.size
    }
}