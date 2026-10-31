# GTNLLib

通用的 MUI2 界面、HUD、文字特效、注册器和网络包基类，模组 id `gtnllib`，包名 `com.gtnllib`。源码、资源和测试属于独立 Gradle 子模块 `gtnllib`；GTNL 通过项目依赖使用它。分发时需要同时安装 GTNL 与 GTNLLib 两个 jar。

## 包含什么

| 包 | 内容 |
|---|---|
| `client/hud` | HUD 框架：元素声明、注册表、布局与保存（`config/GTNotLeisure/hud.json`）、统一绘制、MUI2 编辑器和设置面板 |
| `client/fx` | 角度、缓动 |
| `client/fx/screen` | 屏幕上的 2D 图形、毛玻璃、图形着色器 |
| `client/fx/world` | 世界里的 3D 特效：电弧、冲击波、光点/烟/火花粒子（同时显示的数量上限读配置）、底层画法和内置贴图 `FxRenderHelper`、镜头换算和视野判断、特效可见性，统一注册入口 `FxEffects` |
| `client/shader` | 着色器工具 `GlslProgram`、`ShaderEffect` |
| `gui/theme` | 配色主题：配色表、样式参数、通用构建、实时配色、按钮用途、界面材质（`ThemeSkin` 部件表、金属材质 `MetalSkin`） |
| `gui/layout` | 布局帮助：尺寸方式、纯计算规则、行和网格、分区和卡片 |
| `gui/widget` | 通用控件：刻度滑块、滑动开关、填充条、分隔线、关闭按钮、虚拟物品格（可从物品列表拖入），面板框、卡片和九宫格材质绘制对象，主题间距 `LayoutTheme`，带变更回调的物品处理器 `ListeningItemHandler` |
| `gui/settings` | 设置表单：按配置字段的注解生成滑块和开关 `SettingControls`，方块坐标输入框 `BlockPosFields` |
| `gui/wheel` | 通用轮盘菜单 |
| `client/text` | 文字特效渲染器 `EffectTextRenderer`、遮罩缓存、文本框显示，`compat` 是 Angelica 对接，`effect` 是 24 种内置特效和着色器特效基类，`preview` 是 `/gtnl_texteffects` 预览指令 |
| `text` | 两端都用的特效格式：样式 `TextEffectStyle`、内置特效表 `TextEffects`、格式和别名 `TextEffectFormat`、解析器 `EffectTextParser`、特效配色 `TextEffectColors` |
| `registry` | 统一注册器 `Registrar`：物品和方块的名字、翻译键、贴图、分类页和注册写一条 |
| `network` | 网络包基类 `ClientboundPacket`、`ServerboundPacket`、`PayloadHandler`；频道和注册顺序留在使用方 |
| `world` | 方块变化监听 `WorldListener`、`IBlockStateListener` |
| `client/tooltip` | 物品提示框 `AnimatedTooltipHandler`：按物品追加普通、按住 Shift、按住 Ctrl 才显示的提示行，以及动态变色文字；使用方用 `addTooltipStackResolver` 让伪装类物品改用别的物品的提示 |
| `command` | 通用指令 `/getiteminfo`、`/suicide`；`command/selector` 是指令里 `@a`、`@e[...]` 这类实体选择器的解析 |
| `mixins` | GTNLLib 自己的 mixin（文字特效 6 个）、登记表 `GTNLLibMixins` 和 early / late 加载入口 |
| `mod` | 模组列表 `ModList`（给 mixin 指定目标模组、判断模组是否加载） |
| `utils` | 单行文字绘制、颜色计算 `ColorMath`、数值工具 `MathUtils`（0~1 限制、伪随机数、大数换算和缩写）、长整数饱和运算 `LongMath`、`GameUtils`（射线、权限、放回背包、UUID）、传送 `TeleporterUtils`、提示行拼接 `MetaTooltipUtils`、三维向量 `Vec3d`、结构文件编解码 `StructureFileCodec`（逗号分隔文本和 MBS1 二进制）、创造模式光标归属 `CreativeCursor` |
| `config` | 客户端配置 `GTNLLibConfig`：HUD 总开关、HUD 总缩放、界面主题、世界粒子数量上限、文字特效开关 |

