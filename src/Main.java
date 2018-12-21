import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new ProcessRunner().run(new String[]{
                "/usr/bin/ffmpeg",
                "-i", "/home/tim/Downloads/bbb_sunflower_1080p_30fps_clip.mp4",
                "-s", "hd720", "-c:v", "libx264", "-crf", "23", "-c:a", "aac", "-strict", "-2", "output.mp4"
        });
    }
}
