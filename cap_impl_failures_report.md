# cap-impl Test Failure Report

Module: jSS7/cap/cap-impl
Total failures: 43
Generated: 2026-04-17 09:58

## Summary

| Category | Count |
|----------|-------|
| Jackson XML migration | 29 |
| Pre-existing | 14 |

## org.restcomm.protocols.ss7.cap.functional.CAPFunctionalTest (4 failures)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testACNNotSuported | java.lang.AssertionError | Received event does not match, index[1] expected:<TestEvent [eventType=DialogUserAbort, sent=false, timestamp=1776394190210, eventSource=CAPDialog: LocalDialogId=1 RemoteDialogId=n... | Pre-existing functional/integration test failure |
| testAssistSsf | java.lang.AssertionError | Size of received events: 1, does not equal expected events: 13 | Pre-existing functional/integration test failure |
| testCircuitCall1 | java.lang.AssertionError | Size of received events: 22, does not equal expected events: 21 | Pre-existing functional/integration test failure |
| testSMS1 | java.lang.AssertionError | Size of received events: 8, does not equal expected events: 11 | Pre-existing functional/integration test failure |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.InitialDPRequestTest (3 failures)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testDecode | java.lang.AssertionError | expected:<true> but was:<false> | Pre-existing BER encode/decode mismatch |
| testEncode | java.lang.AssertionError | arrays differ firstly at element [26]; expected value is <18> but was <19>.  | Pre-existing BER encode/decode mismatch |
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.TimeDurationChargingResultTest (3 failures)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testDecode | org.restcomm.protocols.ss7.cap.api.CAPParsingComponentException | Error while decoding ReceivingSideID: choice receivingSideID has bad tag or tagClass or is not primitive | ASN.1 decode bug unrelated to XML |
| testEncode | java.lang.AssertionError | expected:<true> but was:<false> | Pre-existing BER encode/decode mismatch |
| testXMLSerializaion | java.lang.NullPointerException | Cannot invoke "org.restcomm.protocols.ss7.cap.api.primitives.AChChargingAddress.getSrfConnection()" because the return value of "org.restcomm.protocols.ss7.cap.service.circuitSwitc... | Deserialized object missing field / wrong getter mapping |

## org.restcomm.protocols.ss7.cap.service.gprs.primitive.SGSNCapabilitiesTest (2 failures)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testDecode | java.lang.AssertionError | expected:<false> but was:<true> | Pre-existing BER encode/decode mismatch |
| testEncode | java.lang.AssertionError | expected:<true> but was:<false> | Pre-existing BER encode/decode mismatch |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.IPSSPCapabilitiesTest (2 failures)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testDecode | java.lang.AssertionError | expected:<true> but was:<false> | Pre-existing BER encode/decode mismatch |
| testEncode | java.lang.AssertionError | expected:<true> but was:<false> | Pre-existing BER encode/decode mismatch |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.ApplyChargingReportRequestTest (2 failures)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testDecode | org.restcomm.protocols.ss7.cap.api.CAPParsingComponentException | Error while decoding ReceivingSideID: choice receivingSideID has bad tag or tagClass or is not primitive | ASN.1 decode bug unrelated to XML |
| testEncode | java.lang.AssertionError | expected:<true> but was:<false> | Pre-existing BER encode/decode mismatch |

## org.restcomm.protocols.ss7.cap.EsiBcsm.MidCallEventsTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerializaion | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.MessageIDTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | java.lang.AssertionError | expected:<false> but was:<true> | Round-trip XML serialization produced different values |

