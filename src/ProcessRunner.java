import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class ProcessRunner {

    public void run(String[] command) throws IOException, InterruptedException {
        //ffmpeg does not like it when the file to output already exists!
        log("Running command: " + Arrays.toString(command));
        ProcessBuilder ffmpeg = new ProcessBuilder(command).redirectErrorStream(true);
        Process process = ffmpeg.start();
        CountDownLatch latch = new CountDownLatch(1);
        process.onExit().thenAccept(p -> {
            int exitValue = process.exitValue();
            if (exitValue == 0) {
                log(p.info().toString());
            } else {
                try (InputStream errorStream = process.getErrorStream()) {
                    log(new String(errorStream.readAllBytes()));
                } catch (IOException e) {
                    log("error reading error stream");
                }
            }
            latch.countDown();
        });
        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
            //try (InputStream inputStream = process.getInputStream()) {
            Thread readThread = new Thread(() -> {
                try {
                    String line = inputStream.readLine();
                    while (line != null) {
                        System.out.println(line);
                        line = inputStream.readLine();
                    }
                } catch (IOException e) {
                    log("error while reading process inputstream");
                }
            });
            readThread.start();
            //await latch is just to keep the Main thread alive
            latch.await();
        }
    }

    private static void log(String logLine) {
        System.out.println(Thread.currentThread().getName() + " " + logLine);
    }

}
