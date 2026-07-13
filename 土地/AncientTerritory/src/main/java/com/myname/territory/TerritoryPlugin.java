package com.myname.territory;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TerritoryPlugin extends JavaPlugin implements CommandExecutor {

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        this.databaseManager = new DatabaseManager();
        this.databaseManager.connect();

        if (this.getCommand("land") != null) {
            this.getCommand("land").setExecutor(this);
        }

        // ⚖️ 啟動自動政務：定時自動扣稅與逾期法拍循環（每30分鐘檢查一次）
        startTaxScheduler();

        getLogger().info("§e[古代行政] 土地契约與四級行政編制系統已成功歸位！");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.disconnect();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        String landId = "chunk_" + player.getLocation().getChunk().getX() + "_" + player.getLocation().getChunk().getZ();
        LandData land = databaseManager.loadLand(landId);

        // 如果資料庫沒這塊地，初始化它
        if (land == null) {
            land = new LandData(landId, "野外無主地", "四級（區、町、村、亭）");
        }

        if (args.length > 0) {
            // 🗺️ 1. 自由佔領 / 申請圈地指令
            if (args[0].equalsIgnoreCase("claim")) {
                if (land.getOwnerUUID() != null) {
                    player.sendMessage("§c[行政官府] 茲事體大！此地已有地主登記，不可強佔！");
                    return true;
                }

                land.setOwnerUUID(player.getUniqueId());
                land.setBoughtOut(true); // 預設為買斷契约
                databaseManager.saveLand(land);

                player.sendMessage("§a§l[✓] 登記成功！你已成功佔領此區土地。");
                player.sendMessage("§7當前區域行政等級：" + land.getAdminLevel());
                return true;
            }

            // 📜 2. 檢視目前土地狀態
            if (args[0].equalsIgnoreCase("info")) {
                player.sendMessage("§e====== ⚖️ 土地契约詳情 ======");
                player.sendMessage("§7土地編號: " + land.getLandId());
                player.sendMessage("§7土地類型: §f" + land.getLandType());
                player.sendMessage("§7行政級別: §f" + land.getAdminLevel());
                player.sendMessage("§7地契狀態: " + (land.getOwnerUUID() == null ? "§a無主荒地" : "§c已有主"));
                player.sendMessage("§7經營模式: " + (land.isBoughtOut() ? "§6【地契買斷】(低稅金)" : "§b【官府租賃】(高租金)"));
                player.sendMessage("§7目前欠稅: §c" + land.getUnpaidPeriods() + " 期");
                player.sendMessage("§e========================");
                return true;
            }
        }

        player.sendMessage("§7土地行政指令: /land [claim (佔領土地) / info (檢視地契)]");
        return true;
    }

    /**
     * 自動地稅與法拍催收排程器
     */
    private void startTaxScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                getLogger().info("§e[官府通告] 開始進行全城土地稅金核對與逾期催收...");
                // 這裡在實際營運中會從資料庫撈取所有土地，並串接 Vault 經濟插件扣錢
                // 以下模擬欠稅催收與法拍邏輯：
                // 假設某塊地欠稅：
                // land.incrementUnpaidPeriods();
                // if (land.getUnpaidPeriods() >= 3) { 
                //     land.setOwnerUUID(null); // 強制沒收法拍
                //     databaseManager.saveLand(land);
                // }
            }
        }.runTaskTimer(this, 20 * 60 * 30, 20 * 60 * 30); // 每 30 分鐘執行一次
    }
}