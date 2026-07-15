# Sable Mod 兼容性文档

## 概述

本文档记录了 tpCast 传送 Mod 对 [Sable](https://github.com/ryanhcode/sable) Mod（子空间结构系统）的兼容实现。

Sable 是一个 Minecraft 结构生成 mod，它采用了**双坐标系架构**来处理结构：

- **真实坐标**：结构方块实际放置的位置（通常在极远处，如 XZ ≈ 20,000,000）
- **视觉坐标**：玩家看到的结构所在位置（通常在玩家附近，如 XZ ≈ 0~100）

当玩家进入 sable 结构时，实体被传送到真实坐标处，但通过 sable 的渲染系统在视觉坐标处显示结构的副本。这种架构在传送时会造成坐标不匹配问题。

---

## 问题描述

tpCast 的 `/mark`、`/home`、`/back` 命令在 sable 结构中使用时会出现以下问题：

1. **保存位置不正确**：`getOnPos()` 返回的是玩家实体的真实坐标（极远处），而非玩家看到的视觉坐标
2. **传送到错误位置**：使用保存的坐标传送时，玩家被传送到真实方块位置（远处），而非结构视觉位置
3. **AABB 崩溃**：在不正确处理 sable 状态的情况下传送，会导致碰撞箱(AABB)在两个坐标系间撕裂，引发崩溃

---

## 解决策略

### 总体方案

采用**运行时反射**方式检测 sable mod，不做编译期硬依赖。核心逻辑封装在 `SableCompat` 工具类中。

### 关键决策

| 决策 | 选择 | 原因 |
|------|------|------|
| 检测方式 | `Class.forName()` 运行时反射 | 无硬编译依赖，sable 存在时自动激活，不存在时优雅降级 |
| 反射缓存 | `static {}` 一次性初始化 | 避免每次调用都反射查找，提高性能 |
| 方法解析 | 独立解析每个方法 | 单个方法在不同 sable 版本中可能变更，不影响其他功能 |
| 坐标转换 | 位置查找（`getContaining`）而非玩家追踪（`getTrackingSubLevel`） | 动态结构下玩家可能没有 active tracking |
| 传送方式 | 7参数 `teleportTo(level, x, y, z, Set.of(), yaw, pitch)` | 走 sable 的 `projectOutOfSubLevel` wrapper，安全处理坐标转换 |

---

## SableCompat API 参考

`src/main/java/net/lanzr/tpCast/api/SableCompat.java`

### 初始化日志

启动时根据反射结果输出：
```
SableCompat: initialized with 5/5 method handles   ← 全部方法解析成功
SableCompat: initialized with 3/5 method handles   ← 部分方法解析成功
Sable mod not detected — SableCompat features will be disabled  ← sable 未安装
```

### 公共方法

| 方法 | 返回 | 说明 |
|------|------|------|
| `isSableLoaded()` | `boolean` | 检测 sable mod 是否加载成功 |
| `getVisualPosition(player, realPos)` | `BlockPos` | 通过玩家追踪的 sublevel 将真实坐标→视觉坐标 |
| `getVisualPositionFromSubLevel(level, realPos)` | `BlockPos` | 通过位置查找 sublevel 将真实坐标→视觉坐标（推荐） |
| `isPositionInSubLevel(level, pos)` | `boolean` | 检查某位置是否在 sublevel 内 |
| `beforeTeleport(player)` | `void` | 传送前准备（当前未使用，保留备用） |
| `clearTrackingSubLevel(player)` | `void` | 清除追踪 sublevel（当前未使用，保留备用） |

### 使用的反射句柄

| 句柄 | 目标 | 作用 |
|------|------|------|
| `HELPER_GET_TRACKING_SUBLEVEL` | `Sable.Helper.getTrackingSubLevel(Entity)` | 获取玩家追踪的 sublevel |
| `SUBLEVEL_LOGICAL_POSE` | `SubLevel.logicalPose()` | 获取 sublevel 的坐标变换矩阵 |
| `SUBLEVEL_IS_REMOVED` | `SubLevel.isRemoved()` | 检查 sublevel 是否已被移除 |
| `POSE_TRANSFORM_POSITION_INVERSE` | `Pose3dc.transformPositionInverse(Vector3d)` | 全局→局部（真实→视觉）坐标转换 |
| `SET_TRACKING_SUBLEVEL` | `sable$setTrackingSubLevel(SubLevel)` | 设置/清除玩家的追踪 sublevel |

### 反射查找策略

由于不同 sable 版本的内部类结构可能不同，`findContainingMethod()` 采用多级降级搜索：

1. 在 `Sable.Helper` 接口上精确查找 `getContaining(Level, BlockPos)`
2. 在 Helper 接口上按方法名+参数个数暴力搜索
3. 在运行时类的所有父类和接口上通过 `getDeclaredMethods()` 搜索

---

## 坐标转换流程

### 保存位置时

```
玩家在 sublevel 中
    ↓
player.getOnPos() → 真实坐标 (20M, y, 20M)
    ↓
SableCompat.getVisualPositionFromSubLevel(level, realPos)
    ↓
Sable.Helper.getContaining(level, realPos) → 找到 SubLevel
    ↓
subLevel.logicalPose().transformPositionInverse(realPos) → 视觉坐标 (28, y, -5)
    ↓
保存视觉坐标到 NBT
```

### 传送时

```
从 NBT 读取视觉坐标 (28, y, -5)
    ↓
player.teleportTo(level, 28, y, -5, Set.of(), yaw, pitch)  ← 7参数版本
    ↓
Sable 的 @WrapOperation 拦截
    ↓
projectOutOfSubLevel(level, (28, y, -5))
    ├── 如果(28,y,-5)在 sublevel 边界内 → 转换为真实坐标
    └── 如果不在 → 保持不动
    ↓
Sable 安全处理坐标转换和状态更新
    ↓
玩家传送到正确位置，无崩溃
```

---

## 修改的文件

| 文件 | 修改内容 |
|------|----------|
| `api/SableCompat.java` | **新增** — 反射式 sable 兼容工具类 |
| `api/tpCastTag.java` | `setCurrentPosition()` + `setMark()` 保存时转换视觉坐标 |
| `api/LZCommonForgeApi.java` | `PairParseTeleport()` 使用 7参数 teleportTo |
| `command/HOMECommand.java` | respawnPosition 视觉坐标转换 + 7参数 teleportTo |
| `command/BackCommand.java` | 7参数 teleportTo |

---

## 注意事项

### ⚠️ 不使用 `clearTrackingSubLevel`

之前的实现尝试在传送前清除 sable 的 tracking sublevel，但会导致 sable 的碰撞系统 AABB 缓存数据残留，引发崩溃。最终方案是不清除 tracking，改用 7参数 `teleportTo`，让 sable 自己的 `projectOutOfSubLevel` wrapper 处理坐标转换和状态管理。

`clearTrackingSubLevel()` 和 `beforeTeleport()` 方法保留在 SableCompat 中但未使用，供将来参考。

### ⚠️ getVisualPosition vs getVisualPositionFromSubLevel

- `getVisualPosition`：使用 `getTrackingSubLevel(player)` — 依赖于玩家有 active tracking，动态结构下可能失效
- `getVisualPositionFromSubLevel`：使用 `getContaining(level, pos)` — 直接检查位置是否在 sublevel 内，更加可靠

推荐始终使用 `getVisualPositionFromSubLevel`。

### ⚠️ 不同 sable 版本的兼容性

反射初始化是独立解析每个方法的，如果未来 sable 版本变更了某个方法的签名或类路径：
- 其他已解析的方法仍然可用
- 失败的方法在调用时会优雅降级（返回原始坐标或 false）
- 日志会输出 `SableCompat: initialized with X/5 method handles`

---

## 提交历史

```
7521ebb fix(sable): use 7-param teleportTo and remove tracking clear
0cb4905 fix(sable): use position-based sublevel lookup for coord conversion
a0b7b44 fix(sable): correct SubLevel class path
485f849 fix(sable): per-method reflection init to tolerate API changes
38e0fd8 fix(sable): clear tracking sublevel before teleport
325d381 fix(sable): convert respawn position in HOME command
f0357c3 feat(sable): add SableCompat and integrate with tpCastTag
```
