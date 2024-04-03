package app.revanced.patches.youtube.layout.animated

import app.revanced.patcher.data.ResourceContext
import app.revanced.patches.youtube.utils.integrations.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.settings.SettingsPatch
import app.revanced.util.ResourceGroup
import app.revanced.util.copyResources
import app.revanced.util.patch.BaseResourcePatch

@Suppress("unused")
object AnimatedButtonBackgroundPatch : BaseResourcePatch(
    name = "Hide animated button background",
    description = "Hides the background of the pause and play animated buttons in the Shorts player.",
    dependencies = setOf(SettingsPatch::class),
    compatiblePackages = COMPATIBLE_PACKAGE,
    use = false
) {
    override fun execute(context: ResourceContext) {
        /**
         * Copy json
         */
        context.copyResources(
            "youtube/animated",
            ResourceGroup(
                "raw",
                "pause_tap_feedback.json",
                "play_tap_feedback.json"
            )
        )

        SettingsPatch.updatePatchStatus("Hide animated button background")
    }
}