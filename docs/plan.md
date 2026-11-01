# Implementation plan

Each task ends with a review by me and one commit. Tests are written together with the code they test.

| # | Task | Files | Done when |
|---|------|-------|-----------|
| 1 | Specification & AI setup | `docs/spec.md`, `docs/plan.md`, `DECISIONS.md`, `AI.md`, `docs/ai-log.md` | Spec reviewed, open points listed |
| 2 | Device model + tests | `model/Describable`, `Device`, `LightDevice`, `ThermostatDevice`; `DeviceTest`, `LightDeviceTest`, `ThermostatDeviceTest` | `mvn test` green |
| 3 | Exceptions + Room + tests | `exception/*`, `model/Room`; `RoomTest`, exception tests | `mvn test` green |
| 4 | SmartHome, HomeStats, Selector + tests | `model/Selector`, `model/SmartHome`; `SmartHomeTest`, `HomeStatsTest` | `mvn test` green |
| 5 | Service layer + Mockito tests | `service/SmartHomeService`, `SmartHomeServiceImpl`; `SmartHomeServiceImplTest` | `mvn test` green, anonymous class documented |
| 6 | Console UI + Application | `ui/ConsoleUserInterface`, `Application` | Manual run of every menu option incl. wrong input |
| 7 | Final pass | Javadoc, `DECISIONS.md` test references, `docs/ai-log.md` | `mvn verify` and `mvn javadoc:javadoc` clean |
