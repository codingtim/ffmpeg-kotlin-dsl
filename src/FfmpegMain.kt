import AudioCodecs.AAC
import VideoCodecs.H264

fun main(args: Array<String>) {
    val ffmpeg = ffmpeg {
        input { value("/home/tim/Downloads/bbb_sunflower_1080p_30fps_clip.mp4") }
        for(q in arrayOf(720, 480)) {
            output {
                videoQuality(q)
                video(H264)
                audio(AAC)
                value("output_$q.mp4")
            }
        }
    }
    ProcessRunner().run(ffmpeg.toCommand())
}