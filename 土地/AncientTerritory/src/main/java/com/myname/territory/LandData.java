package com.myname.territory;

import java.util.UUID;

public class LandData {
    private final String landId;          // 土地唯一識別碼 (例如: chunk_x_z)
    private String landType;              // 王都安全區, 庇護區, 野外無主地
    private String adminLevel;            // 一級(闕都省道), 二級(府郡州縣), 三級(市鎮鄉旗), 四級(區町村亭)
    private UUID ownerUUID;               // 領主/租客的 UUID (null 代表無主地)
    private boolean isBoughtOut;          // true = 買斷, false = 租賃
    private int unpaidPeriods;            // 欠稅期數 (用來判斷是否法拍)

    public LandData(String landId, String landType, String adminLevel) {
        this.landId = landId;
        this.landType = landType;
        this.adminLevel = adminLevel;
        this.unpaidPeriods = 0;
    }

    // Getters and Setters
    public String getLandId() { return landId; }
    public String getLandType() { return landType; }
    public void setLandType(String landType) { this.landType = landType; }
    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
    public UUID getOwnerUUID() { return ownerUUID; }
    public void setOwnerUUID(UUID ownerUUID) { this.ownerUUID = ownerUUID; }
    public boolean isBoughtOut() { return isBoughtOut; }
    public void setBoughtOut(boolean boughtOut) { this.isBoughtOut = boughtOut; }
    public int getUnpaidPeriods() { return unpaidPeriods; }
    public void incrementUnpaidPeriods() { this.unpaidPeriods++; }
    public void resetUnpaidPeriods() { this.unpaidPeriods = 0; }
}