package app.revanced.patches.youtube.utils.fix.doublebacktoclose

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.utils.fix.doublebacktoclose.fingerprint.ScrollPositionFingerprint
import app.revanced.patches.youtube.utils.fix.doublebacktoclose.fingerprint.ScrollTopFingerprint
import app.revanced.patches.youtube.utils.fix.doublebacktoclose.fingerprint.ScrollTopParentFingerprint
import app.revanced.patches.youtube.utils.integrations.Constants.UTILS_PATH
import app.revanced.patches.youtube.utils.mainactivity.MainActivityResolvePatch
import app.revanced.util.exception
import app.revanced.util.getWalkerMethod

@Patch(dependencies = [MainActivityResolvePatch::class])
object DoubleBackToClosePatch : BytecodePatch(
    setOf(
        ScrollPositionFingerprint,
        ScrollTopParentFingerprint
    )
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "$UTILS_PATH/DoubleBackToClosePatch;"

    override fun execute(context: BytecodeContext) {

        /**
         * Hook onBackPressed method inside MainActivity (WatchWhileActivity)
         */
        MainActivityResolvePatch.injectOnBackPressedMethodCall(INTEGRATIONS_CLASS_DESCRIPTOR, "closeActivityOnBackPressed")


        /**
         * Inject the methods which start of ScrollView
         */
        ScrollPositionFingerprint.result?.let {
            val walkerMethod = it.getWalkerMethod(context, it.scanResult.patternScanResult!!.startIndex + 1)
            val insertIndex = walkerMethod.implementation!!.instructions.size - 1 - 1

            walkerMethod.injectScrollView(insertIndex, "onStartScrollView")
        } ?: throw ScrollPositionFingerprint.exception


        /**
         * Inject the methods which stop of ScrollView
         */
        ScrollTopParentFingerprint.result?.let { parentResult ->
            ScrollTopFingerprint.also { it.resolve(context, parentResult.classDef) }.result?.let {
                val insertIndex = it.scanResult.patternScanResult!!.endIndex

                it.mutableMethod.injectScrollView(insertIndex, "onStopScrollView")
            } ?: throw ScrollTopFingerprint.exception
        } ?: throw ScrollTopParentFingerprint.exception

    }

    private fun MutableMethod.injectScrollView(
        index: Int,
        descriptor: String
    ) {
        addInstruction(
            index,
            "invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->$descriptor()V"
        )
    }
}
