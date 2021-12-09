package dev.murad.shipping.entity.custom;

import dev.murad.shipping.ShippingMod;
import dev.murad.shipping.setup.ModEntityTypes;
import dev.murad.shipping.setup.ModItems;
import dev.murad.shipping.util.Train;
import javafx.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ModBargeEntity extends BoatEntity implements ISpringableEntity{
    private Optional<Pair<ISpringableEntity, SpringEntity>> dominated = Optional.empty();
    private Optional<Pair<ISpringableEntity, SpringEntity>> dominant = Optional.empty();
    private Train train;

    public ModBargeEntity(EntityType<? extends BoatEntity> type, World world) {
        super(type, world);
        this.blocksBuilding = true;
        this.train = new Train(this);
    }

    public ModBargeEntity(World worldIn, double x, double y, double z) {
        this(ModEntityTypes.BARGE.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(Vector3d.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }


    @Override
    public Item getDropItem() {
        return ModItems.BARGE.get();
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(ShippingMod.MOD_ID, "barge")));
    }

    @Override
    protected void addPassenger(Entity passenger){

    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }


    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        if (player.isSecondaryUseActive()) {
            return ActionResultType.PASS;
        } else {
            if (!this.level.isClientSide) {
                doInteract(player);
            }
            return ActionResultType.PASS;
        }
    }

    protected void doInteract(PlayerEntity player) {
        player.sendMessage(new StringTextComponent("Hello World"), null);
    }

    @Override
    public Optional<Pair<ISpringableEntity, SpringEntity>> getDominated() {
        return this.dominated;
    }

    @Override
    public Optional<Pair<ISpringableEntity, SpringEntity>> getDominant() {
        return this.dominant;
    }

    @Override
    public void setDominated(ISpringableEntity entity, SpringEntity spring) {
        this.dominated = Optional.of(new Pair<>(entity, spring));
    }

    @Override
    public void setDominant(ISpringableEntity entity, SpringEntity spring) {
        this.setTrain(entity.getTrain());
        this.dominant = Optional.of(new Pair<>(entity, spring));
    }

    @Override
    public void removeDominated() {
        this.dominated = Optional.empty();
    }

    @Override
    public void removeDominant() {
        this.dominant = Optional.empty();
    }

    @Override
    public Train getTrain() {
        return train;
    }

    @Override
    public void setTrain(Train train) {
        this.train = train;
        train.setTail(this);
        dominated.ifPresent(dominated -> {
            // avoid recursion loops
            if(!dominated.getKey().getTrain().equals(train)){
                dominated.getKey().setTrain(train);
            }
        });
    }

    @Override
    public void remove(){
        handleKill();
        super.remove();
    }


}