package fi.dy.masa.minihud.util;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import fi.dy.masa.malilib.util.Constants;
import fi.dy.masa.malilib.util.IntBoundingBox;

public class MiscUtils
{
    private static final Random RAND = new Random();

    public static long bytesToMb(long bytes)
    {
        return bytes / 1024L / 1024L;
    }

    public static double intAverage(int[] values)
    {
        final int size = values.length;
        long sum = 0L;

        for (int i = 0; i < size; ++i)
        {
            sum += values[i];
        }

        return (double) sum / (double) values.length;
    }

    public static boolean canSlimeSpawnAt(int posX, int posZ, long worldSeed)
    {
        return canSlimeSpawnInChunk(posX >> 4, posZ >> 4, worldSeed);
    }

    public static boolean canSlimeSpawnInChunk(int chunkX, int chunkZ, long worldSeed)
    {
        long slimeSeed = 987234911L;
        long rngSeed = worldSeed +
                       (long) (chunkX * chunkX *  4987142) + (long) (chunkX * 5947611) +
                       (long) (chunkZ * chunkZ) * 4392871L + (long) (chunkZ * 389711) ^ slimeSeed;

        RAND.setSeed(rngSeed);

        return RAND.nextInt(10) == 0;
    }

    public static boolean isOverworld(World world)
    {
        return world.getDimension().isNatural();
    }

    public static boolean isStructureWithinRange(@Nullable BlockBox bb, BlockPos playerPos, int maxRange)
    {
        if (bb == null ||
            playerPos.getX() < (bb.minX - maxRange) ||
            playerPos.getX() > (bb.maxX + maxRange) ||
            playerPos.getZ() < (bb.minZ - maxRange) ||
            playerPos.getZ() > (bb.maxZ + maxRange))
        {
            return false;
        }

        return true;
    }

    public static boolean isStructureWithinRange(@Nullable IntBoundingBox bb, BlockPos playerPos, int maxRange)
    {
        if (bb == null ||
            playerPos.getX() < (bb.minX - maxRange) ||
            playerPos.getX() > (bb.maxX + maxRange) ||
            playerPos.getZ() < (bb.minZ - maxRange) ||
            playerPos.getZ() > (bb.maxZ + maxRange))
        {
            return false;
        }

        return true;
    }

    public static boolean areBoxesEqual(IntBoundingBox bb1, IntBoundingBox bb2)
    {
        return bb1.minX == bb2.minX && bb1.minY == bb2.minY && bb1.minZ == bb2.minZ &&
               bb1.maxX == bb2.maxX && bb1.maxY == bb2.maxY && bb1.maxZ == bb2.maxZ;
    }

    public static void addBeeTooltip(ItemStack stack, List<Text> lines)
    {
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
        {
            tag = tag.getCompound("BlockEntityTag");
            ListTag bees = tag.getList("Bees", Constants.NBT.TAG_COMPOUND);
            int count = bees.size();
            int babyCount = 0;

            for (int i = 0; i < count; i++)
            {
                tag = bees.getCompound(i).getCompound("EntityData");

                if (tag != null)
                {
                    if (tag.contains("CustomName", Constants.NBT.TAG_STRING))
                    {
                        String beeName = tag.getString("CustomName");
                        lines.add(Math.min(1, lines.size()), new TranslatableText("minihud.label.bee_info.name", Text.Serializer.fromJson(beeName).getString()));
                    }

                    if (tag.contains("Age", Constants.NBT.TAG_INT) && tag.getInt("Age") < 0)
                    {
                        ++babyCount;
                    }
                }
            }

            TranslatableText text;

            if (babyCount > 0)
            {
                text = new TranslatableText("minihud.label.bee_info.count_babies", String.valueOf(count), String.valueOf(babyCount));
            }
            else
            {
                text = new TranslatableText("minihud.label.bee_info.count", String.valueOf(count));
            }

            lines.add(Math.min(1, lines.size()), text);
        }
    }

    public static void addHoneyTooltip(ItemStack stack, List<Text> lines)
    {
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains("BlockStateTag", Constants.NBT.TAG_COMPOUND))
        {
            tag = tag.getCompound("BlockStateTag");
            String honeyLevel = "0";

            if (tag != null && tag.contains("honey_level", Constants.NBT.TAG_STRING))
            {
                honeyLevel = tag.getString("honey_level");
            }
            else if (tag != null && tag.contains("honey_level", Constants.NBT.TAG_INT))
            {
                honeyLevel = String.valueOf(tag.getInt("honey_level"));
            }

            lines.add(Math.min(1, lines.size()), new TranslatableText("minihud.label.honey_info.level", honeyLevel));
        }
    }
}
