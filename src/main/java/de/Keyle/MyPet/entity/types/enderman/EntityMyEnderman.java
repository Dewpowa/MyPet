/*
 * This file is part of MyPet
 *
 * Copyright (C) 2011-2013 Keyle
 * MyPet is licensed under the GNU Lesser General Public License.
 *
 * MyPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.entity.types.enderman;

import de.Keyle.MyPet.entity.EntitySize;
import de.Keyle.MyPet.entity.types.EntityMyPet;
import de.Keyle.MyPet.entity.types.MyPet;
import net.minecraft.server.v1_6_R3.*;


@EntitySize(width = 0.6F, height = 2.9F)
public class EntityMyEnderman extends EntityMyPet {
    public EntityMyEnderman(World world, MyPet myPet) {
        super(world, myPet);
    }

    public int getBlockData() {
        return ((MyEnderman) myPet).BlockData;
    }

    public int getBlockID() {
        return ((MyEnderman) myPet).BlockID;
    }

    @Override
    protected String getDeathSound() {
        return "mob.endermen.death";
    }

    @Override
    protected String getHurtSound() {
        return "mob.endermen.hit";
    }

    @Override
    protected String getLivingSound() {
        return isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
    }

    public boolean handlePlayerInteraction(EntityHuman entityhuman) {
        if (super.handlePlayerInteraction(entityhuman)) {
            return true;
        }

        ItemStack itemStack = entityhuman.inventory.getItemInHand();

        if (getOwner().equals(entityhuman) && itemStack != null && canUseItem()) {
            if (itemStack.id == Item.SHEARS.id && getOwner().getPlayer().isSneaking()) {
                if (getBlockID() != 0) {
                    EntityItem entityitem = this.a(new ItemStack(getBlockID(), 1, getBlockData()), 1.0F);
                    entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                    entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);

                    setBlock(0, 0);

                    return true;
                }
            } else if (getBlockID() <= 0 && itemStack.id > 0 && itemStack.id < 256 && getOwner().getPlayer().isSneaking()) {
                setBlock(itemStack.id, itemStack.getData());
                if (!entityhuman.abilities.canInstantlyBuild) {
                    --itemStack.count;
                }
                if (itemStack.count <= 0) {
                    entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                }
            }
        }
        return false;
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(16, new Byte((byte) 0));  // blockID
        this.datawatcher.a(17, new Byte((byte) 0));  // blockData
        this.datawatcher.a(18, new Byte((byte) 0));  // face(angry)
    }

    public boolean isScreaming() {
        return ((MyEnderman) myPet).isScreaming;
    }

    public void setScreaming(boolean screaming) {
        this.datawatcher.watch(18, (byte) (screaming ? 1 : 0));
        ((MyEnderman) myPet).isScreaming = screaming;
    }

    public void setBlock(int blockID, int blockData) {
        this.datawatcher.watch(16, (byte) (blockID & 0xFF));
        ((MyEnderman) myPet).BlockID = blockID;

        this.datawatcher.watch(17, (byte) (blockData & 0xFF));
        ((MyEnderman) myPet).BlockData = blockData;
    }

    public void setMyPet(MyPet myPet) {
        if (myPet != null) {
            super.setMyPet(myPet);

            this.setScreaming(((MyEnderman) myPet).isScreaming());
            this.setBlock(((MyEnderman) myPet).getBlockID(), ((MyEnderman) myPet).getBlockData());
        }
    }
}