包之间只能单向依赖，不能互相引用：

- `utils`、`registry`、`mod` 在最底层；`client/shader`、`config` 不依赖别的包。
- `text` 只依赖 `utils`；`client/tooltip` 只依赖 `text`；`client/text` 再加上 `text`、`mod` 和 `client/shader`。
- `client/fx` 只依赖 `utils`；`client/fx/world` 再加上 `client/fx`，`client/fx/screen` 再加上 `client/fx` 和 `client/shader`。
- `gui/widget` 只依赖 `utils`；`gui/theme` 依赖 `gui/widget`；`gui/layout` 依赖 `gui/theme` 和 `gui/widget`。所以 `gui/widget` 里的控件不能用 `gui/layout`，要按布局摆放的组合控件放 `gui/settings`。
- `gui/settings`、`client/hud`、`gui/wheel` 在最上层。
- 只有入口 `GTNLLibClient` 读配置。

## 规则

- 不引用 GTNL 和 GregTech 的任何类，也不写任何具体功能的名字；具体功能用到的颜色来源、编辑器配色、HUD 元素都由使用方在启动时注册进来。
- 资源只用 `gtnllib` 命名空间：着色器在 `assets/gtnllib/shaders`（文字特效的在 `shaders/text`），语言文件在 `assets/gtnllib/lang`，界面材质在 `assets/gtnllib/textures/gui/skin`，语言键以 `gtnllib.` 开头，绘制对象和控件主题键也带 `gtnllib` 前缀。例外：从 GTNL 搬来的 `/getiteminfo`、文字特效预览、文字特效开关和物品提示的"按住 Shift / Ctrl"两行保留原来的 `gtnl.*` 键名，整合包里其他语言的翻译按键名覆盖，改名会让它们变回英文。
- 契约测试在 `src/test/java/com/gtnllib`，其中 `BoundaryContracts` 守住上面两条，并检查包之间没有互相引用、只有入口读配置。

## 初始化

GTNLLib 是独立的 `@Mod`（`com.gtnllib.GTNLLib`），自己完成初始化，使用方只需在 `@Mod` 里写 `required-after:gtnllib`：

- 客户端 preInit：`GTNLLibClient.preInit()` 注册配置并接到主题、HUD 和粒子上限，挂上 HUD 绘制、着色器和主题重载、世界特效渲染器（电弧、冲击波、粒子）、文字特效和它的资源重载、`/gtnl_texteffects` 预览指令。使用方在自己的 preInit 里注册主题和 HUD 元素即可。
- init：创建并挂上方块变化监听的根对象 `WorldListener.INSTANCE`。
- 服务器启动：注册 `/getiteminfo`、`/suicide`。
- 配置界面：模组列表里 GTNLLib 自己的配置按钮（`GTNLLibGuiFactory`）。

版本号从 `mcmod.info` 读，不引用使用方的构建常量。

## mixin

GTNLLib 的 mixin 在 `com.gtnllib.mixins`，登记表是 `GTNLLibMixins`，配置文件是 `mixins.gtnllib.early*.json` 和 `mixins.gtnllib.late*.json`。

- late：`GTNLLibLateMixins` 带 `@LateMixin`，按注解在运行时被发现，不依赖 jar 里的 coremod；NEI 那条按可选模组登记，没装 NEI 时不加载。
- early：独立 jar 的 manifest 指向 `GTNLLibEarlyCoreMod`，它继承 `GTNLLibEarlyMixins` 并实现 `IFMLLoadingPlugin`，由自身登记 early mixin。
- refmap 由模块的注解处理器生成 `mixins.gtnllib.refmap.json`，只供 GTNLLib 的配置引用。
- `gtnllib_at.cfg` 只公开相机插值使用的 `Minecraft.timer`，模块编译和运行都使用自己的访问权限声明。

