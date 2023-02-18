package app.revanced.patches.youtube.layout.general.personalinformation.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.annotation.YouTubeCompatibility
import app.revanced.patches.youtube.layout.general.personalinformation.fingerprints.AccountSwitcherAccessibilityLabelFingerprint
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.util.integrations.Constants.GENERAL_LAYOUT
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("hide-email-address")
@DependsOn([SettingsPatch::class])
@YouTubeCompatibility
@Version("0.0.1")
class HideEmailAddressPatch : BytecodePatch(
    listOf(
        AccountSwitcherAccessibilityLabelFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        AccountSwitcherAccessibilityLabelFingerprint.result?.let {
            with (it.mutableMethod) {
                val insertIndex = it.scanResult.patternScanResult!!.endIndex
                val register = (instruction(insertIndex - 2) as OneRegisterInstruction).registerA

                addInstructions(
                    insertIndex, """
                        invoke-static {v$register}, $GENERAL_LAYOUT->hideEmailAddress(I)I
                        move-result v$register
                    """
                )
            }
        } ?: return AccountSwitcherAccessibilityLabelFingerprint.toErrorResult()

        /*
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: LAYOUT_SETTINGS",
                "PREFERENCE_HEADER: GENERAL",
                "SETTINGS: HIDE_EMAIL_ADDRESS"
            )
        )

        SettingsPatch.updatePatchStatus("hide-email-address")

        return PatchResultSuccess()
    }
}
