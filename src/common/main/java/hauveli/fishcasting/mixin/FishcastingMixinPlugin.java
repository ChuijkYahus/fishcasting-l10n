package hauveli.fishcasting.mixin;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FishcastingMixinPlugin implements IMixinConfigPlugin {
    private static final Map<String, String> MIXIN_MAP = Map.of(
            "FixCreateHatArmorLayerMixin", "create",
            "FixCreateSeatOffsetsSeatEntityMixin", "create"
    );

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String requiredMod = MIXIN_MAP.get(mixinClassName);
        if (requiredMod == null)
            return true; // true iff the mixin doesn't depend on mods that aren't dependencies... I wonder if I can do this in a smarter cleaner way than this?

        // oh my god I hexxying forgot I could just use this thank you hexmod
        // IXplatAbstractions.INSTANCE.isModPresent();
        return IXplatAbstractions.INSTANCE.isModPresent(requiredMod);
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
