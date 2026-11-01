# AI.md – instructions for AI assistants

## Project
Smart Home console app (Inholland OOP1 final assignment). Java 25, Maven, JUnit 6 (Jupiter), Mockito 5.

## Sources of truth (in this order)
1. `INSTRUCTIONS.md` and `uml/uml-diagram.png` – never rename or remove anything from the UML.
2. `docs/spec.md` – the detailed specification written before the code.
3. `DECISIONS.md` – choices for open points. If you find a new gap, **stop and propose** a new
   `D-xx` entry instead of silently picking something.

## Rules
- Base package `nl.inholland.oop1`; sub-packages `model`, `exception`, `service`, `ui` (see spec §2).
- Custom exceptions are **checked** (`extends Exception`) with the exact messages from the spec.
- No `System.out` outside `ui` and `Application`.
- Every public class and method gets Javadoc (`@param`, `@return`, `@throws`).
- Use lambdas/method references where they improve clarity; keep the one anonymous `Selector`
  in `SmartHomeServiceImpl`.
- Returned collections are never `null`; internal lists are never exposed modifiably.
- Write/extend the tests in the same step as the code. `SmartHomeServiceImpl` is tested with a
  Mockito mock of `SmartHome`. No tests for `ConsoleUserInterface` and `Application`.
- Work in the small steps from `docs/plan.md`; one task = one reviewable commit.

## Commands
- `mvn test` – run tests
- `mvn verify` – tests + JaCoCo report (`target/site/jacoco/index.html`)
- `mvn javadoc:javadoc` – must finish without errors
- Run the app: `mvn compile` then `java -cp target/classes nl.inholland.oop1.Application`
  (or run `Application` from the IDE).
