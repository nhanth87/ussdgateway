# jSS7 Remaining Modules Test Report

**Date:** 2026-04-15  
**Command:** `mvn test` run per module  
**Context:** XML serialization tests migrated from Javolution to Jackson

---

## 1. map/map-impl

| Metric | Value |
|--------|-------|
| Total tests run | 824 |
| Failures | 22 |
| Errors | 0 |
| Skipped | 0 |

### First 10 failing tests
1. `CSGIdTest.testXMLSerialize`
2. `AnyTimeInterrogationResponseTest.testXMLSerialize`
3. `CellGlobalIdOrServiceAreaIdOrLAITest.testSerialization`
4. `EUtranCgiTest.testXMLSerialize`
5. `GPRSMSClassTest.testXMLSerialize`
6. `IMEITest.testXMLSerialize`
7. `LocationInformationEPSTest.testXMLSerialize`
8. `LocationInformationGPRSTest.testXMLSerialize`
9. `LocationNumberMapTest.testXMLSerialize`
10. *(3 more `testXMLSerialize` tests)*

### Assessment
**Jackson XML migration issues.** All failures are deserialization problems:
- `MismatchedInputException: Cannot construct instance of BitSetStrictLength`
- `JsonMappingException: For input string: "b21"` / `"b00"` (hex strings mis-parsed as integers)
- `NullPointerException` after deserialization (nested objects not restored)
- `AssertionError` on expected vs. actual values after round-trip

---

## 2. isup/isup-impl

| Metric | Value |
|--------|-------|
| Total tests run | 298 *(from prior surefire report)* |
| Failures | 11 |
| Errors | 0 |
| Skipped | 0 |

> **Note:** The current `mvn test` invocation hangs indefinitely after emitting XML output. The counts below are taken from the most recent complete surefire report in `target/surefire-reports`.

### First 10 failing tests
1. `CalledPartyNumberTest.testXMLSerialize`
2. `CallingPartyCategoryTest.testXMLSerialize`
3. `CauseIndicatorsTest.testXMLSerialize`
4. `CallingPartyNumberTest.testXMLSerialize` (second variant)
5. `GenericDigitsTest.testXMLSerialize`
6. `GenericNumberTest.testXMLSerialize`
7. `LocationNumberTest.testXMLSerialize`
8. `OriginalCalledNumberTest.testXMLSerialize`
9. `RedirectionInformationTest.testXMLSerialize`
10. `UserTeleserviceInformationTest.testXMLSerialize`

### Assessment
**Incomplete XML migration.** Every failure throws:
```
javolution.xml.stream.XMLStreamException:
No XMLFormat or TextFormat for instances of class ...Impl
```
The tests still use Javolution serialization, but the implementation classes appear to have had their `XMLFormat` support removed. The hang in the current run is likely a test-suite side effect of the incomplete migration.

---

## 3. inap/inap-impl

| Metric | Value |
|--------|-------|
| Total tests run | 15 |
| Failures | 2 |
| Errors | 0 |
| Skipped | 0 |

### First 10 failing tests
1. `HighLayerCompatibilityInapTest.testXMLSerialize`
2. `RedirectionInformationInapTest.testXMLSerialize`

### Assessment
**Jackson XML migration issues.** Both failures are:
```
InvalidDefinitionException: Cannot construct instance of ...
(no Creators, like default constructor, exist):
abstract types either need to be mapped to concrete types,
have custom deserializer, or contain additional type information
```
The mapper cannot deserialize abstract ISUP parameter interfaces because no concrete subtype bindings or type-info annotations were added.

---

## 4. sccp/sccp-impl

| Metric | Value |
|--------|-------|
| Total tests run | 132 |
| Failures | 7 |
| Errors | 0 |
| Skipped | 42 |

### First 10 failing tests
1. `AddressIndicatorTest.testSerialize`
2. `MessageReassemblyTest.testReassembly`
3. `XudtReassemblingTest.testA`
4. `RouteOnGtTest.testSend`
5. `SccpExecutorTest.setUp` *(configuration failure)*
6. `SccpExecutorTest.setUp` *(configuration failure)*
7. `SccpExecutorTest.setUp` *(configuration failure)*

### Assessment
**Jackson XML migration issues.** 
- `AddressIndicatorTest.testSerialize` is an explicit Jackson round-trip failure (`AssertionError: expected:<true> but was:<false>` after `XmlMapper` deserialization).
- The remaining 6 failures all cascade from the same root cause: `Mtp3Destination.getFirstSls()` returns `null`, indicating that the SCCP router's XML store/deserialization is broken. Test setups that load router XML fixtures therefore fail. This is consistent with router-store serialization being in flux during the Jackson migration.

---

## 5. sccp/sccp-impl-ext

| Metric | Value |
|--------|-------|
| Total tests run | 0 *(did not reach test phase)* |
| Failures | N/A |
| Errors | N/A |
| Skipped | N/A |

### Build result
**Compilation failure** during `testCompile`:
```
RuleTest.java:[40,39] cannot find symbol
  symbol:   class SCCPJacksonXMLHelper
  location: package org.restcomm.protocols.ss7.sccp
```
The same symbol is missing at lines 469 and 523.

### Assessment
**Jackson XML migration issue.** The extended SCCP tests reference `SCCPJacksonXMLHelper`, which was either renamed, moved, or not yet introduced in this branch. The tests cannot compile until the helper class reference is corrected.

---

## Summary

| Module | Tests Run | Failures | Errors | Nature of Failures |
|--------|-----------|----------|--------|--------------------|
| map/map-impl | 824 | 22 | 0 | Jackson XML migration |
| isup/isup-impl | 298 | 11 | 0 | Incomplete migration (Javolution tests vs. stripped impls) |
| inap/inap-impl | 15 | 2 | 0 | Jackson XML migration |
| sccp/sccp-impl | 132 | 7 | 0 | Jackson XML migration (router store + indicator serialization) |
| sccp/sccp-impl-ext | 0 | N/A | N/A | Compilation error — missing `SCCPJacksonXMLHelper` |

**Bottom line:**
- **MAP**, **INAP**, and **SCCP impl** failures are all attributable to the Jackson XML migration (deserialization errors, missing type mappings, broken router XML stores).
- **ISUP** is stuck in an incomplete state: tests still call Javolution, but the underlying implementations no longer provide `XMLFormat`, causing 11 serialization failures and a test-suite hang.
- **SCCP impl-ext** cannot even compile its tests because of a missing post-migration helper class reference.
