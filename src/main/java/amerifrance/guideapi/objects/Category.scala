package amerifrance.guideapi.objects

import java.util

import amerifrance.guideapi.gui.GuiBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector

class Category(entryList: util.List[Entry] = new util.ArrayList[Entry](), unlocCategoryName: String, itemstack: ItemStack) {

  var entries: util.List[Entry] = entryList
  var unlocalizedCategoryName: String = unlocCategoryName
  var stack = itemstack

  def addEntry(entry: Entry) = {
    this.entries.add(entry)
  }

  def addEntryList(list: util.List[Entry]) = {
    this.entries.addAll(list)
  }

  def removeEntry(entry: Entry) = {
    this.entries.remove(entry)
  }

  def removeCategoryList(list: util.List[Entry]) = {
    this.entries.remove(list)
  }

  def getLocalizedName(): String = {
    return StatCollector.translateToLocal(unlocalizedCategoryName)
  }

  def drawExtras(mouseX: Int, mouseY: Int, guiBase: GuiBase) = {
  }

  def canSee(player: EntityPlayer): Boolean = {
    return true
  }

  def onLeftClicked(mouseX: Int, mouseY: Int) = {
    System.out.println(getLocalizedName() + "Left Clicked")
  }

  def onRightClicked(mouseX: Int, mouseY: Int) = {
    System.out.println(getLocalizedName() + "Right Clicked")
  }
}