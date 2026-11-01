# Smart Home – Specification

This specification was written **before** any production code. It restates the assignment
(`INSTRUCTIONS.md`, `uml/uml-diagram.png`, `console-user-interface.png`) in a precise, testable form
and fills the gaps. Every gap that required a choice is recorded in [`DECISIONS.md`](../DECISIONS.md)
with a reference (`D-xx`).

## 1. Scope
A console application that manages exactly **one** smart home in memory. The home starts empty.
All data is lost on exit.

## 2. Package structure
Base package `nl.inholland.oop1`.

| Package                        | Contents                                                                 | Reason                                  |
|--------------------------------|--------------------------------------------------------------------------|-----------------------------------------|
| `nl.inholland.oop1`            | `Application`                                                            | Entry point / composition root          |
| `nl.inholland.oop1.model`      | `Describable`, `Device`, `LightDevice`, `ThermostatDevice`, `Room`, `SmartHome` (+ `HomeStats`), `Selector` | Domain model and business rules         |
| `nl.inholland.oop1.exception`  | `DeviceNotFoundException`, `RoomNotFoundException`, `RoomNotEmptyException` | Checked domain exceptions               |
| `nl.inholland.oop1.service`    | `SmartHomeService`, `SmartHomeServiceImpl`                               | Application layer used by the UI        |
| `nl.inholland.oop1.ui`         | `ConsoleUserInterface`                                                   | Presentation (console I/O only)         |

Dependencies only point "downwards": `ui → service → model → exception`. The model never prints
anything; the UI never contains business rules.

## 3. Classes (public API exactly as in the UML)

### 3.1 `Describable` (interface)
- `String describe()`

### 3.2 `Device` (abstract, implements `Describable`)
Fields: `Integer id`, `String name`, `double powerUsage`.

| Member                         | Behaviour                                                                                     |
|--------------------------------|-----------------------------------------------------------------------------------------------|
| `Device(name, powerUsage)`     | Validates through the setters. `id` is `null`.                                                |
| `getId()` / `setId(Integer)`   | Plain accessor; `null` allowed (D-15).                                                        |
| `getName()` / `setName(String)`| `null`, empty or blank → `IllegalArgumentException` (D-12).                                   |
| `getPowerUsage()` / `setPowerUsage(double)` | Negative, `NaN` or infinite → `IllegalArgumentException`. `0` allowed (D-13).  |
| `abstract getType()`           | `"Light"` / `"Thermostat"`.                                                                   |
| `describe()`                   | `\| id: <id> \|\| name: <name> \|\| type: <type> \|`                                          |
| `toString()`                   | Returns `describe()` (so it is polymorphic as well).                                          |

### 3.3 `LightDevice extends Device`
- `LightDevice(name, powerUsage)`, `getType()` → `"Light"`.

### 3.4 `ThermostatDevice extends Device`
Fields: `minimumTemperature`, `maximumTemperature`, `targetTemperature` (all `double`).

- Constructor `ThermostatDevice(name, powerUsage, temperatureRange)`.
  - Range format `"<min>-<max>"`, decimal point `.`, optional whitespace around the numbers (D-10).
  - `null`, empty, wrong format, non-numeric, `min < 5`, `max > 30`, `min >= max`
    → `IllegalArgumentException`.
  - Target temperature starts at `min`.
- `setTargetTemperature(t)`: `t` must satisfy `min <= t <= max` (inclusive, D-11) else `IllegalArgumentException`.
- `getType()` → `"Thermostat"`.
- `describe()` →
  `| id: <id> || name: <name> || type: Thermostat || range: <min>-<max> || target: <target> |`
  using Java's default `double` text, e.g. `16.0-24.0` and `16.0` (D-14).

### 3.5 `Room`
- `Room(name)`: `null`/blank name → `IllegalArgumentException`.
- `getName()`.
- `getDevices()`: **unmodifiable view** (`Collections.unmodifiableList`) – callers cannot change it.
- `addDevice(device)`: `null` → `IllegalArgumentException` (D-17).
- `removeDevice(id)`: `true` if a device with that id was removed, else `false`.

### 3.6 `Selector<T>` (functional interface)
- `boolean select(T t)`. Annotated `@FunctionalInterface`.

### 3.7 `SmartHome`
Field: `List<Room> rooms` (keeps insertion order, D-20).