## 构建边界验证

`./gradlew :gtnllib:build` 独立编译、测试和打包本模块，产物在 `gtnllib/build/libs`。
根项目的 `build/libs` 也会收集两个模组的正式包、开发包和源码包，供现有发布流程使用；测试夹具不发布。
开发启动请指定完整任务路径：GTNL 用 `:runClient25` / `:runServer25`，仅 GTNLLib 用
`:gtnllib:runClient25` / `:gtnllib:runServer25`，避免 Gradle 同时选择两个项目的同名启动任务。
独立运行包含 GTNHLib、MUI2 和 MUI2 初始化所需的 Baubles Expanded；现代 Java 的启动依赖由构建插件提供。
正式安装也需要这三个前置模组，GTNLLib 会在加载前检查它们是否存在。
`gtnllib/build.gradle` 显式声明基础库和可选对接的编译依赖；GTNL、GregTech 及宿主的 Minecraft
访问权限修改不在编译路径中。原来的 `compileGTNLLibBoundary` 入口保留为模块编译任务的别名。

GTNL 的 `check` 会执行模块的检查。GTNLLib 的契约测试位于模块内，运行时也不包含 GTNL 或
GregTech；跨模块共用的断言和主题检查放在 `src/testFixtures`，通过 Gradle 测试夹具依赖使用。

契约测试入口按模块分别运行：`verifyGTNLLib`、`verifyArchitecture`、`verifyMachines`、
`verifyWireless`、`verifyCelestweave`（装备）和 `verifyGrandAssemblyLine`。`test` 会执行全部套件，
每个套件在独立进程中报告断言和失败，不依赖其他套件的初始化副作用。

`verifyModuleArtifacts` 检查实际 jar 的模组元数据、coremod、访问权限声明、refmap、代码与资源归属、
许可文件及多版本字节码。CI 在 Java 25 上启动完整 GTNL 服务器并核对存档兼容快照，
再分别在 Java 25、Java 8 上启动 GTNLLib 独立服务器，各次启动保留单独日志。

宿主另有 `-Pgtnl.smokeScenario=without-neenergistics` 和 `quest-without-api` 两个启动场景，
分别验证缺少 NotEnoughEnergistics、存在 BetterQuesting 但缺少 BQAPI 时的类加载边界。
场景只调整 `:runServer25` 的运行路径，编译依赖不变；运行目录、日志和快照互相隔离。
检查会在 Forge 内确认模组的实际存在状态，再核对同一份兼容快照。
CropsNH 的类型已进入宿主温室公共接口，属于必需依赖；当前 CropsNH 版本还直接依赖 Waila，
因此移除这两者不能当作正常的可选联动场景。

CI 强制重新编译两个模块，并用 `.github/scripts/check-deprecations.cjs` 核对
`src/test/resources/compat/deprecations.json`。基线按源码文件和告警种类计数，允许减少，
不允许用另一个文件减少的告警抵消新增告警；缓存编译结果不能冒充零告警。
`verifyArchitecture` 还会比较五个 NEI 页面在不同容量下的槽位和尺寸基线。

可替换的可变集合统一使用 fastutil；访问顺序缓存显式使用 `getAndMoveToLast` / `putAndMoveToLast`。
`src/test/resources/compat/collections-compatibility.txt` 逐项记录仍需 JDK 具体类型的公开接口、
上游调用、旧存档序列化和双端队列。架构检查会阻止未说明理由的新增分配。
弱引用、枚举专用、并发和不可变集合保留原有语义。

## 第三方代码

`gui/wheel` 的轮盘移植自 AnvilLib（MIT 许可），许可声明见 `src/main/resources/licenses/ANVILLIB-MIT.txt`。
