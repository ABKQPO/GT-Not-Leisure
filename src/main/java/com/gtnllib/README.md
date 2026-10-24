# GTNLLib

通用的 MUI2 界面和 HUD 代码，模组 id `gtnllib`，包名 `com.gtnllib`。现在还放在 GTNL 的源码里预拆分，等用法稳定后拆成独立模组，由它依赖 ModularUI2。

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
| `gui/widget` | 通用控件：刻度滑块、滑动开关、填充条、分隔线、关闭按钮、虚拟物品格（可从物品列表拖入），面板框、卡片和九宫格材质绘制对象，主题间距 `LayoutTheme` |
| `gui/settings` | 设置表单：按配置字段的注解生成滑块和开关 `SettingControls`，方块坐标输入框 `BlockPosFields` |
| `gui/wheel` | 通用轮盘菜单 |
| `utils` | 单行文字绘制、颜色计算 `ColorMath`、0~1 限制和稳定的伪随机数 `MathUtils`、三维向量 `Vec3d` |
| `config` | 客户端配置 `GTNLLibConfig`：HUD 总开关、HUD 总缩放、界面主题、世界粒子数量上限 |

包之间只能单向依赖，不能互相引用：

- `utils` 在最底层；`client/shader`、`config` 不依赖别的包。
- `client/fx` 只依赖 `utils`；`client/fx/world` 再加上 `client/fx`，`client/fx/screen` 再加上 `client/fx` 和 `client/shader`。
- `gui/widget` 只依赖 `utils`；`gui/theme` 依赖 `gui/widget`；`gui/layout` 依赖 `gui/theme` 和 `gui/widget`。所以 `gui/widget` 里的控件不能用 `gui/layout`，要按布局摆放的组合控件放 `gui/settings`。
- `gui/settings`、`client/hud`、`gui/wheel` 在最上层。
- 只有入口 `GTNLLibClient` 读配置。

## 规则

- 不引用 GTNL 和 GregTech 的任何类，也不写任何具体功能的名字；具体功能用到的颜色来源、编辑器配色、HUD 元素都由使用方在启动时注册进来。
- 资源只用 `gtnllib` 命名空间：着色器在 `assets/gtnllib/shaders`，语言文件在 `assets/gtnllib/lang`，界面材质在 `assets/gtnllib/textures/gui/skin`，语言键以 `gtnllib.` 开头，绘制对象和控件主题键也带 `gtnllib` 前缀。
- 契约测试在 `src/test/java/com/gtnllib`，其中 `BoundaryContracts` 守住上面两条，并检查包之间没有互相引用、只有入口读配置。

## 初始化

使用方在客户端 preInit 里先调用 `GTNLLibClient.preInit()`（注册配置并接到主题、HUD 和粒子上限，再挂上 HUD 绘制、着色器和主题重载），再注册自己的主题和 HUD 元素。用到世界特效（电弧、冲击波、粒子）时还要调用 `FxEffects.register()`，它挂上这些渲染器，并在退出世界时清空、资源重载时刷新贴图。服务端不需要初始化。

## 第三方代码

`gui/wheel` 的轮盘移植自 AnvilLib（MIT 许可），许可声明见 `src/main/resources/licenses/ANVILLIB-MIT.txt`。
