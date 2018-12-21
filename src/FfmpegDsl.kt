
abstract class ArgumentPart(val args: Array<String>) {
    private val children: ArrayList<ArgumentPart> = arrayListOf()

    protected fun <T : ArgumentPart> addPart(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    protected fun toCommand(accumulator: ArrayList<String>) {
        accumulator.addAll(args)
        children.forEach { it.toCommand(accumulator) }
    }
}

class Ffmpeg : ArgumentPart(arrayOf("ffmpeg")) {
    fun input(init: Input.() -> Unit) = addPart(Input(), init)

    fun output(init: Output.() -> Unit) = addPart(Output(), init)

    fun toCommand(): Array<String>? {
        val acc = arrayListOf<String>()
        toCommand(acc)
        return acc.toTypedArray()
    }
}

class Input : ArgumentPart(arrayOf("-i")) {
    fun value(value: String) = addPart(Value(value)) {}
}

class Output : ArgumentPart(arrayOf()) {
    fun value(value: String) = addPart(Value(value)) {}

    fun videoQuality(quality: Int) = addPart(VideoQuality(quality)) {}

    fun video(codec: VideoCodecs) = addPart(VideoCodec(codec)) {}

    fun audio(codec: AudioCodecs) = addPart(AudioCodec(codec)) {}
}

enum class VideoCodecs(val type: String) {
    H264("libx264")
}

enum class AudioCodecs(val type: String) {
    AAC("aac")
}

class VideoQuality(quality: Int) : ArgumentPart(arrayOf("-s", "hd$quality"))

//TODO how to do this without extends? if you want to check type and add specifics?
class VideoCodec(codec: VideoCodecs) : ArgumentPart(arrayOf("-c:v", codec.type, "-crf", "23"))

class AudioCodec(codec: AudioCodecs) : ArgumentPart(arrayOf("-c:a", codec.type, "-strict", "-2"))

class Value(v: String) : ArgumentPart(arrayOf(v))

fun ffmpeg(init: Ffmpeg.() -> Unit): Ffmpeg {
    val ffmpeg = Ffmpeg()
    ffmpeg.init()
    return ffmpeg
}