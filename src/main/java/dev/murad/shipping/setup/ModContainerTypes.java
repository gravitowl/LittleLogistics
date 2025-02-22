package dev.murad.shipping.setup;

import dev.murad.shipping.entity.accessor.EnergyTugDataAccessor;
import dev.murad.shipping.entity.accessor.SteamTugDataAccessor;
import dev.murad.shipping.entity.accessor.TugRouteScreenDataAccessor;
import dev.murad.shipping.entity.container.EnergyTugContainer;
import dev.murad.shipping.entity.container.FishingBargeContainer;
import dev.murad.shipping.entity.container.SteamTugContainer;
import dev.murad.shipping.item.container.TugRouteContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntArray;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;

public class ModContainerTypes {

    private static IntArray makeIntArray(PacketBuffer buffer) {
        int size = (buffer.readableBytes() + 1) / 4;
        IntArray arr = new IntArray(size);
        for (int i = 0; i < size; i++) {
            arr.set(i, buffer.readInt());
        }
        return arr;
    }

    public static final RegistryObject<ContainerType<SteamTugContainer>> TUG_CONTAINER =
            Registration.CONTAINERS.register("tug_container",
                    () -> IForgeContainerType.create(
                            (windowId, inv, data) ->
                                    new SteamTugContainer(windowId, inv.player.level, new SteamTugDataAccessor(makeIntArray(data)), inv, inv.player)));

    public static final RegistryObject<ContainerType<EnergyTugContainer>> ENERGY_TUG_CONTAINER =
            Registration.CONTAINERS.register("energy_tug_container",
                    () -> IForgeContainerType.create(
                            (windowId, inv, data) ->
                                    new EnergyTugContainer(windowId, inv.player.level, new EnergyTugDataAccessor(makeIntArray(data)), inv, inv.player)));

    public static final RegistryObject<ContainerType<FishingBargeContainer>> FISHING_BARGE_CONTAINER =
            Registration.CONTAINERS.register("fishing_barge_container",
                    () -> IForgeContainerType.create(
                            (windowId, inv, data) ->
                                    new FishingBargeContainer(windowId, inv.player.level, data.readInt(), inv, inv.player)));

    public static final RegistryObject<ContainerType<TugRouteContainer>> TUG_ROUTE_CONTAINER =
            Registration.CONTAINERS.register("tug_route_container",
                    () -> IForgeContainerType.create(
                            (windowId, inv, data) ->
                                    new TugRouteContainer(windowId, inv.player.level, new TugRouteScreenDataAccessor(makeIntArray(data)), inv, inv.player)));




    public static void register () {}
}
