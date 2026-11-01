# AI log

Tool: Claude Code (VS Code extension). Way of working: spec first (`docs/spec.md`), then the small
tasks from `docs/plan.md`. After every task I reviewed the result, and committed and pushed it myself.

| Date       | Task | What I asked | What the AI did | What went wrong / what I corrected |
|------------|------|--------------|-----------------|------------------------------------|
| 2026-11-01 | 1 | Read `INSTRUCTIONS.md`, the UML and the menu screenshot; split the work into tasks and stop after each task for review. | Wrote `docs/spec.md`, `docs/plan.md`, the AI instruction file and 24 open points in `DECISIONS.md` before any code. | I renamed the instruction file from `CLAUDE.md` to `AI.md` so it is tool-neutral. Downside: Claude Code only loads `CLAUDE.md` automatically, so I have to ask it to read `AI.md` in a new session. |
| 2026-11-01 | 2 | Device model (`Device`, `LightDevice`, `ThermostatDevice`) with tests. | Model + 66 tests. | Things caught during this step: `NaN` passes a plain `powerUsage < 0` check, so the check tests the *valid* range instead; `Double.parseDouble` accepts `"NaN"`, `"Infinity"` and `"16d"`, so the range is parsed with a regular expression; the constructor uses private validators instead of the overridable setters. |
| 2026-11-01 | 3 | Checked exceptions and `Room` with tests. | 3 exceptions, `Room`, 22 tests. | `Integer == int` is safe, but `Integer == Integer` fails above 127 (Integer cache). Added a test with id 1000 so this can never silently break. |
| 2026-11-01 | 4 | `SmartHome`, `HomeStats`, `Selector` with tests. | 57 tests. | Checked that a failed `addDeviceToRoom` (unknown room) does not change the device's id: the room is looked up *before* the id is assigned. |
| 2026-11-01 | 5 | Service interface + implementation with anonymous `Selector`, Mockito tests. | 22 Mockito tests; `ArgumentCaptor` used to test the anonymous selector itself. Wrote the lambda vs anonymous class section in `DECISIONS.md`. | Mockito printed a "dynamically loaded agent" warning on Java 25; fixed in task 7 in `pom.xml`. |
| 2026-11-01 | 6 | Console UI exactly like the screenshot, plus `Application`. | UI with try/catch/finally around every action; scripted manual run of all options with wrong input. | Found two new open points while testing (D-25 end of input, D-26 showing power usage). The AI's script that added them to `DECISIONS.md` broke a table row with real line breaks; noticed it and fixed it. |
| 2026-11-01 | 7 | Final pass. | Mockito agent configured in `pom.xml`; coverage checked (100% of all tested classes after one extra test for a missed branch); `mvn javadoc:javadoc` clean; README written. | JaCoCo showed one uncovered branch in `SmartHome#getById` (device whose id is cleared after adding); added a test instead of removing the guard. |

## What I learned about working with AI
_Fill in yourself (a few sentences): what worked well, where you had to check or correct the AI,
and what you would do differently next time._
