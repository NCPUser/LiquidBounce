/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2016 - 2022 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */

package net.ccbluex.liquidbounce.injection.mixins.minecraft.render;

import net.ccbluex.liquidbounce.features.module.modules.misc.ModuleNameProtect;
import net.ccbluex.liquidbounce.interfaces.IMixinGameRenderer;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextRenderer.class)
public abstract class MixinTextRenderer implements IMixinGameRenderer {
    @Shadow protected abstract float drawLayer(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int underlineColor, int light);

    @Redirect(method = "drawInternal(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawLayer(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)F"))
    private float injectNameProtectA(TextRenderer textRenderer, String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, boolean seeThrough, int underlineColor, int light) {
        return this.drawLayer(ModuleNameProtect.INSTANCE.replace(text), x, y, color, shadow, matrix, vertexConsumerProvider, seeThrough, underlineColor, light);
    }

    @Redirect(method = "getWidth(Ljava/lang/String;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextHandler;getWidth(Ljava/lang/String;)F"))
    private float injectNameProtectWidthA(TextHandler textHandler, String text) {
        return textHandler.getWidth(ModuleNameProtect.INSTANCE.replace(text));
    }

    @Redirect(method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/OrderedText;accept(Lnet/minecraft/text/CharacterVisitor;)Z"))
    private boolean injectNameProtectB(OrderedText orderedText, CharacterVisitor visitor) {
        if (ModuleNameProtect.INSTANCE.getEnabled()) {
            final OrderedText wrapped = new ModuleNameProtect.NameProtectOrderedText(orderedText);
            return wrapped.accept(visitor);
        }

        return orderedText.accept(visitor);
    }

    @Redirect(method = "getWidth(Lnet/minecraft/text/OrderedText;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextHandler;getWidth(Lnet/minecraft/text/OrderedText;)F"))
    private float injectNameProtectWidthB(TextHandler textHandler, OrderedText orderedText) {
        if (ModuleNameProtect.INSTANCE.getEnabled()) {
            final OrderedText wrapped = new ModuleNameProtect.NameProtectOrderedText(orderedText);
            return textHandler.getWidth(wrapped);
        }

        return textHandler.getWidth(orderedText);
    }

}