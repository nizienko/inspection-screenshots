import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.stepsProcessing.StepLogger
import com.intellij.remoterobot.stepsProcessing.StepWorker
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import utils.IdeaToLaunch
import utils.paintToFile
import java.nio.file.Path

class LaunchIdeaTest {
    companion object {
        val idea = IdeaToLaunch()

        @BeforeAll
        @JvmStatic
        fun startIdea() {
            StepWorker.registerProcessor(StepLogger())
            idea.launch()
        }

        @AfterAll
        @JvmStatic
        fun cleanUp() {
            idea.stop()
        }
    }


    @Test
    fun launchIdeaAndPaintWelcomeFrame() {
        val remoteRobot = idea.getRemoteRobot()
        // about searching: https://github.com/JetBrains/intellij-ui-test-robot#searching-components
        val welcomeFrame = remoteRobot.find<CommonContainerFixture>(byXpath("//div[@class='FlatWelcomeFrame']"))
        welcomeFrame.paintToFile(Path.of("welcomeFrame.png"))
    }
}

