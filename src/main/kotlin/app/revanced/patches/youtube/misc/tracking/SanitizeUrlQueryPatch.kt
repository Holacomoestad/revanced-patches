package app.revanced.patches.youtube.misc.tracking

import app.revanced.patcher.data.ResourceContext
import app.revanced.patches.youtube.utils.integrations.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.settings.SettingsPatch
import app.revanced.util.patch.BaseResourcePatch

@Suppress("unused")
object SanitizeUrlQueryPatch : BaseResourcePatch(
    name = "Sanitize sharing links",
    description = "Adds an option to remove tracking query parameters from URLs when sharing links.",
    dependencies = setOf(
        SanitizeUrlQueryBytecodePatch::class,
        SettingsPatch::class
    ),
    compatiblePackages = COMPATIBLE_PACKAGE
) {
    override fun execute(context: ResourceContext) {

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "SETTINGS: SANITIZE_SHARING_LINKS"
            )
        )

        SettingsPatch.updatePatchStatus("Sanitize sharing links")
    }
}
