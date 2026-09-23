# Text effects

Inline effects work through the shared font renderer, with optional Angelica batching integration. Both prefixes are accepted:

```text
&{burnished_auric;colors=#FFD700,#FFF2A0;speed=1.2}&oText&r
§{burnished_auric;colors=#FFD700,#FFF2A0;speed=1.2}§oText§r
```

`&r`/`§r` clears the effect and native formatting. Native colors such as `&c` replace the active effect palette while keeping the effect. Bold, italic and other native styles retain their ordinary meaning. Angelica's own `&q` and `&z` still require Angelica.

## Compact input

Sign lines in Minecraft 1.7.10 allow only 15 characters, including formatting declarations. Aliases reduce a declaration to six characters. Long identifiers remain supported.

| Alias | Renderer |
| --- | --- |
| `ir` | `infernum_red_rarity` |
| `gc` | `genesis_component_rarity_shader` |
| `pc` | `pulse_circle` |
| `nb` | `nameless_boss_bar_shader` |
| `pu` | `pulse_upwards` |
| `cr` | `calamity_red` |
| `er` | `exotic_rainbow` |
| `sb` | `superboss_rarity` |
| `is` | `infernum_spark_rarity` |
| `ba` | `burnished_auric` |
| `ec` | `evercold_cyan` |
| `ss` | `starsilver_rarity` |

```text
&{ba}GOLD
&{pu}&oHello
&{ba;#fc0;2}Hi
```

The last example uses `#FFCC00` at twice the normal speed and occupies 14 characters. Positional colors start with `#`; a positional number sets speed. Named options accept `c` for `colors` and `s` for `speed`, for example `&{ba;c=fc0;s=2}Hi`. Colors accept three or six hexadecimal digits, with up to eight comma-separated entries. Speed must be finite and nonnegative; zero freezes animation. Duplicate and unknown options are rejected.

Unknown inline renderer names remain ordinary input when drawn. They do not flush deferred text belonging to other signs. Available renderer names are resolved during layout creation, so register renderers and aliases before rendering starts.

## Code entry points

Use `TextEffects.format(style)` to open an inline effect and `TextEffects.apply(text, style)` for a closed, composable span. Existing `AnimatedTooltipHandler.renderedText` suppliers remain supported. Additional aliases can be registered during initialization with `TextEffectFormat.registerAlias(alias, namespacedRendererId)`.

All font-effect mixins are grouped under `mixins.early.texteffect` and `mixins.late.texteffect`, with optional Angelica hooks in `early.texteffect.angelica`. NEI keeps editable declarations intact and shares the vanilla input-field projection. The projection maps visible glyphs back to original source indices for scrolling, selection and cursor splits.

The font hooks cover the public draw and trim overloads as well as their vanilla delegates. This allows font replacements such as Qz-UILib to keep supplying glyphs without consuming effect declarations before GTNL parses them. No Qz-UILib dependency, reflection or class reference is introduced. UILib's separate scene/Markdown rendering API is outside these Minecraft font entry points.

The preview reads every registered effect when opened and displays its shortest registered alias before its name, for example `[ba] burnished_auric`. Custom registered renderers are included automatically; entries without aliases display their renderer names. Page count and the mixed-text example follow the collected entries.

Angelica defers sign text until its board geometry has rendered. Effect text follows that same deferred flush, using saved matrices, depth flags, Unicode mode and lightmap coordinates. It does not terminate model batching. Deferred storage and matrix buffers are pooled; layouts, draw runs, input slices and glyph masks are cached with existing bounds. No-Angelica rendering does not link Angelica classes: optional methods are erased and Angelica mixins are conditionally loaded.

## Verification

- `gradlew build -x test -x testClasses` passed, including formatting, compilation, checkstyle, packaging and JVM downgrading. No unit tests were added or run.
- Both client variants loaded the input/NEI mixins. The no-Angelica variant skipped the Angelica mixin.
- Both variants rendered adjacent signs containing `&{123}`, `PLAIN`, and `&{ba}GOLD`; all three texts remained visible. Captures are in `build/signcheck-angelica.png` and `build/signcheck-vanilla.png`.
- The user confirmed the NEI search field and chat input fixes. Their injection sites were also verified in transformed classes.
- Qz-UILib compatibility was traced against its local source. No Qz-UILib binary was available in the checkout for a runtime check.
- No numerical FPS benchmark or exhaustive check of third-party text renderers was performed.

## Public type and field audit

Ordinary public classes remain non-final. Existing public constructors noted below remain available. Unchanged fields are omitted except where a relocated mixin needs its complete shadow contract documented.