## org.restcomm.protocols.ss7.cap.gap.GapCriteriaTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.InitialDPArgExtensionTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "userServiceInformation": org.restcomm.protocols.ss7.cap.isup.BearerCapImpl#getUserServiceInformation() vs org.restcomm.protocols.ss7.ca... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.EsiBcsm.OMidCallSpecificInfoTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerializaion | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.EsiBcsm.TMidCallSpecificInfoTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerializaion | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.TimeInformationTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerializaion | java.lang.AssertionError | expected:<null> but was:<26> | Round-trip XML serialization produced different values |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.VariablePartTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.PromptAndCollectUserInformationResponseTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.RequestReportBCSMEventRequestTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | java.lang.AssertionError | expected:<true> but was:<false> | Round-trip XML serialization produced different values |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.SpecializedResourceReportRequestTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | java.lang.AssertionError | expected:<false> but was:<true> | Round-trip XML serialization produced different values |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.FurnishChargingInformationTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | java.lang.NullPointerException | Cannot invoke "org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.FCIBCCCAMELsequence1.getFreeFormatData()" because the return value of "org.restcomm.protocol... | Deserialized object missing field / wrong getter mapping |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.CallGapRequestTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.gap.CalledAddressAndServiceTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.gap.CallingAddressAndServiceTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.BearerCapabilityTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "userServiceInformation": org.restcomm.protocols.ss7.cap.isup.BearerCapImpl#getUserServiceInformation() vs org.restcomm.protocols.ss7.ca... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.ApplyChargingRequestTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerializaion | java.lang.NullPointerException | Cannot invoke "org.restcomm.protocols.ss7.cap.api.primitives.AChChargingAddress.getSrfConnection()" because the return value of "org.restcomm.protocols.ss7.cap.service.circuitSwitc... | Deserialized object missing field / wrong getter mapping |

## org.restcomm.protocols.ss7.cap.gap.BasicGapCriteriaTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.isup.BearerCapTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "userServiceInformation": org.restcomm.protocols.ss7.cap.isup.BearerCapImpl#getUserServiceInformation() vs org.restcomm.protocols.ss7.ca... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.gap.CompoundCriteriaTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.DisconnectLegRequestTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | java.lang.NullPointerException | Cannot invoke "org.restcomm.protocols.ss7.cap.api.isup.CauseCap.getCauseIndicators()" because the return value of "org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.Discon... | Deserialized object missing field / wrong getter mapping |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.EventSpecificInformationBCSMTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerializaion | java.lang.AssertionError | expected object to not be null | Round-trip XML serialization produced different values |

## org.restcomm.protocols.ss7.cap.service.sms.FurnishChargingInformationSMSRequestTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | java.lang.AssertionError | expected object to not be null | Round-trip XML serialization produced different values |

## org.restcomm.protocols.ss7.cap.isup.DigitsTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | com.fasterxml.jackson.databind.exc.InvalidDefinitionException | Conflicting getter definitions for property "genericDigits": org.restcomm.protocols.ss7.cap.isup.DigitsImpl#getGenericDigits() vs org.restcomm.protocols.ss7.cap.isup.DigitsImpl#get... | Conflicting getters in impl class (needs @JsonIgnore or @JsonProperty) |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.ConnectRequestTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | java.lang.AssertionError | expected:<true> but was:<false> | Round-trip XML serialization produced different values |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.ContinueWithArgumentArgExtensionTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerializaion | java.lang.AssertionError | expected:<false> but was:<true> | Round-trip XML serialization produced different values |

## org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.DestinationRoutingAddressTest (1 failure)

| Method | Failure Type | Error / First Stack Line | Diagnosis |
|--------|--------------|--------------------------|-----------|
| testXMLSerialize | java.lang.AssertionError | expected:<true> but was:<false> | Round-trip XML serialization produced different values |

---

## Notes

- Jackson XML migration failures are concentrated around InvalidDefinitionException (conflicting getters in DigitsImpl, BearerCapImpl) and AssertionError/NullPointerException from incomplete round-trip serialization.
- Pre-existing failures are mainly BER encode/decode mismatches in InitialDPRequestTest, ApplyChargingReportRequestTest, TimeDurationChargingResultTest, and functional test event-count mismatches in CAPFunctionalTest.

