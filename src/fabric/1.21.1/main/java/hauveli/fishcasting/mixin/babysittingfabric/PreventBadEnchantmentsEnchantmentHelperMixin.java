package hauveli.fishcasting.mixin.babysittingfabric;


import hauveli.fishcasting.registry.FishcastingItems;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class PreventBadEnchantmentsEnchantmentHelperMixin {

    // todo: figure out what I can actually mixin to in order to block the enchantments on the fabric side..........
    /*
        if (!stack.is(FishcastingItems.SHEPHERDS_CASTING_ROD.getValue())) {
            return enchantments;
        }

        return enchantments.filter(enchantment ->
                !enchantment.is(Enchantments.MENDING)
                        && !enchantment.is(Enchantments.UNBREAKING)
        );
     */
}