# JSS7 + JAIN-SLEE.JSS7 JCTools Migration Plan

## Executive Summary

Migrate jss7 (v9.0.0-318) and jain-slee.ss7 from Javolution 5.5.1 to JCTools 4.0.3 for better performance and modern concurrency support.

## Scope Analysis

### JSS7 Project
- **Current Version**: 9.0.0-318
- **Javolution Version**: 5.5.1
- **Files affected**:
  - 624 files with `javolution` imports
  - 69 files with `FastList`
  - 87 files with `FastMap`
  - 10 files with `FastSet`

### JAIN-SLEE.JSS7 Project
- **Files affected**: 3 wrapper files only
  - `CAPProviderWrapper.java`
  - `MAPProviderWrapper.java`
  - `TCAPProviderWrapper.java`

## Migration Mapping

| Javolution | JCTools/Standard Java | Use Case |
|------------|----------------------|----------|
| `FastList<T>` (queue/work list) | `MpscArrayQueue<T>` | Work queues, pending tasks |
| `FastList<T>` (listeners/storage) | `CopyOnWriteArrayList<T>` | Listeners, read-heavy lists |
| `FastMap<K,V>` | `NonBlockingHashMap<K,V>` | Caches, lookups |
| `FastSet<T>` | `ConcurrentHashMap.newKeySet()` | Unique collections |
| `TextBuilder` | `StringBuilder` | String building |

## Module-by-Module Breakdown

### 1. CAP Module (cap/cap-impl)
- **Files**: ~40 implementation files
- **Usage**: Primarily `FastList` for message components, `FastMap` for provider storage
- **Migration**: CopyOnWriteArrayList for listeners, NonBlockingHashMap for dialog storage

### 2. MAP Module (map/map-impl)
- **Files**: ~50 implementation files
- **Usage**: `FastList` for message components, `FastMap` for provider/dialog storage
- **Migration**: Similar to CAP

### 3. M3UA Module (m3ua/impl)
- **Files**: 30+ implementation files
- **Usage**: Heavy usage of `FastList`, `FastMap`, `FastSet`
- **Migration**: 
  - FSM states: CopyOnWriteArrayList
  - Route management: NonBlockingHashMap
  - ASP sets: ConcurrentHashMap.newKeySet()

### 4. ISUP Module (isup/isup-impl)
- **Files**: 10+ files
- **Usage**: `FastList` for listeners
- **Migration**: CopyOnWriteArrayList

### 5. OAM/Statistics (oam/common)
- **Files**: 10+ files
- **Usage**: `FastMap` for counters, statistics
- **Migration**: NonBlockingHashMap

### 6. Congestion Module
- **Files**: 2-3 files
- **Usage**: `FastList` for monitors
- **Migration**: CopyOnWriteArrayList

## Migration Steps

### Phase 1: POM Updates
1. Add `jctools.version` property (4.0.3)
2. Add JCTools dependency
3. Remove Javolution dependency (or keep for XML if needed)

### Phase 2: Core API Changes
Update interfaces that expose Javolution collections:
- `CAPProvider.getDialogs()` - change return type
- `MAPProvider.getDialogs()` - change return type
- Similar for other providers

### Phase 3: Implementation Migration
Module-by-module migration following the table above.

### Phase 4: JAIN-SLEE Wrapper Updates
Update 3 wrapper files to use new collection types.

### Phase 5: Testing
- Unit tests
- Integration tests
- Performance benchmarks

## Key Challenges

1. **API Compatibility**: Many interfaces expose Javolution types - need to change to standard Java interfaces
2. **Serialization**: Javolution XML serialization used in some places - migrate to XStream
3. **Thread Safety**: Ensure correct queue selection (MPSC vs MPMC vs SPSC)
4. **Testing**: Large codebase requires extensive testing

## Rollback Plan

Keep Javolution dependency in pom.xml (commented) during migration for easy rollback if needed.

## Estimated Effort

- POM updates: 1 hour
- CAP module: 4 hours
- MAP module: 4 hours
- M3UA module: 6 hours (most complex)
- ISUP module: 2 hours
- OAM/Statistics: 3 hours
- Testing: 4 hours
- **Total**: ~24 hours of work

## Migration Command Reference

```bash
# Find all javolution imports
git grep -l "import javolution" -- "*.java"

# Find specific types
git grep -l "FastList" -- "*.java"
git grep -l "FastMap" -- "*.java"
git grep -l "FastSet" -- "*.java"

# After migration, verify no Javolution remains
git grep "import javolution" -- "*.java" | wc -l
```
