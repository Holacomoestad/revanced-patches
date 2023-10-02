package app.revanced.patches.music.utils.flyoutbutton.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.music.utils.flyoutbutton.fingerprints.FlyoutPanelLikeButtonFingerprint
import app.revanced.patches.music.utils.resourceid.patch.SharedResourceIdPatch
import app.revanced.patches.music.utils.resourceid.patch.SharedResourceIdPatch.Companion.MusicMenuLikeButtons
import app.revanced.util.bytecode.getWideLiteralIndex
import app.revanced.util.integrations.Constants.MUSIC_FLYOUT
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@DependsOn(
    [
        FlyoutButtonContainerResourcePatch::class,
        SharedResourceIdPatch::class
    ]
)
class FlyoutButtonContainerPatch : BytecodePatch(
    listOf(FlyoutPanelLikeButtonFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        FlyoutPanelLikeButtonFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetIndex = getWideLiteralIndex(MusicMenuLikeButtons)

                var insertIndex = -1

                for (index in targetIndex until targetIndex + 5) {
                    if (getInstruction(index).opcode != Opcode.MOVE_RESULT_OBJECT) continue

                    val register = getInstruction<OneRegisterInstruction>(index).registerA
                    insertIndex = index

                    addInstruction(
                        index + 1,
                        "invoke-static {v$register}, $MUSIC_FLYOUT->setFlyoutButtonContainer(Landroid/view/View;)V"
                    )
                    break
                }
                if (insertIndex == -1)
                    throw PatchException("Couldn't find target Index")
            }
        } ?: throw FlyoutPanelLikeButtonFingerprint.exception

    }
}