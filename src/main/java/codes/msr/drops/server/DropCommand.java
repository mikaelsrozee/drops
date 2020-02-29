package codes.msr.drops.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DropCommand extends CommandBase {

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("now");
        }

        return suggestions;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @Nonnull
    public String getName() {
        return "drop-force";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/drop-force <now>";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if (args.length == 0) {
            DropHandler.INSTANCE.force(false);
            sender.sendMessage(new TextComponentTranslation("text.drops.forced"));
        } else if (args.length == 1) {
            String option = args[0];

            if (option.equals("now")) {
                DropHandler.INSTANCE.force(true);
            } else {
                throw new WrongUsageException(this.getUsage(sender));
            }
        } else {
            throw new WrongUsageException(this.getUsage(sender));
        }
    }
}
