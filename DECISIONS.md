# Decisions

Open points found in the specification, the decision taken, why, and the test that proves it.
Test references are filled in when the test is written (`Class#method`).

| ID   | Question | Decision | Why | Test |
|------|----------|----------|-----|------|
| D-01 | Which exception for `setTargetTemperature` on a device that is not a thermostat? | `IllegalArgumentException` with message `Device with id '<id>' is not a thermostat.` | The id exists, so `DeviceNotFoundException` would be misleading; the caller passed an argument that is invalid for this operation. Consistent with the other validation errors. | `SmartHomeTest#setTargetTemperatureOnLightThrowsIllegalArgumentException` |
| D-02 | Id of the first device in an empty home? | `1` | "Highest id + 1" with no ids means highest = 0. Ids starting at 1 are more natural for console users. | `SmartHomeTest#firstDeviceInEmptyHomeGetsIdOne` |
| D-03 | Does `getCountByType()` contain types with 0 devices? | No, only types that occur. | `stats()` is "based on the current devices only"; the model has no registry of all types, and a new device type would otherwise require changing `HomeStats`. | `HomeStatsTest#countByTypeOnlyContainsTypesThatOccur`, `SmartHomeTest#statsOfEmptyHome` |
| D-04 | What happens when a room with an existing name is added? | `IllegalArgumentException` | Room names must be unique; no custom exception is in the UML and we may not add API without need. | `SmartHomeTest#addRoomRejectsDuplicateNameIgnoringCase` |
| D-05 | Are room names case-sensitive? | Comparison is case-insensitive and ignores surrounding whitespace; stored name keeps the user's spelling (trimmed). | "Kitchen" and "kitchen" being two rooms would confuse a console user. | `SmartHomeTest#addRoomRejectsDuplicateNameIgnoringCase`, `#removeRoomFindsNameIgnoringCaseAndWhitespace`, `#addDeviceToRoomUsesRoomNameIgnoringCase` |
| D-06 | Can ids be reused after the device with the highest id is removed? | Yes, the rule is applied literally ("highest *existing* id + 1"). | The specification defines the algorithm explicitly; storing a counter would contradict it. | `SmartHomeTest#idOfRemovedHighestDeviceIsReused` |
| D-07 | Can the same `Device` instance be added twice (same or other room)? | No → `IllegalArgumentException`. `null` device → `IllegalArgumentException`. | Adding it again would give the single object a new id and make it appear twice. | `SmartHomeTest#addSameDeviceTwiceThrows`, `#addNullDeviceThrows` |
| D-08 | `findDevicesWithPowerUsageLessThan(max)`: strict or inclusive? | Strict (`<`). Negative or `NaN` max simply gives an empty list. | The name says "less than". | `SmartHomeServiceImplTest#findDevicesWithPowerUsageLessThanUsesStrictLessThan`, `#findDevicesWithNegativeOrNaNMaxSelectsNothing` |
| D-09 | How does the UI read numbers? | Read full lines and parse with `Integer.parseInt`/`Double.parseDouble` inside try/catch. | Avoids the classic `nextInt()`/`nextLine()` problems and `InputMismatchException` crashes. | manual (UI not unit-tested) |
| D-10 | Thermostat range format details? | `"<min>-<max>"`, `.` as decimal separator, whitespace around numbers allowed (`" 16 - 24 "`). Comma decimals, missing part, extra `-`, text → `IllegalArgumentException`. | Spec gives `"16-24"` and `"16.5-23.5"`; negative values are impossible because of the 5–30 bound, so splitting on `-` is unambiguous. | `ThermostatDeviceTest#constructorParsesValidRange`, `#constructorRejectsInvalidRange` |
| D-11 | Is the target temperature range inclusive? | Yes, `min <= target <= max`. | A new thermostat's target is `min`, so `min` must be valid; symmetric for `max`. | `ThermostatDeviceTest#setTargetTemperatureAcceptsValueInRange`, `#setTargetTemperatureRejectsValueOutsideRangeAndKeepsOldTarget` |
| D-12 | Is a name of only spaces "empty"? | Yes → `IllegalArgumentException`. Name stored as given. | A blank name is unusable in the UI. | `DeviceTest#constructorRejectsNullOrBlankName`, `#setNameRejectsNullOrBlankAndKeepsOldName` |
| D-13 | Is `0` W power usage allowed? `NaN`/`Infinity`? | `0` allowed; `NaN` and infinite rejected. | Only negatives are forbidden by the spec; `NaN`/`Infinity` break sum and sort. | `DeviceTest#constructorAcceptsZeroPowerUsage`, `#constructorRejectsInvalidPowerUsage` |
| D-14 | Exact `describe()` format of a thermostat? | `\| id: 1 \|\| name: Hall \|\| type: Thermostat \|\| range: 16.0-24.0 \|\| target: 16.0 \|` | "Same style" as `Device.describe()`; default `double` text is locale independent. | `ThermostatDeviceTest#describeAddsRangeAndTarget` |
| D-15 | What does `describe()` show for a device without id? Is `setId` validated? | `id: null`; `setId` accepts any value (also `null`). | The spec explicitly says new devices have id `null`; `SmartHome` is the only place that assigns ids. | `DeviceTest#describeWithoutIdShowsNull`, `#setIdChangesIdAndAcceptsNull` |
| D-16 | Tie-break for equal power usage: case-sensitive? | Case-insensitive name order. | Users expect `"lamp"` and `"Lamp"` to sort together, not all capitals first. | `SmartHomeTest#sortedByPowerUsageHighestFirstAndTiesByNameIgnoringCase` |
| D-17 | `Room.addDevice(null)`? | `IllegalArgumentException` | Fail fast; a `null` in the list would break every stream. | `RoomTest#addDeviceRejectsNull` |
| D-18 | `findByType(null)`? Subclasses? | `NullPointerException` (same as `find`); matches via `isInstance`, so `findByType(Device.class)` returns all devices. | Consistent with `find`; `isInstance` is the polymorphic choice. | `SmartHomeTest#findByTypeThrowsNullPointerExceptionForNullType`, `#findByTypeWithDeviceClassReturnsAllDevices` |
| D-19 | Is `SmartHome.getRooms()` modifiable? | No, unmodifiable view. | Same reasoning as `Room.getDevices()`: rooms must be added through `addRoom` so the rules are enforced. | `SmartHomeTest#getRoomsCannotBeModified` |
| D-20 | Order of rooms and devices in listings? | Insertion order (`ArrayList`). | Predictable output for the user and tests. | `SmartHomeTest#addRoomKeepsInsertionOrder`, `#getAllDevicesReturnsDevicesInRoomOrder` |
| D-21 | Key order of `getCountByType()`? | Alphabetical (`TreeMap`), map unmodifiable. | Deterministic output in the UI and tests. | `HomeStatsTest#countByTypeKeysAreSortedAlphabetically`, `#countByTypeCannotBeModified` |
| D-22 | UML has `setTargetTemperature` but the menu has no option for it. | Option 4 asks for an optional target temperature after the thermostat is added (empty = keep minimum). | Uses the required API without changing the given menu. | manual |
| D-23 | `getDevicesSortedByPowerUsage()` has no menu option. | Option 6 shows its result sorted (highest first); the sort is done by `SmartHome`, not in the UI. | Uses the required API without changing the given menu. | manual |
| D-24 | Validation of the room name in `Room`? | `null`/blank → `IllegalArgumentException`; name is trimmed. | Same rule as device names. | `RoomTest#constructorRejectsNullOrBlankName`, `#constructorTrimsName` |
| D-25 | What happens when the input ends (Ctrl+Z/Ctrl+D, piped input), also in the middle of an action? | The UI stops cleanly with `Goodbye!` instead of throwing `NoSuchElementException` or looping forever. | "The UI must never crash". | manual (pipe only "3" and "Kitchen" into the app) |
| D-26 | `describe()` does not contain the power usage, but options 1, 2 and 6 are about devices and their usage. | The UI prints `describe()` followed by `power: 60.00 W`; numbers are formatted with `Locale.ROOT` (always a decimal point). `describe()` itself stays exactly as specified. | The user needs to see the usage to understand option 6; the business format must not change. | manual |

## Lambda vs anonymous class
**Where:** `SmartHomeServiceImpl#findDevicesWithPowerUsageLessThan` uses an anonymous class
`new Selector<Device>() { ... }`. Lambdas and method references are used everywhere else
(`SmartHome#find`, `#findByType`, `#sortedByPowerUsage`, `#totalPowerUsage`, `HomeStats`,
`Room#removeDevice`), and in the tests (`home.find(device -> device.getPowerUsage() > 10)`).

**When I choose a lambda:** the interface is functional (one abstract method), the behaviour is
short and has no state of its own. A lambda is shorter and keeps the focus on *what* is selected.
For `Selector` this is almost always the best choice, so a lambda would also work in the service.

**When I choose an anonymous class:**
- the interface has more than one abstract method, or it is an abstract class (a lambda is impossible);
- the object needs its own fields (state), e.g. a selector that counts how many devices it checked;
- I need `this` to refer to the selector object itself (in a lambda `this` is the enclosing object);
- the implementation is longer and I want to add helper methods or override `toString()`.

The anonymous class in the service is kept because the assignment requires one; it also shows that
both forms capture the effectively-final parameter `max` in the same way.

## Extra public methods
None so far.
