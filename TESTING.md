# 测试说明

测试按实际职责命名。`*Test` 是 JUnit 5 用例，`*TestSupport` 是共享初始化或断言，
`*Scenario` 是真实 Forge 生命周期中的场景。架构规则、行为回归、打包检查和游戏内验收分别报告，
不再把所有测试称为“契约测试”，也不再用 `main` 方法和累计断言数模拟测试框架。

## JVM 回归和打包

```sh
./gradlew :test :artifactTest
./gradlew :architectureTest
./gradlew :machineTest
./gradlew :wirelessTest
./gradlew :equipmentTest
./gradlew :gtnllib:test
./gradlew :test --tests '*GlobalSteamPersistenceTest'
node --test tools/testing/*.test.cjs
```

Windows 使用 `gradlew.bat`。Java 25 运行宿主测试；基础库仍保持 Java 8 字节码和独立依赖边界。
`:test` 同时依赖 `:gtnllib:test`，`:check` 还包含 `:artifactTest`。
每个测试类单独启动 JVM，避免 Forge、MUI2 的全局注册状态串扰。具体测试和动态场景有独立结果，
失败直接抛出断言，由 JUnit 汇总；报告位于各模块 `build/test-results/` 和 `build/reports/tests/`。

当前 JVM 用例覆盖架构依赖、公共 API、双端类加载、配置迁移、存档格式、装配任务、流体扣除、
无线授权与缓存、装备和传送门数学、UI 布局与基础库注册等。`artifactTest` 使用单独源集，
检查实际发布包，不依赖游戏启动。测试夹具和原生测试模组均不得进入发布包。
公共 API 和布局基线只读，测试失败不会自动重写基线。

旧的 `verifyGTNLLib`、`verifyArchitecture`、`verifyMachines`、`verifyWireless`、
`verifyCelestweave`、`verifyGrandAssemblyLine`、`verifyModuleArtifacts` 仅保留为 Gradle 别名。

## 原生服务器场景

```sh
node tools/testing/run-runtime-tests.cjs --server
node tools/testing/run-runtime-tests.cjs --case steam.hatch.simulation-payment-reload
```

运行器只创建 `build/runtime-tests/<唯一运行编号>/game/GTNLTestWorld`，不会复用开发世界。
每次运行使用独立目录、场景计划、进程记录、日志和逐场景 JSON/JUnit XML；失败记录不覆盖。
`GTNL_TEST_OFFLINE=1` 可使用已有 Gradle 缓存，`GRADLE_USER_HOME` 可指定缓存位置。

原生测试模组位于 `src/runtimeTest`，只生成到 `build/test-mods`，由运行器安装到专用目录。
场景通过真实注册的机器物品放置方块，执行 tick，经过 Minecraft 的 TileEntity 注册表保存和重载。
机器目录来自 `GTNLMachineID`，同时展开线缆和管道的连续 ID。新增枚举自动加入场景计划，
遗漏、重复、未执行、超时和陈旧报告都会失败。未安装的可选联动与预留 ID 明确记为 `SKIP`。

蒸汽舱口场景验证模拟不扣款、正式扣款、水拒收、余额重载和不能重复扣款。
世界存档场景将大整数蒸汽余额写入真实 `MapStorage`，正常停止服务器，再启动一个新 JVM
读取同一测试世界。内存中写后读不能替代这项跨进程检查。
大型蒸汽炉的生产场景按 StructureLib 定义搭建，装入真实输入总线、输出总线和蒸汽舱口，
验证缺失外壳阻止运行、修复后消耗输入与蒸汽并产出 16 个石头，以及空闲期间不重复结算。
两次进程在 Forge 初始化时记录实际加载的宿主、基础库、测试模组路径和 SHA-256；重启前后
代码必须相同。运行记录也保留 Git 提交及工作区是否有未提交改动。

状态含 `NOT_RUN`、`RUNNING`、`PASS`、`FAIL`、`TIMEOUT`、`SKIP`。
只有全部计划内场景完成且进程正常结束，运行才通过；Minecraft 自行崩溃后 Gradle 返回 0 也不能通过。
注册、放置和 NBT 成功的覆盖范围是 `registry-placement-nbt`，不代表该机器已经通过生产验收。

## 客户端原生界面

```sh
node tools/testing/run-runtime-tests.cjs --client
node tools/testing/run-runtime-tests.cjs --client --case machine.large_steam_furnace.gui
```

CI 使用 Xvfb 和软件 OpenGL，固定 1280×720、GUI 缩放 2，并隐藏世界 HUD 避免欢迎消息遮挡。
测试模组从主菜单建立专用集成服务器世界，等待真实方块同步，
经方块激活路径打开服务端容器，核对客户端窗口编号，等待渲染，检查 MUI2 主面板边界，
保存实际帧截图，再确认服务端收到关闭。截图、哈希清单和 `gallery.html` 与报告一起归档。
截图必须能完整解码为实际窗口尺寸；游戏正常退出、无崩溃报告也是通过条件。
这一路径使用原生游戏 API，不使用桌面自动化。

现有主菜单贴图检查、五种服务器启动组合和兼容快照继续保留。
原生 GUI 场景使用未成型机器，当前范围是打开、同步、渲染和关闭；它不证明每个按钮、槽位、
成型机器工作界面、物理鼠标键盘输入或远程 TCP 联机行为。报告中的 `physicalInput`、`remoteTcp`
明确保留为 `NOT_RUN`，这些项目需要另行补充场景或人工验收。

## 增加测试

纯逻辑改动在对应包添加 JUnit 用例；跨全局状态用同类内的独立动态场景，保持名称说明行为。
游戏场景实现 `RuntimeScenario` 的开始、tick、超时和清理，并在 `RuntimeCatalog` 与 Node 计划中登记。
必须通过真实结构、输入、能源和输出验证的生产测试，不能用强制设置 `mMachine`、直接写输出库存
或单次调用配方函数替代。失败后仍需清理；不能把未知失败自动降级成跳过。

设计参考 [GT-Steam-Reborn 的 TestLab](https://github.com/MIAOKATZE/GT-Steam-Reborn/tree/ba04f99b938bf487f027b39c764a8c524649c98f/tools/testlab)
的场景生命周期、隔离世界、逐场景证据和 GUI 原生验证思路；实现针对 GTNL 独立编写，未复制其源代码。
