# Smart Home – OOP1 final assignment

Console application that manages one smart home: rooms, the devices in those rooms (lights and
thermostats), their power usage and some statistics. Everything is kept in memory.

## Requirements
- Java 25
- Maven 3.9+

## Build, test and run
```
mvn verify                 # compile, run all unit tests, JaCoCo report in target/site/jacoco/index.html
mvn javadoc:javadoc        # Javadoc in target/reports/apidocs
mvn compile
java -cp target/classes nl.inholland.oop1.Application
```

## Structure
| Package                       | Contents                                                                |
|-------------------------------|-------------------------------------------------------------------------|
| `nl.inholland.oop1`           | `Application` – creates and connects all objects                        |
| `nl.inholland.oop1.model`     | `Describable`, `Device`, `LightDevice`, `ThermostatDevice`, `Room`, `SmartHome` (+ `HomeStats`), `Selector` |
| `nl.inholland.oop1.exception` | `DeviceNotFoundException`, `RoomNotFoundException`, `RoomNotEmptyException` (checked) |
| `nl.inholland.oop1.service`   | `SmartHomeService`, `SmartHomeServiceImpl`                              |
| `nl.inholland.oop1.ui`        | `ConsoleUserInterface`                                                  |

## Documentation
- [`INSTRUCTIONS.md`](INSTRUCTIONS.md) – the assignment
- [`docs/spec.md`](docs/spec.md) – specification written before the code
- [`docs/plan.md`](docs/plan.md) – the tasks the work was split into
- [`DECISIONS.md`](DECISIONS.md) – open points in the assignment, the decisions and the tests that prove them
- [`AI.md`](AI.md) – instructions for AI assistants
- [`docs/ai-log.md`](docs/ai-log.md) – how AI was used, and what was corrected
