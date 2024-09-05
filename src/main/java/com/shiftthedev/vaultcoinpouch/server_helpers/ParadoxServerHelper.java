package com.shiftthedev.vaultcoinpouch.server_helpers;

import iskallia.vault.block.entity.GateLockTileEntity;
import iskallia.vault.core.event.common.GateLockOpenEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Modifiers;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.objective.ParadoxObjective;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.VaultGenerator;
import iskallia.vault.core.world.generator.layout.ClassicPresetLayout;
import iskallia.vault.core.world.generator.layout.GridLayout;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.item.crystal.layout.preset.ParadoxTemplatePreset;
import iskallia.vault.item.crystal.layout.preset.PoolTemplatePreset;
import iskallia.vault.item.crystal.layout.preset.StructurePreset;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.world.data.ParadoxCrystalData;
import iskallia.vault.world.data.PlayerReputationData;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.UUID;

;

public class ParadoxServerHelper
{
    public static void registerGateOpen_coinpouch(ParadoxObjective instance, GateLockOpenEvent.Data data, VirtualWorld world, Vault vault)
    {
        if (data.getWorld() == world && instance.get(ParadoxObjective.TYPE) != ParadoxObjective.Type.RUN)
        {
            if(ShopPedestalHelper.hasEnoughCurrency(InventoryUtil.findAllItems(data.getPlayer()), data.getEntity().getCost().get(0)))
            {
                if (PlayerReputationData.getReputation(data.getPlayer().getUUID(), data.getEntity().getGod()) >= data.getEntity().getReputationCost())
                {
                    ShopPedestalHelper.extractCurrency(data.getPlayer(), InventoryUtil.findAllItems(data.getPlayer()), data.getEntity().getCost().get(0));
                    Direction direction = data.getEntity().getDirection().getOpposite();
                    VaultGenerator generator = (VaultGenerator) ((WorldManager) vault.get(Vault.WORLD)).get(WorldManager.GENERATOR);
                    if (generator instanceof GridGenerator)
                    {
                        GridGenerator gridGenerator = (GridGenerator) generator;
                        RegionPos region = RegionPos.ofBlockPos(data.getPos(), (Integer) gridGenerator.get(GridGenerator.CELL_X), (Integer) gridGenerator.get(GridGenerator.CELL_Z));
                        GridLayout layout = (GridLayout) gridGenerator.get(GridGenerator.LAYOUT);
                        if (layout instanceof ClassicPresetLayout)
                        {
                            ClassicPresetLayout presetLayout = (ClassicPresetLayout) layout;
                            boolean changed = false;
                            if (!presetLayout.hasGenerated(region.add(direction, 1)))
                            {
                                presetLayout.append(vault, world, region.add(direction, 1), new PoolTemplatePreset(data.getEntity().getTunnel()));
                                changed = true;
                            }

                            if (!presetLayout.hasGenerated(region.add(direction, 2)))
                            {
                                presetLayout.append(vault, world, region.add(direction, 2), new ParadoxTemplatePreset(data.getEntity().getRoom(), data.getEntity().getGod()));
                                changed = true;
                            }

                            if (changed)
                            {
                                ParadoxCrystalData.Entry entry = ParadoxCrystalData.get(world).getOrCreate((UUID) instance.get(ParadoxObjective.PLAYER));
                                entry.preset = (StructurePreset) presetLayout.get(ClassicPresetLayout.PRESET);
                                entry.mergeModifiers(data.getEntity().getModifiers());
                                ChunkRandom random = ChunkRandom.any();
                                random.setBlockSeed(((UUID) instance.get(ParadoxObjective.PLAYER)).getLeastSignificantBits(), data.getPos(), (Long) instance.get(ParadoxObjective.SEED));
                                Iterator var13 = data.getEntity().getModifiers().iterator();

                                while (var13.hasNext())
                                {
                                    VaultModifierStack modifier = (VaultModifierStack) var13.next();
                                    ((Modifiers) vault.get(Vault.MODIFIERS)).addModifier(modifier.getModifier(), modifier.getSize(), true, random);
                                }

                                entry.changed = true;
                            }
                        }
                    }

                    data.getEntity().setStep(GateLockTileEntity.Step.REMOVED);
                    world.playSound((Player) null, (double) data.getPos().getX(), (double) data.getPos().getY(), (double) data.getPos().getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.9F, 1.2F);
                }
            }
        }
    }

