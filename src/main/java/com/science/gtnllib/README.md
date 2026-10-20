# GTNLLib

通用的 MUI2 界面和 HUD 代码，模组 id `gtnllib`，包名 `com.science.gtnllib`。现在还放在 GTNL 的源码里预拆分，等用法稳定后拆成独立模组，由它依赖 ModularUI2。

## 包含什么

| 包 | 内容 |
|---|---|
| `client/hud` | HUD 框架：元素声明、注册表、布局与保存（`config/GTNotLeisure/hud.json`）、统一绘制、MUI2 编辑器和设置面板 |
| `client/fx` | 颜色、角度、缓动 |
| `client/fx/screen` | 屏幕上的 2D 图形、毛玻璃、图形着色器 |
| `client/shader` | 着色器工具 `GlslProgram`、`ShaderEffect` |
| `gui/theme` | 配色主题：配色表、样式参数、通用构建、实时配色、按钮用途 |
| `gui/layout` | 布局帮助：尺寸方式、纯计算规则、主题间距 |
| `gui/widget` | 通用控件：刻度滑块、滑动开关、填充条、面板框和卡片绘制对象 |
| `gui/wheel` | 通用轮盘菜单 |
| `utils` | 单行文字绘制 |
| `config` | 客户端配置 `GTNLLibConfig`：HUD 总开关、HUD 总缩放、界面主题 |

## 规则

- 不引用 GTNL 和 GregTech 的任何类，也不写任何具体功能的名字；具体功能用到的颜色来源、编辑器配色、HUD 元素都由使用方在启动时注册进来。
- 资源只用 `gtnllib` 命名空间：着色器在 `assets/gtnllib/shaders`，语言文件在 `assets/gtnllib/lang`，语言键以 `gtnllib.` 开头，绘制对象和控件主题键也带 `gtnllib` 前缀。
- 契约测试在 `src/test/java/com/science/gtnllib`，其中 `BoundaryContracts` 守住上面两条。

## 初始化

使用方在客户端 preInit 里先调用 `GTNLLibClient.preInit()`（注册配置、HUD 绘制、着色器和主题重载），再注册自己的主题和 HUD 元素。服务端不需要初始化。

## 第三方代码

`gui/wheel` 的轮盘移植自 AnvilLib（MIT 许可），许可声明见 `src/main/resources/licenses/ANVILLIB-MIT.txt`。
