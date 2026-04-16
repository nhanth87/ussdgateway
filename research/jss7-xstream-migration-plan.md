# JSS7 XStream Migration Plan

## Overview
Complete migration from Javolution XML to XStream for jss7 v9.1.0-SNAPSHOT.

## Migration Scope
- **499 files** using `javolution.xml` for serialization
- **Complete removal** of Javolution dependency
- **Version bump**: 9.0.0-318 → 9.1.0-SNAPSHOT

## XStream Migration Pattern

### 1. Annotations (replacing XMLFormat)
```java
// Before (Javolution)
public class MyClass implements XMLSerializable {
    protected static final XMLFormat<MyClass> XML = new XMLFormat<MyClass>(MyClass.class) {
        public void read(InputElement xml, MyClass obj) throws XMLStreamException {
            obj.field = xml.get("field");
        }
        public void write(MyClass obj, OutputElement xml) throws XMLStreamException {
            xml.setAttribute("field", obj.field);
        }
    };
}

// After (XStream)
@XStreamAlias("myClass")
public class MyClass {
    @XStreamAsAttribute
    private String field;
    @XStreamOmitField
    private transient Object cache;
}
```

### 2. Serialization/Deserialization
```java
// Before (Javolution)
XMLObjectWriter writer = XMLObjectWriter.newInstance(new FileOutputStream(file));
writer.write(obj, "root", MyClass.class);
writer.close();

XMLObjectReader reader = XMLObjectReader.newInstance(new FileInputStream(file));
MyClass obj = reader.read("root", MyClass.class);
reader.close();

// After (XStream)
XStream xstream = new XStream(new DomDriver());
xstream.processAnnotations(MyClass.class);

// Write
try (Writer writer = new FileWriter(file)) {
    xstream.toXML(obj, writer);
}

// Read
try (Reader reader = new FileReader(file)) {
    MyClass obj = (MyClass) xstream.fromXML(reader);
}
```

### 3. XML Binding (replacing XMLBinding)
```java
// Before (Javolution)
public class MyBinding extends XMLBinding {
    protected XMLFormat getFormat(Class forClass) throws XMLStreamException {
        if (MyClass.class.isAssignableFrom(forClass)) {
            return MyClass.XML;
        }
        return super.getFormat(forClass);
    }
}

// After (XStream)
public class MyXStreamHelper {
    private static final XStream xstream = new XStream(new DomDriver());
    static {
        xstream.processAnnotations(MyClass.class);
        xstream.processAnnotations(OtherClass.class);
    }
    public static XStream getXStream() { return xstream; }
}
```

## Module Breakdown

### CAP Module (~100 files)
- Location: `cap/cap-impl/src/main/java/...`
- Files: All `*Impl.java` with XML serialization
- Key classes: `CAPStackConfigurationManagement`, `*SpecificInfoImpl`

### MAP Module (~150 files)
- Location: `map/map-impl/src/main/java/...`
- Files: All `*Impl.java` with XML serialization
- Key classes: `MAPProviderImpl`, `MAPStackConfigurationManagement`

### M3UA Module (~50 files)
- Location: `m3ua/impl/src/main/java/...`
- Files: Parameter implementations, message implementations
- Key classes: `*ParameterImpl`, `M3UAMessageImpl`

### SCCP Module (~100 files)
- Location: `sccp/sccp-impl/src/main/java/...`
- Files: Router configurations, resource implementations
- Key classes: `SccpRouterXMLBinding`, `*Map.java`

### TCAP Module (~50 files)
- Location: `tcap/tcap-impl/src/main/java/...`
- Files: Dialog implementations, component implementations

### Other Modules (~50 files)
- ISUP, Statistics, OAM, etc.

## Migration Steps

1. **Create XStream Helper Classes**
   - Create `XStreamHelper` in each module
   - Configure XStream with annotations
   - Set up security permissions

2. **Migrate Model Classes**
   - Add XStream annotations
   - Remove Javolution XMLFormat
   - Keep backward compatibility if possible

3. **Migrate Management Classes**
   - Update load/save methods
   - Replace XMLObjectReader/Writer with XStream
   - Use try-with-resources

4. **Remove Javolution Dependency**
   - Remove from pom.xml
   - Clean up any remaining imports

## Testing Strategy

1. **XML Round-trip Tests**
   - Serialize old format → Deserialize with new
   - Serialize new format → Deserialize with new
   - Verify data integrity

2. **Backward Compatibility**
   - Old XML files should still be readable
   - Consider XStream aliases for compatibility

## Risks

1. **XML Format Changes**: XStream produces different XML structure
2. **Performance**: XStream may be slower than Javolution
3. **Security**: XStream requires security framework setup

## Rollback Plan

Keep a backup branch with Javolution until migration is fully tested.
