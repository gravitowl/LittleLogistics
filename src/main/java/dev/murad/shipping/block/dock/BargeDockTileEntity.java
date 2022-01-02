package dev.murad.shipping.block.dock;

import dev.murad.shipping.entity.custom.barge.AbstractBargeEntity;
import dev.murad.shipping.entity.custom.barge.ChestBargeEntity;
import dev.murad.shipping.setup.ModTileEntitiesTypes;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

import java.util.Optional;

public class BargeDockTileEntity extends AbstractDockTileEntity {
    public BargeDockTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    public BargeDockTileEntity() {
        super(ModTileEntitiesTypes.BARGE_DOCK.get());
    }

    public Optional<HopperTileEntity> getExtractHopper(){
        TileEntity mayBeHopper = this.level.getBlockEntity(this.getBlockPos()
                .below()
                .relative(this.getBlockState().getValue(BargeDockBlock.FACING)));
        if (mayBeHopper instanceof HopperTileEntity) {
            return Optional.of((HopperTileEntity) mayBeHopper);
        }
        else return Optional.empty();
    }


    @Override
    public boolean holdVessel(Entity vessel, Direction direction) {
        if (!(vessel instanceof AbstractBargeEntity)
                || !getBlockState().getValue(BargeDockBlock.FACING).getOpposite().equals(direction)
                || !(vessel instanceof IInventory)) // TODO: Will need to change for liquid loading
        {
            return false;
        }


        return getBlockState().getValue(BargeDockBlock.EXTRACT_MODE) ?
                getExtractHopper().map(hopper -> mayMoveIntoInventory(hopper, (IInventory) vessel)).orElse(false) :
                getInsertHopper().map(hopper -> mayMoveIntoInventory((IInventory) vessel, hopper)).orElse(false);
    }
}