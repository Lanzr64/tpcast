# Teleport Cast

Teleport Cast 是一个面向 Minecraft 1.20.1、基于 Minecraft Forge 的传送与位置管理模组。模组提供床/重生锚点返回、死亡点返回、个人标记点、玩家间传送和可分享的 Beacon，并通过“过载”系统限制连续使用传送功能。

## 版本要求

- Minecraft：1.20.1
- Minecraft Forge：47.3.0（Forge 47 系列）
- Java：17
- 当前模组版本：`1.20.1-forge-0.2.7`

模组功能由服务器提供；服务器需要安装本模组，客户端可以不安装。

## 命令

所有命令都只能由玩家在游戏内执行。大多数命令也可以加 `/tyj` 前缀使用，例如 `/tyj spawn`。需要管理员权限或默认关闭的命令会特别标注。

### 传送与位置

| 命令 | 说明 |
| --- | --- |
| `/home` | 返回当前玩家设置的床或重生锚点位置。没有重生位置时无法使用。 |
| `/spawn` | 传送到主世界的世界重生点。 |
| `/back` | 返回最近一次死亡位置；使用后清除该死亡记录。 |
| `/back-safe` | 在死亡点附近寻找安全站立位置后返回；成功后清除死亡记录。与 `/back` 共用同一条记录。 |
| `/mark` | 显示个人标记列表及当前标记操作的过载消耗。 |
| `/mark set <名称>` | 在当前位置创建或更新个人标记。名称含空格时请用引号括起。 |
| `/mark go <名称>` | 传送到指定个人标记，可跨维度。 |
| `/mark delete <名称>` | 删除指定个人标记。 |
| `/beacon` | 在当前位置放置或移动个人 Beacon，并向在线玩家广播可点击的传送链接。 |
| `/beacon go <玩家名>` | 传送到指定玩家的 Beacon；Beacon 数据持久保存，玩家可以离线。 |
| `/debeacon` | 移除自己的 Beacon。 |

### 玩家间传送

| 命令 | 说明 |
| --- | --- |
| `/tpa <玩家>` | 请求传送到指定玩家身边；对方使用 `/tpy` 接受后，请求者传送。 |
| `/tpahere <玩家>` | 请求指定玩家传送到你身边；对方使用 `/tpy` 接受后，该玩家传送到请求者身边。 |
| `/tpy` | 接受发给自己的 `/tpa` 或 `/tpahere` 请求。 |
| `/tpn` | 拒绝发给自己的 `/tpa` 或 `/tpahere` 请求。 |

### 管理与辅助

| 命令 | 说明 |
| --- | --- |
| `/self-check` | 查看当前过载状态；熔断时显示恢复倒计时。 |
| `/resetCoolDown <玩家>` | 管理员命令，需要 OP 权限等级 4。**当前实现会重置命令执行者的过载，参数玩家只用于提示消息。** |
| `/c-tp <区块X> <区块Z>` | 管理员命令，需要 OP 权限等级 4。按区块坐标传送，保留当前高度；输入坐标会乘以 16 转成方块坐标。 |
| `/cast-assist <玩家>` | 为指定玩家降低过载；施术者会增加过载。 |
| `/suicide` | 使自己死亡。 |
| `/overload-tp <X> <Y> <Z>` | 按方块坐标传送，并根据距离增加过载。该命令默认关闭；需将配置中的 `enable ops` 设为 `true`。 |

### 附加命令

以下命令由 `enable toy` 或 `enable body` 配置项控制：

| 命令 | 说明 |
| --- | --- |
| `/repair` | 修复主手和副手中可损坏的物品；成功修复时增加过载。 |
| `/trashcan` | 打开临时垃圾桶界面；关闭界面时其中物品会被清除。 |
| `/cast-off` | 丢弃玩家背包中的全部物品。 |
| `/tyjtyj` | 掉落一份熟鸡肉，并增加过载。 |
| `/hat` | 将头盔栏物品与手上的物品交换。 |

## 过载机制

传送等操作会累积过载，过载随时间恢复。操作导致过载达到上限时会触发熔断并追加惩罚过载；熔断期间，受冷却检查的命令不能使用。玩家数据会保存过载状态。

部分操作的消耗会随配置变化：个人标记的列表和传送消耗为“标记基础消耗 + 标记数量 × 每个标记的附加消耗”；`/overload-tp` 的消耗随传送距离增加。

## 配置

模组首次启动后会生成通用配置文件 `config/tpcast-common.toml`。下表为代码中的默认值：

### 过载与消耗

| 配置项 | 默认值 | 说明 |
| --- | ---: | --- |
| `max level` | `5` | 熔断前允许累积的最大过载等级。 |
| `cooldown per level` | `240` 秒 | 每一级过载的恢复时间。 |
| `punish ment` | `2` | 熔断时额外增加的过载等级。 |
| `back cost` | `0.5` | `/back` 和 `/back-safe` 消耗。 |
| `home cost` | `0.25` | `/home` 与 `/tyjtyj` 消耗。 |
| `tpa cost` | `1.5` | 玩家间传送消耗。 |
| `spawn cost` | `1.0` | `/spawn` 消耗。 |
| `mark base cost` | `0.8` | `/mark` 列表及标记传送的基础消耗。 |
| `mark add cost` | `0.2` | 每个已保存标记增加的消耗。 |
| `assist cost` | `2.0` | `/cast-assist` 施术者增加的过载。 |
| `assist add` | `1.4` | `/cast-assist` 为目标降低的过载。 |
| `repair cost` | `30` | `/repair` 成功修复时的消耗。 |
| `overload tp cost` | `0.5` | `/overload-tp` 的基础消耗。 |
| `distant tp index` | `1.0` | `/overload-tp` 距离附加消耗的系数。 |
| `beacon cost` | `0.8` | 放置或移动 Beacon 的消耗。 |
| `beacon go cost` | `0.5` | 传送到 Beacon 的消耗。 |

### 命令开关

下列命令组开关默认均为 `true`，唯独 `enable ops` 默认是 `false`。关闭某组后，该组命令不会注册。

| 配置项 | 控制的命令 |
| --- | --- |
| `enable home` | `/home` |
| `enable mark` | `/mark` |
| `enable tpa` | `/tpa`、`/tpahere`、`/tpy`、`/tpn` |
| `enable toy` | `/cast-off`、`/tyjtyj`、`/repair`、`/trashcan` |
| `enable spawn` | `/spawn` |
| `enable back` | `/back` |
| `enable back safe` | `/back-safe` |
| `enable assist` | `/cast-assist` |
| `enable common` | `/self-check`、`/resetCoolDown`、`/c-tp`、`/suicide` |
| `enable ops` | `/overload-tp`（默认关闭） |
| `enable beacon` | `/beacon`、`/debeacon` |
| `enable body` | `/hat` |

## 安装

将构建好的 `tpcast-1.20.1-forge-0.2.7.jar` 放入 Forge 1.20.1 服务器的 `mods` 目录，然后启动服务器。`/beacon` 数据保存在当前世界目录下的 `tpcast_beacons/` 文件夹。

## 从源码构建

需要 Java 17。Windows 可在项目目录运行：

```powershell
.\gradlew.bat build
```

其他平台可运行：

```bash
./gradlew build
```

构建产物位于 `build/libs/`。当前 `build.gradle` 还会运行 `exportJar`，将 JAR 复制到 `D:/lz_workplace/java/lzProject/output`；若该路径在你的环境中不可用，需要先调整构建脚本中的导出目录。

## 项目信息

- Mod ID：`tpcast`
- 作者：Lanzr
- Minecraft：1.20.1
- Forge：47.3.0
