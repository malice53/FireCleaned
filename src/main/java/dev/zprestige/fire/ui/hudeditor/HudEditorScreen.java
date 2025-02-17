package dev.zprestige.fire.ui.hudeditor;

import dev.zprestige.fire.Main;
import dev.zprestige.fire.module.client.clickgui.ClickGui;
import dev.zprestige.fire.util.impl.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;

public class HudEditorScreen extends GuiScreen {
    protected final ArrayList<HudComponentScreen> hudComponentScreens = new ArrayList<>();
    protected float x, y, width, height, dragX, dragY;
    protected boolean corrected, dragging;

    public HudEditorScreen() {
        width = 120;
        height = 15;
        Main.hudManager.getHudComponents().forEach(hudComponent -> hudComponentScreens.add(new HudComponentScreen(hudComponent,0, 0, 118, 15)));
    }

    protected void drag(int mouseX, int mouseY) {
        x = dragX + mouseX;
        y = dragY + mouseY;
        float deltaY = y;
        for (HudComponentScreen hudComponentScreen : hudComponentScreens) {
            hudComponentScreen.setY(deltaY += 16);
            hudComponentScreen.setX(x + 1);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            drag(mouseX, mouseY);
        }
        if (!corrected) {
            final ScaledResolution scaledResolution = new ScaledResolution(mc);
            x = (scaledResolution.getScaledWidth() / 2f) - 60;
            y = (scaledResolution.getScaledHeight() / 2f) - (hudComponentScreens.size() * 7.5f) - 7.5f;
            float deltaY = y;
            for (HudComponentScreen hudComponentScreen : hudComponentScreens) {
                hudComponentScreen.setY(deltaY += 16);
                hudComponentScreen.setX(x + 1);
            }
            corrected = true;
        }
        float deltaY = y;
        for (HudComponentScreen ignored : hudComponentScreens) {
            deltaY += 16;
        }
        deltaY += 16;
        RenderUtil.drawRect(x, y, x + width, deltaY, ((ClickGui) Main.moduleManager.getModuleByClass(ClickGui.class)).color.GetColor().getRGB());
        RenderUtil.drawRect(x, y, x + width, y + height, ((ClickGui) Main.moduleManager.getModuleByClass(ClickGui.class)).color.GetColor().getRGB());
        RenderUtil.drawOutline(x, y, x + width, deltaY, new Color(0, 0, 0, 50), 1.0f);
        RenderUtil.drawOutline(x, y, x + width, y + height, new Color(0, 0, 0, 50), 1.0f);
        final String text = "HudEditor";
        Main.fontManager.drawStringWithShadow(text,x + (width / 2f) - (Main.fontManager.getStringWidth(text) / 2f), y + (height / 2f) - (Main.fontManager.getFontHeight() / 2f), -1);
        hudComponentScreens.forEach(hudComponentScreen -> hudComponentScreen.draw(mouseX, mouseY));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (inside(mouseX, mouseY) && mouseButton == 0) {
            dragX = x - mouseX;
            dragY = y - mouseY;
            dragging = true;
        }
        hudComponentScreens.forEach(hudComponentScreen -> hudComponentScreen.click(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            dragging = false;
        }
        hudComponentScreens.forEach(hudComponentScreen -> hudComponentScreen.release(state));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    protected boolean inside(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
}
