# Unique Urban Mythical Beast Graffiti Panel Artworks - Implementation Plan

Generate distinct, tailored graffiti artworks featuring urban mythical beasts (cyber dragons and panthers) customized for each routine time slot and priority card, and apply them directly to panel backdrops while preserving 100% text readability.

---

## User Selections & Direction
- **Visual Theme**: Urban mythical beasts (cyber dragons, prowling panthers, glowing eyes, aerosol neon spray, dark street concrete textures).
- **Variation Strategy**: **Unique art tailored to each routine time slot** and priority card.
- **Backdrop Legibility**: Kept subtle and dark via existing deep obsidian scrims so text, checkboxes, and buttons remain crisp and unobstructed.

---

## Proposed Artwork Breakdown

| Panel / Component | Beast Subject & Atmosphere | Color Palette |
| :--- | :--- | :--- |
| **Morning Routine** | Rising Dawn Cyber Dragon emerging through sunrise aerosol mist on a gritty concrete alley | Gold, Sunrise Amber, Fiery Orange & Charcoal |
| **Afternoon Routine** | Prowling Cyber Panther leaping across brick mural with electric neon claw tags | Electric Cyan, Hot Magenta & Concrete Grey |
| **Evening Routine** | Coiled Shadow Cyber Dragon breathing violet neon mist on dark brickwork | Neon Violet, Deep Purple, Electric Pink |
| **Night Routine** | Sleek Obsidian Cyber Panther resting with glowing cyan eyes under neon moonlight spray | Midnight Blue, Bioluminescent Cyan & Onyx |
| **Top 5 Priority Board** | Dual Mythical Beasts (Dragon & Panther standoff in high-stakes graffiti arena) | Electric Gold, Hot Pink, Cyan spray flares |
| **Daily Progress Card** | Unleashed Cyber Dragon roaring neon emerald green flames | Toxic Green, Lime, Emerald & Deep Obsidian |

---

## Technical Execution Steps

### 1. Asset Generation (`generate_image`)
Generate 5 dedicated, high-resolution 16:9 urban graffiti assets:
- `img_graffiti_morning_dragon`: Sunrise golden aerosol dragon mural.
- `img_graffiti_afternoon_panther`: Prowling electric cyan & magenta urban panther.
- `img_graffiti_evening_dragon`: Coiled shadow dragon with violet spray mist.
- `img_graffiti_night_panther`: Obsidian panther with glowing cyan eyes in midnight spray.
- `img_graffiti_top5_beasts`: Dragon & panther clash graffiti mural for the Top 5 priority board.
- `img_graffiti_progress_dragon`: Emerald flame dragon for the progress completion card.

### 2. Panel Background Assignment
- **`TodayScreen.kt`**:
  - Connect `img_graffiti_progress_dragon` to the Daily Completion card.
- **`Top5Card.kt`**:
  - Connect `img_graffiti_top5_beasts` to the My Top 5 Priority Board.
- **`RoutineSectionCard.kt`**:
  - Map `TimeSlot.MORNING` -> `img_graffiti_morning_dragon`
  - Map `TimeSlot.AFTERNOON` -> `img_graffiti_afternoon_panther`
  - Map `TimeSlot.EVENING` -> `img_graffiti_evening_dragon`
  - Map `TimeSlot.NIGHT` -> `img_graffiti_night_panther`

### 3. Verification & Testing
- Compile app via `compile_applet`.
- Execute Robolectric/JVM tests via `gradle :app:testDebugUnitTest`.
- Ensure all text, checkboxes, icons, and times remain clearly legible over each unique backdrop.
