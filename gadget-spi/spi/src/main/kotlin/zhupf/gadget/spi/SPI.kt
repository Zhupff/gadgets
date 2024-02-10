package zhupf.gadget.spi

class SPI(
    private val classLoader: ClassLoader,
) {

    constructor() : this(Thread.currentThread().contextClassLoader)

    fun read(cls: String): List<String> =
        classLoader
            .getResources("META-INF/services/$cls")
            .toList()
            .flatMap { it.openStream().reader(Charsets.UTF_8).readLines() }

    fun read(cls: Class<*>): List<String> =
        read(cls.canonicalName)

    fun load(cls: String): List<Class<*>> =
        read(cls).map { Class.forName(it) }

    fun <T> load(cls: Class<T>): List<Class<T>> =
        read(cls).map { Class.forName(it) as Class<T> }

    fun create(cls: String): List<Any> =
        load(cls).map { it.getConstructor().newInstance() }

    fun create(cls: String, filter: (Class<*>) -> Boolean = { true }): List<Any> =
        load(cls).filter(filter).map { it.getConstructor().newInstance() }

    fun  createOne(cls: String, filter: (List<Class<*>>) -> Class<*>?): Any? =
        filter.invoke(load(cls))?.getConstructor()?.newInstance()

    fun <T> create(cls: Class<T>): List<T> =
        load(cls).map { it.getConstructor().newInstance() }

    fun <T> create(cls: Class<T>, filter: (Class<T>) -> Boolean = { true }): List<T> =
        load(cls).filter(filter).map { it.getConstructor().newInstance() }

    fun <T> createOne(cls: Class<T>, filter: (List<Class<T>>) -> Class<T>?): T? =
        filter.invoke(load(cls))?.getConstructor()?.newInstance()
}

inline fun <reified T> SPI.read(): List<String> =
    read(T::class.java)

inline fun <reified T> SPI.load(): List<Class<T>> =
    load(T::class.java)

inline fun <reified T> SPI.create(): List<T> =
    create(T::class.java)

inline fun <reified T> SPI.create(noinline filter: (Class<T>) -> Boolean = { true }): List<T> =
    create(T::class.java, filter)

inline fun <reified T> SPI.createOne(noinline filter: (List<Class<T>>) -> Class<T>?): T? =
    createOne(T::class.java, filter)