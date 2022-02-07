package utils

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.launcher.Ide
import com.intellij.remoterobot.launcher.IdeDownloader
import com.intellij.remoterobot.launcher.IdeLauncher
import com.intellij.remoterobot.utils.waitFor
import okhttp3.OkHttpClient
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

fun RemoteRobot.isAvailable(): Boolean = runCatching {
    callJs<Boolean>("true")
}.getOrDefault(false)


// about running code in idea runtime: https://github.com/JetBrains/intellij-ui-test-robot#getting-data-from-a-real-component
fun ComponentFixture.paintToFile(path: Path) = callJs<ByteArray>(
    """
                        importPackage(java.io)
                        importPackage(javax.imageio)
                        importPackage(java.awt.image)
                        const screenShot = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        component.paint(screenShot.getGraphics())
                        let pictureBytes;
                        const baos = new ByteArrayOutputStream();
                        try {
                            ImageIO.write(screenShot, "png", baos);
                            pictureBytes = baos.toByteArray();
                        } finally {
                          baos.close();
                        }
                        pictureBytes;   
            """, true
).apply { path.toFile().writeBytes(this) }


class IdeaToLaunch {
    private var ideaProcess: Process? = null
    private var tmpDir: Path = Files.createTempDirectory("launcher")
    private lateinit var remoteRobot: RemoteRobot

    fun launch() {
        val client = OkHttpClient()
        remoteRobot = RemoteRobot("http://localhost:8082", client)
        val ideDownloader = IdeDownloader(client)
        ideaProcess = IdeLauncher.launchIde(
            ideDownloader.downloadAndExtractLatestEap(Ide.IDEA_COMMUNITY, tmpDir),
            mapOf("robot-server.port" to 8082),
            emptyList(),
            listOf(ideDownloader.downloadRobotPlugin(tmpDir)),
            tmpDir
        )
        waitFor(Duration.ofSeconds(90), Duration.ofSeconds(5)) {
            remoteRobot.isAvailable()
        }
    }
    fun stop() {
        ideaProcess?.destroy()
        tmpDir.toFile().deleteRecursively()
    }

    fun getRemoteRobot() = remoteRobot
}