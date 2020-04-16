package com.github.kisaragieffective.mcfuncrunner

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class McFuncRunner : JavaPlugin(), CommandExecutor {
    override fun onEnable() {
        // Plugin startup logic
        getCommand("function").executor = this
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.toLowerCase() != "function") return false
        if (args.size != 1) {
            sender.sendMessage("${ChatColor.RED}arguments count must be exactly 1, got ${args.size}")
            return false
        }

        val world: World = when (sender) {
            is Player -> sender.world
            is BlockCommandSender -> sender.block.world
            else -> {
                sender.sendMessage("${ChatColor.RED}${sender.javaClass.canonicalName} is unknown type")
                return true
            }
        }
        val sep = args[0].split(':')
        val namespace = when(sep.size) {
            1 -> "minecraft"
            2 -> sep[0]
            else -> throw AssertionError("unreachable")
        }
        val path = when(sep.size) {
            1 -> sep[0]
            2 -> sep[1]
            else -> throw AssertionError("unreachable")
        }
        val fullQualifier = "$namespace:$path"
        val mcfunc = File(world.worldFolder, "data/functions/$namespace/$path.mcfunction")
        if (mcfunc.exists().not()) {
            val hint = if (sender.hasPermission("mcfuncrunner.exact_place")) {
                " (Hint: Looked ${mcfunc.canonicalPath})"
            } else {
                ""
            }
            sender.sendMessage("${ChatColor.RED}Unknown function: $fullQualifier$hint")
            return true
        }

        val all: Int
        val maxLimit = world.getGameRuleValue("maxCommandChainLength").toIntOrNull() ?: 65536
        val body = mcfunc.bufferedReader()
                .lineSequence()
                .filter { !it.startsWith("#") }
                .toList()
                .also { all = it.size }
                .take(maxLimit)
        body.forEach { line ->
            Bukkit.dispatchCommand(sender, line)
        }
        val warn = if (all - maxLimit > 0) {
            // There are ignored commands
            " ${ChatColor.YELLOW}(${all - maxLimit} commands were ignored)"
        } else {
            ""
        }
        sender.sendMessage("${body.size} function(s) executed$warn")
        return true
    }
}