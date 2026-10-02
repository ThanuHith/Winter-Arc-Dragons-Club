# Implementation Plan: Duolingo-Style Marimba Chime & Compact Audio Button UI

Address the vertical text wrapping glitch shown in user screenshots and replace the alert audio with a pleasant, soft buoyant marimba bounce chime inspired by Duolingo.

---

### User Review Required
> [!IMPORTANT]
> - **Sound Style**: Soft buoyant marimba bounce chime with wooden percussive mallet attack, warm fundamental body, and a cheerful rising progression (reminiscent of Duolingo's delightful completion feedback).
> - **UI Redesign**: Eliminate awkward vertical text clipping (`Te / st` and `Pl / ay / in / g`) by replacing the inline text button with an ergonomic, compact circular audio button (`▶` / `■`) alongside single-line non-wrapping labels.

---

### Proposed Changes

#### 1. Audio Synthesis (`res/raw/dragon_chime.mp3`)
- Generate an authentic, studio-quality soft buoyant marimba chime:
  - Model wooden bar physical acoustics: warm fundamental frequencies (e.g. C5 ~523Hz, E5 ~659Hz, G5 ~784Hz, C6 ~1046Hz in quick buoyant arpeggio).
  - Soft mallet attack ramp (3ms) with natural woody resonance decay.
  - Convert to 44.1kHz MP3 using `ffmpeg` and place in `app/src/main/res/raw/dragon_chime.mp3`.
  - Ensure compatibility with both `MediaPlayer` in `SoundPreviewHelper` and Android's `NotificationChannel`.

#### 2. Sound Preview UI Redesign (`AddTaskDialog.kt`)
- Resolve the layout squeeze identified in the user screenshots:
  - Structure the preview item as a clean horizontal bar where the left container takes `Modifier.weight(1f)` with strictly bounded single-line text:
    - Title: `"Notification Chime"` (FontWeight: SemiBold, 13sp)
    - Subtitle: `"Soft marimba bounce preview"` (TextMuted, 11sp, single line with ellipsis)
  - Replace the text-based right widget with a **Compact Circular Audio Button**:
    - 36dp x 36dp circular surface with 48dp minimum interactive touch target.
    - Icon toggle: `Icons.Default.PlayArrow` when idle, `Icons.Default.Stop` when playing.
    - Subtle pulsing or bright emerald border glow while playing.
    - Prevents any vertical text wrapping or overlapping regardless of device font scaling.

#### 3. Verification & Testing
- Compile applet with `compile_applet`.
- Execute Robolectric/JVM unit tests with `gradle :app:testDebugUnitTest`.
- Validate that the custom MP3 plays smoothly and preview stops cleanly on dialog dismissal.
