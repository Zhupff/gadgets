package zhupff.gadgets.transform

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.util.concurrent.atomic.AtomicBoolean

class ClassNodeWrapper(
    bytes: ByteArray,
) : ClassNode() {

    private val dirty = AtomicBoolean(false)

    private val reader = ClassReader(bytes).also {
        it.accept(this, ClassReader.EXPAND_FRAMES)
    }

    fun toByteArray(): ByteArray {
        val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        accept(writer)
        return writer.toByteArray()
    }

    fun markDirty() {
        dirty.set(true)
    }

    fun isDirty(): Boolean = dirty.get()
}