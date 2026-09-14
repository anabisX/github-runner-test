package jp.co.personal.githubrunnertest

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.format.Formatter
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private lateinit var informationText: TextView
    private lateinit var switchInformationButton: Button
    private var showingBuildInformation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        informationText = findViewById(R.id.informationText)
        switchInformationButton = findViewById(R.id.switchInformationButton)
        switchInformationButton.setOnClickListener {
            showingBuildInformation = !showingBuildInformation
            updateInformation()
        }
        updateInformation()
    }

    private fun updateInformation() {
        if (showingBuildInformation) {
            informationText.text = buildInformation()
            switchInformationButton.setText(R.string.show_runtime_information)
        } else {
            informationText.text = runtimeInformation()
            switchInformationButton.setText(R.string.show_build_information)
        }
    }

    private fun buildInformation(): String = lines(
        getString(R.string.section_build),
        "Build time (UTC)" to BuildConfig.BUILD_TIME_UTC,
        "Build host" to BuildConfig.BUILD_HOST,
        "Java" to BuildConfig.BUILD_JAVA_VERSION,
        "Java vendor" to BuildConfig.BUILD_JAVA_VENDOR,
        "Gradle" to BuildConfig.BUILD_GRADLE_VERSION,
        "Build OS" to BuildConfig.BUILD_OS,
        "CI" to BuildConfig.BUILD_CI,
        "GitHub Actions" to BuildConfig.BUILD_GITHUB_ACTIONS,
        "Workflow" to BuildConfig.BUILD_GITHUB_WORKFLOW,
        "Job" to BuildConfig.BUILD_GITHUB_JOB,
        "Run ID / attempt" to "${BuildConfig.BUILD_GITHUB_RUN_ID} / ${BuildConfig.BUILD_GITHUB_RUN_ATTEMPT}",
        "Git ref" to BuildConfig.BUILD_GIT_REF,
        "Git SHA" to BuildConfig.BUILD_GIT_SHA,
        "Build type" to BuildConfig.BUILD_TYPE,
        "App version" to "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    )

    private fun runtimeInformation(): String {
        val metrics = resources.displayMetrics
        val runtime = Runtime.getRuntime()
        val configuration = resources.configuration
        val locales = (0 until configuration.locales.size())
            .joinToString(", ") { configuration.locales[it].toLanguageTag() }

        return lines(
            getString(R.string.section_runtime),
            "Device" to "${Build.MANUFACTURER} ${Build.MODEL}",
            "Android" to "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            "Build ID" to Build.DISPLAY,
            "Device / product" to "${Build.DEVICE} / ${Build.PRODUCT}",
            "Hardware / board" to "${Build.HARDWARE} / ${Build.BOARD}",
            "Supported ABIs" to Build.SUPPORTED_ABIS.joinToString(),
            "Java runtime" to System.getProperty("java.runtime.version", "unknown"),
            "VM" to System.getProperty("java.vm.name", "unknown"),
            "Processors" to runtime.availableProcessors().toString(),
            "Max heap" to Formatter.formatFileSize(this, runtime.maxMemory()),
            "Screen pixels" to "${metrics.widthPixels} x ${metrics.heightPixels}",
            "Density" to "${metrics.densityDpi} dpi (${densityName(metrics)})",
            "Font scale" to configuration.fontScale.toString(),
            "UI mode" to uiModeName(configuration),
            "Locales" to locales.ifBlank { Locale.getDefault().toLanguageTag() },
            "Time zone" to TimeZone.getDefault().id,
            "Package" to packageName
        )
    }

    private fun lines(title: String, vararg values: Pair<String, String>): String = buildString {
        appendLine(title)
        appendLine("=".repeat(title.length.coerceAtLeast(8)))
        values.forEach { (name, value) -> appendLine("$name: $value") }
    }.trimEnd()

    private fun densityName(metrics: DisplayMetrics): String = when (metrics.densityDpi) {
        DisplayMetrics.DENSITY_LOW -> "ldpi"
        DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
        DisplayMetrics.DENSITY_TV -> "tvdpi"
        DisplayMetrics.DENSITY_HIGH -> "hdpi"
        DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
        DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
        DisplayMetrics.DENSITY_XXXHIGH -> "xxxhdpi"
        else -> "custom"
    }

    private fun uiModeName(configuration: Configuration): String =
        when (configuration.uiMode and Configuration.UI_MODE_TYPE_MASK) {
            Configuration.UI_MODE_TYPE_TELEVISION -> "television"
            Configuration.UI_MODE_TYPE_CAR -> "car"
            Configuration.UI_MODE_TYPE_DESK -> "desk"
            Configuration.UI_MODE_TYPE_WATCH -> "watch"
            Configuration.UI_MODE_TYPE_VR_HEADSET -> "VR headset"
            Configuration.UI_MODE_TYPE_APPLIANCE -> "appliance"
            Configuration.UI_MODE_TYPE_NORMAL -> "normal"
            else -> "undefined"
        }
}