| Public type/signature | Added or changed fields/components |
| --- | --- |
| `class TextEffectFormat` | `String INLINE_OPEN`, `String AMP_INLINE_OPEN`, `int MAX_HEADER_LENGTH`; private `Map<String,String> ALIASES`, `int[] VANILLA_COLORS`. Static syntax, formatting, color, alias-registration and `String aliasFor(String)` methods. |
| `EffectTextParser.Cursor(String)`; `(String, UnaryOperator<String>, boolean)`; `(String, UnaryOperator<String>, boolean, Predicate<String>)` | `String source`, `UnaryOperator<String> preprocessor`, `boolean hexResetsStyles`, `Predicate<String> rendererAvailable`, `Deque<Scope> scopes`, `TextEffectStyle style`, `String formatting`, `int position`. |
| `record EffectTextParser.Token` | `String text`, `TextEffectStyle style`, `String formatting`, `int start`, `int end`, `boolean formattingCode`. |
| `record EffectTextLayout.Glyph` | Adds `int sourceStart`, `int sourceEnd` to `text`, `formatting`, `style`, `width`; old four-argument constructor retained. |
| `record EffectTextLayout.DrawRun` | `String text`, `TextEffectStyle style`, `float x`, `float y`, `float width`. |
| `record EffectTextLayout.Layout` | Adds `List<DrawRun> runs` to `glyphs`, `width`, `height`, `spacing`, `lines`; old five-argument constructor retained. |
| `record EffectTextRenderer.LayoutKey` | `FontRenderer font`, `String text`, `boolean unicode`, `int fontHeight`, `float scaleX`, `float scaleY`, `float spacing`, `float whitespace`, `String preprocessing`, `boolean hexResetsStyles`. |
| `class EffectTextFieldView(String, Layout)` | `String source`, `Layout layout`, `int[] sliceStarts`, `int[] sliceEnds`, `String[] slices`, `int nextSlice`. Public `matches`, `slice`, `width`, `trim`. |
| `class DeferredTextEffects` | Static `Deque<Draw> PENDING`, `Deque<Draw> POOL`, `boolean flushing`. Public static `isFlushing`, `enqueue`, `flush`, `clear`. |
| `static class DeferredTextEffects.Draw` | `FloatBuffer projection`, `FloatBuffer modelView`, `FontRenderer font`, `String text`, `float x/y`, `int color`, `boolean shadow/unicode/depthTest/depthMask`, `float lightX/lightY`. Private capture/replay methods; render-thread-owned storage. |
| `class EffectTextRenderer implements IResourceManagerReloadListener` | Adds private static `int nativeDepth` and public static `boolean isBypassingEffects()` to separate native fallback from coverage capture. |
| `class TextEffects` | Adds static `String format(TextEffectStyle)`; existing preset fields retain their signatures. |
| `class AngelicaTextAdapter` | Adds static `boolean shouldDeferEffects()` and erased implementation method; no added fields. |
| `class TextEffectPreview extends GuiScreen` | Adds private `boolean italic` and `List<Entry> entries`; removes the hardcoded `PRESETS` array. |
| `record TextEffectPreview.Entry` | `TextEffectStyle style`, `String label`. |
| `class TextEffectRegistry` | Changes the backing map to registration order and adds private `List<String> identifiers`; public static `List<String> identifiers()` returns its immutable snapshot. |
| `abstract class MixinGuiTextFieldTextEffects` | Shadow `FontRenderer field_146211_a`, `String text`, `int lineScrollOffset`, `int cursorPosition`; unique `EffectTextFieldView gtnl$effectView`. |
| `abstract class MixinNEIFormattedTextField extends GuiTextField` | Public `(FontRenderer,int,int,int,int)` constructor; no fields. |
| `abstract class MixinGuiNewChatTextEffects`; `abstract class MixinEnumChatFormattingTextEffects` | No fields. |
| Relocated `abstract class MixinFontRendererTextEffects` | No fields. |
| Relocated `abstract class MixinFontBatchTextEffects implements FontBatchBridge` | Shadows `FontRenderer underlying`, `int batchDepth`, static `BatchingFontRenderer arenaOwner`, `int fontShaderId`, `int AAMode`, `int fontAAModeLast`. Adds a deferred-flush callback; no queue state in the mixin. |
| `enum Mixins implements IMixins` | Separate `TEXT_EFFECTS_COMMON`, `TEXT_EFFECTS_CLIENT`, `TEXT_EFFECTS_ANGELICA`, `TEXT_EFFECTS_NEI` registrations; instance fields unchanged. |

`TextEffectStyle(String rendererId, List<Integer> colors, float speed)`, `AnimatedText` preset field signatures, and `TextMaskCache` field signatures remain unchanged.