| Method                                   | Behaviour                                                                                         |
|------------------------------------------|---------------------------------------------------------------------------------------------------|
| `addRoom(Room)`                          | `null` → `IllegalArgumentException`; duplicate name (case-insensitive) → `IllegalArgumentException` (D-04, D-05). |
| `getRooms()`                             | Unmodifiable list (D-19).                                                                         |
| `removeRoom(name)`                       | Unknown → `RoomNotFoundException`; contains devices → `RoomNotEmptyException`.                    |
| `addDeviceToRoom(roomName, device)`      | Unknown room → `RoomNotFoundException`; `null` device or device already in the home → `IllegalArgumentException` (D-07). New id = max id across all rooms + 1, first id = `1` (D-02). Pre-set id ignored. Returns the id. |
| `removeDevice(id)`                       | `true` / `false`.                                                                                 |
| `getAllDevices()`                        | New list, room order then insertion order.                                                        |
| `find(Selector<Device>)`                 | New list with matches; `null` → `NullPointerException`.                                           |
| `<T extends Device> List<T> findByType(Class<T>)` | Generic; uses `isInstance`/`cast`; `null` → `NullPointerException` (D-18).               |
| `getById(id)`                            | Unknown → `DeviceNotFoundException`.                                                              |
| `setTargetTemperature(id, target)`       | Unknown → `DeviceNotFoundException`; not a thermostat → `IllegalArgumentException` (D-01); out of range → `IllegalArgumentException`. |
| `sortedByPowerUsage()`                   | Highest first, ties by name case-insensitive (D-16).                                              |
| `totalPowerUsage()`                      | Sum; `0.0` when empty.                                                                            |
| `stats()`                                | New `HomeStats(getAllDevices())`.                                                                 |

Room names are looked up case-insensitively (D-05).

### 3.8 `SmartHome.HomeStats` (static nested class)
- `HomeStats(Collection<Device>)`: `null` → `NullPointerException`. Snapshot computed in constructor.
- `getAveragePowerUsage()`: mean, `0.0` when empty.
- `getCountByType()`: unmodifiable map, **only types that occur** (D-03), key order sorted alphabetically (D-21).

### 3.9 Exceptions (all `extends Exception`, i.e. checked)
| Exception                  | Message                                           |
|----------------------------|---------------------------------------------------|
| `DeviceNotFoundException`  | `Device with id '<id>' not found.`                |
| `RoomNotFoundException`    | `Room '<name>' not found.`                        |
| `RoomNotEmptyException`    | `Room '<name>' still contains <n> device(s).`     |

### 3.10 `SmartHomeService` / `SmartHomeServiceImpl`
`SmartHomeServiceImpl(SmartHome)` (`null` → `NullPointerException`). Thin delegation layer:

| Service method                                   | Delegates to                                                       |
|--------------------------------------------------|--------------------------------------------------------------------|
| `fetchAllRoomsAndDevices()`                      | `getRooms()`                                                       |
| `searchDeviceById(id)`                           | `getById(id)`                                                      |
| `addLightDevice(room, light)`                    | `addDeviceToRoom`                                                  |
| `addThermostatDevice(room, thermostat)`          | `addDeviceToRoom`                                                  |
| `removeDevice(id)`                               | `removeDevice`                                                     |
| `setTargetTemperature(id, target)`               | `setTargetTemperature`                                             |
| `findDevicesWithPowerUsageLessThan(max)`         | `find(...)` with an **anonymous class** `Selector`, strict `<` (D-08) |
| `getDevicesSortedByPowerUsage()`                 | `sortedByPowerUsage()`                                             |
| `getTotalPowerUsage()`                           | `totalPowerUsage()`                                                |
| `getHomeStats()`                                 | `stats()`                                                          |
| `addRoom(name)`                                  | `addRoom(new Room(name))`                                          |
| `removeRoom(name)`                               | `removeRoom(name)`                                                 |

Checked exceptions are declared and propagated (not swallowed) so the UI can show their message.

### 3.11 `ConsoleUserInterface`
Menu exactly as in the screenshot:
```
Welcome to the Smart House Application!
What do you want to do?
1. View all rooms and their devices
2. Search for a device (by id)
3. Add a light device
4. Add a thermostat device
5. Remove a device (by id)
6. Find devices whose power usage is less than a value
7. Total power usage of all devices
8. Stats (avg power usage and count by type)
9. Add a room (by name)
10. Remove a room (by name)
0. Exit
Enter choice:
```
- Reads whole lines with one `Scanner`; parses numbers itself → bad input never crashes (D-09).
- Every action is wrapped in `try/catch`; domain exceptions and `IllegalArgumentException`
  print `Error: <message>` and return to the menu.
- Option 4 additionally asks for an optional target temperature (D-22).
- Option 6 prints the matches sorted by power usage (D-23).
- Welcome line printed once; menu is repeated after every action.

### 3.12 `Application`
`main` creates `SmartHome → SmartHomeServiceImpl → ConsoleUserInterface` and calls `start()`.

## 4. Quality requirements
- JUnit 6 tests for every class except `ConsoleUserInterface` and `Application`;
  `SmartHomeServiceImpl` tested with Mockito mocks of `SmartHome`.
- `mvn verify` green on a clean clone; JaCoCo report in `target/site/jacoco`.
- Javadoc on every public type and method; `mvn javadoc:javadoc` without errors.
- Lambdas/method references for `find`, `sortedByPowerUsage`, `totalPowerUsage`, `stats`;
  one anonymous `Selector` (see 3.10).
