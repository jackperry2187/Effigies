package jackperry2187.effigies.mixin;

import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameTestServer.class)
public final class GameTestServerMixin {
    private GameTestServerMixin() {
    }

    @Redirect(
        method = "startTests",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/gametest/framework/GameTestRunner$Builder;build()Lnet/minecraft/gametest/framework/GameTestRunner;"
        )
    )
    private static GameTestRunner clearBetweenBatches(GameTestRunner.Builder builder) {
        return builder.clearBetweenBatches().build();
    }
}