    public static void registerGateOpen_vh(ParadoxObjective instance, GateLockOpenEvent.Data data, VirtualWorld world, Vault vault)
    {
        if (data.getWorld() == world && instance.get(ParadoxObjective.TYPE) != ParadoxObjective.Type.RUN)
        {
            if (InventoryUtil.consumeInputs(data.getEntity().getCost(), data.getPlayer().getInventory(), true))
            {
                if (PlayerReputationData.getReputation(data.getPlayer().getUUID(), data.getEntity().getGod()) >= data.getEntity().getReputationCost())
                {
                    InventoryUtil.consumeInputs(data.getEntity().getCost(), data.getPlayer().getInventory(), false);
                    Direction direction = data.getEntity().getDirection().getOpposite();
                    VaultGenerator generator = (VaultGenerator) ((WorldManager) vault.get(Vault.WORLD)).get(WorldManager.GENERATOR);
                    if (generator instanceof GridGenerator)
                    {
                        GridGenerator gridGenerator = (GridGenerator) generator;
                        RegionPos region = RegionPos.ofBlockPos(data.getPos(), (Integer) gridGenerator.get(GridGenerator.CELL_X), (Integer) gridGenerator.get(GridGenerator.CELL_Z));
                        GridLayout layout = (GridLayout) gridGenerator.get(GridGenerator.LAYOUT);
                        if (layout instanceof ClassicPresetLayout)
                        {
                            ClassicPresetLayout presetLayout = (ClassicPresetLayout) layout;
                            boolean changed = false;
                            if (!presetLayout.hasGenerated(region.add(direction, 1)))
                            {
                                presetLayout.append(vault, world, region.add(direction, 1), new PoolTemplatePreset(data.getEntity().getTunnel()));
                                changed = true;
                            }

                            if (!presetLayout.hasGenerated(region.add(direction, 2)))
                            {
                                presetLayout.append(vault, world, region.add(direction, 2), new ParadoxTemplatePreset(data.getEntity().getRoom(), data.getEntity().getGod()));
                                changed = true;
                            }

                            if (changed)
                            {
                                ParadoxCrystalData.Entry entry = ParadoxCrystalData.get(world).getOrCreate((UUID) instance.get(ParadoxObjective.PLAYER));
                                entry.preset = (StructurePreset) presetLayout.get(ClassicPresetLayout.PRESET);
                                entry.mergeModifiers(data.getEntity().getModifiers());
                                ChunkRandom random = ChunkRandom.any();
                                random.setBlockSeed(((UUID) instance.get(ParadoxObjective.PLAYER)).getLeastSignificantBits(), data.getPos(), (Long) instance.get(ParadoxObjective.SEED));
                                Iterator var13 = data.getEntity().getModifiers().iterator();

                                while (var13.hasNext())
                                {
                                    VaultModifierStack modifier = (VaultModifierStack) var13.next();
                                    ((Modifiers) vault.get(Vault.MODIFIERS)).addModifier(modifier.getModifier(), modifier.getSize(), true, random);
                                }

                                entry.changed = true;
                            }
                        }
                    }

                    data.getEntity().setStep(GateLockTileEntity.Step.REMOVED);
                    world.playSound((Player) null, (double) data.getPos().getX(), (double) data.getPos().getY(), (double) data.getPos().getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.9F, 1.2F);
                }
            }
        }
    }
}