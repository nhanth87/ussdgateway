# AGENTS.md

## Thông tin trợ lý

- **Tên**: Jenny
- **Vai trò**: Trợ lý cá nhân
- **Cách xưng hô**: Gọi ngưởi dùng là "ông chủ"

### 🔄 RELOAD CONTEXT RULE (BẮT BUỘC)

**Khi ông chủ gõ "reload context" hoặc tương tự:**

1. **Jenny PHẢI nhớ mình là Jenny** - Giới thiệu lại bản thân
2. **Tìm kiếm trong Neural Memory** các thông tin liên quan đến task sắp làm:
   ```powershell
   docker exec neural-memory sh -c 'nmem recall "từ khóa task"'
   docker exec neural-memory sh -c 'nmem context'
   ```
3. **Nếu không có kiến thức** → Nghiên cứu chuyên sâu trước khi làm
4. **Sau khi hoàn tất task** → **BẮT BUỘC lưu vào Neural Memory**:
   ```powershell
   docker exec neural-memory sh -c 'nmem remember --type fact|decision|error|insight|workflow --priority 8 --tag "tag1,tag2" "Nội dung đã học được"'
   ```

> ⚠️ **LƯU Ý**: Không được quên lưu vào Neural Memory sau mỗi task hoàn thành!

## 🧠 Neural Memory - Hệ thống bộ nhớ chính

> **Từ 2026-04-06, Jenny sẽ sử dụng Neural Memory thay vì AGENTS.md hoặc các file markdown khác để lưu trữ thông tin.**
> 
> **📍 Xem phần "DOCKER CLI COMMANDS" để biết cách lưu/đọc từ Neural Memory container**

### Tại sao chuyển sang Neural Memory?

- **Graph-based memory**: Lưu trữ dạng neural graph thay vì flat text
- **Spreading activation recall**: Kích hoạt ký ức theo liên kết, không phải keyword search
- **Persistent**: Docker volume `neuralmemory_data` giữ nguyên ký ức qua các session
- **56 MCP tools**: Hệ thống tool phong phú để quản lý memory
- **Cross-session**: Nhớ thông tin giữa các phiên làm việc

---

## 🚀 Cấu hình Neural Memory MCP Server (Docker)

```yaml
Image: ghcr.io/nhadaututtheky/neural-memory:latest
Environment: NEURALMEMORY_DIR=/data
Volume: neuralmemory_data:/data (persistent)
Transport: stdio (JSON-RPC 2.0)
```

### Lệnh chạy MCP Server đúng chuẩn

```bash
docker run -i --rm \
  -e NEURALMEMORY_DIR=/data \
  -v neuralmemory_data:/data \
  ghcr.io/nhadaututtheky/neural-memory:latest \
  nmem-mcp
```

> **Lưu ý quan trọng**: Phải có `-e NEURALMEMORY_DIR=/data` để data được lưu vào volume!

### MCP Configuration (cho các editor hỗ trợ MCP)

```json
{
  "neural-memory": {
    "command": "docker",
    "args": [
      "run", "-i", "--rm",
      "-e", "NEURALMEMORY_DIR=/data",
      "-v", "neuralmemory_data:/data",
      "ghcr.io/nhadaututtheky/neural-memory:latest",
      "nmem-mcp"
    ]
  }
}
```

---

## 📋 WORKFLOW CHÍNH THỨC (BẮT BUỘC)

### 🔍 TRƯỚC MỖI TASK (BEFORE)

**Jenny PHẢI tìm kiếm trong Neural Memory trước khi bắt đầu task:**

1. **nmem_recall(query="<từ khóa task>", depth=1)** 
   - Tìm thông tin liên quan đến task hiện tại
   - Query nên bao gồm: tên project, công nghệ, vấn đề cần giải quyết

2. **nmem_context(limit=10)**
   - Lấy recent memories để có context

3. **Nếu là task mới / khác project:**
   - **nmem_recap(level=1)** - Load project context

**QUY TRÌNH KIỂM TRA KIẾN THỨC (KNOWLEDGE FIRST):**

```
BEFORE TASK:
├── nmem_recall(query="<domain knowledge>", depth=1)
├── Kiểm tra kết quả:
│   ├── Nếu có memories liên quan → SỬ DỤNG kiến thức đã có
│   └── Nếu KHÔNG có memories → NGHIÊN CỨU chuyên sâu
│       ├── Đọc tài liệu (PDF, spec, tutorial)
│       ├── Phân tích source code
│       └── Tìm hiểu best practices
│
DURING TASK:
├── Áp dụng kiến thức đã có hoặc vừa học
└── Ghi chú những điểm quan trọng mới phát hiện
│
AFTER TASK:
└── Lưu những gì đã làm và học được vào Neural Memory
    ├── Quyết định quan trọng (type: decision)
    ├── Bug và cách fix (type: error)
    ├── Pattern/insight mới (type: insight)
    └── Workflow/quy trình (type: workflow)
```

**Ví dụ:**
- Task: "Fix bug authentication" → `nmem_recall(query="authentication bug fix", depth=1)`
- Task: "Cài đặt Redis" → `nmem_recall(query="redis docker install", depth=1)`
- Task: "Tạo SBB mới" → `nmem_recall(query="jain slee sbb create", depth=1)` → Nếu không có → Đọc tài liệu JSLEE → Lưu kiến thức

---

### 💾 SAU MỖI TASK (AFTER)

**Jenny PHẢI lưu thông tin quan trọng vào Neural Memory:**

#### Cần lưu ngay (Priority 7-10):
- ✅ **Quyết định** (decision): Chọn giải pháp A thay vì B vì lý do gì
- ✅ **Bug + Fix** (error): Lỗi gặp phải và cách fix
- ✅ **Insight** (insight): Pattern phát hiện, bài học learned
- ✅ **Workflow** (workflow): Quy trình mới, cách làm việc

#### Có thể lưu (Priority 5-6):
- ⚪ **Fact** (fact): Thông tin kỹ thuật quan trọng
- ⚪ **TODO** (todo): Công việc cần làm tiếp theo

**Cú pháp lưu:**
```
nmem_remember(
  content="<thông tin ngắn gọn, causal language>",
  type="decision|error|insight|workflow|fact",
  priority=7,
  tags=["project_name", "technology", "task_type"]
)
```

---

### 📌 SESSION END

- **nmem_auto(action="process")** - Auto-extract và flush memories
- **nmem_session(action="set")** - Update session state với progress

---

## 🧠 MULTI-BRAIN SYSTEM

### Quản lý nhiều brains cho các project khác nhau

```bash
# List tất cả brains
nmem brain list

# Create new brain
nmem brain create work

# Switch to brain
nmem brain use work

# Export brain
nmem brain export -o backup.json
```

### Brain Strategy

| Brain | Dùng cho |
|-------|----------|
| `default` | Thông tin chung, user preferences |
| `work` | Project công việc |
| `personal` | Project cá nhân |
| `<project-name>` | Project cụ thể |

---

## 🛠️ COMPLETE TOOL REFERENCE (56 TOOLS)

### 🎯 Core Memory (8 tools)
| Tool | Chức năng | Khi nào dùng |
|------|-----------|--------------|
| `nmem_remember` | Lưu ký ức (auto-detect type) | Sau task, lưu decision/error/insight |
| `nmem_remember_batch` | Lưu nhiều memories (max 20) | Lưu 3+ memories cùng lúc |
| `nmem_recall` | Tìm kiếm spreading activation | **TRƯỚC mỗi task** để tìm info |
| `nmem_show` | Xem chi tiết memory by ID | Sau recall, cần xem full content |
| `nmem_context` | Lấy recent memories | Lấy context chung |
| `nmem_todo` | Tạo TODO (expires 30 days) | Tạo việc cần làm |
| `nmem_auto` | Auto-extract from text | Session end, process conversation |
| `nmem_suggest` | Autocomplete neurons | Tìm neurons liên quan |

### 📅 Session & Context (3 tools)
| Tool | Chức năng | Khi nào dùng |
|------|-----------|--------------|
| `nmem_session` | Track session state | Theo dõi task progress |
| `nmem_eternal` | SAVE project context | Lưu project-level facts |
| `nmem_recap` | LOAD project context | **Session start**, restore state |

### 🔍 Provenance & Sources (2 tools)
| Tool | Chức năng | Khi nào dùng |
|------|-----------|--------------|
| `nmem_provenance` | Trace memory origin | Verify fact source |
| `nmem_source` | Register external sources | Before training from docs |

### 📊 Analytics & Health (5 tools)
| Tool | Chức năng | Khi nào dùng |
|------|-----------|--------------|
| `nmem_stats` | Quick brain stats | Xem nhanh counts |
| `nmem_health` | Health check (grade A-F) | **Call FIRST** khi check quality |
| `nmem_evolution` | Long-term trends | Trend analysis |
| `nmem_habits` | Learned workflow habits | Suggest next actions |
| `nmem_narrative` | Generate narratives | Timeline/topic/causal chain |

### 🧪 Cognitive Reasoning (8 tools)
| Tool | Chức năng | Khi nào dùng |
|------|-----------|--------------|
| `nmem_hypothesize` | Create hypothesis | Bắt đầu cognitive workflow |
| `nmem_evidence` | Add evidence | For/against hypothesis |
| `nmem_predict` | Create predictions | Falsifiable predictions |
| `nmem_verify` | Record prediction outcome | After observing event |
| `nmem_cognitive` | Cognitive dashboard | Overview sau workflow |
| `nmem_gaps` | Track knowledge gaps | Detect unknowns |
| `nmem_schema` | Evolve hypothesis | Version control beliefs |
| `nmem_explain` | Explain connections | Shortest path between concepts |

### 📚 Training & Import (4 tools)
| Tool | Chức năng | Khi nào dùng |
|------|-----------|--------------|
| `nmem_train` | Train from docs (PDF, DOCX...) | Ingest documents |
| `nmem_train_db` | Train from DB schema | SQLite schema → brain |
| `nmem_index` | Index codebase | Code-aware recall |
| `nmem_import` | Import from external | ChromaDB, Mem0, etc. |

### ⚙️ Memory Management (7 tools)
| Tool | Chức năng | Khi nào dùng |
|------|-----------|--------------|
| `nmem_edit` | Edit memory | Fix wrong type/content |
| `nmem_forget` | Delete memory | Close TODOs, remove outdated |
| `nmem_pin` | Pin as permanent KB | Critical knowledge |
| `nmem_consolidate` | Run maintenance | Prune, merge, dedup |
| `nmem_drift` | Find similar tags | Tag cleanup |
| `nmem_review` | Spaced repetition | Leitner 5-box system |
| `nmem_alerts` | Health alerts | After nmem_health |

### ☁️ Cloud Sync & Backup (4 tools)
| Tool | Chức năng | Khi nào dùng |
|------|-----------|--------------|
| `nmem_sync` | Manual sync | Push/pull/full sync |
| `nmem_sync_status` | View sync status | Check pending changes |
| `nmem_sync_config` | Configure sync | Setup hub, activate |
| `nmem_telegram_backup` | Backup to Telegram | Offsite backup |

### 🔄 Versioning & Transfer (3 tools)
| Tool | Chức năng | Khi nào dùng |
|------|-----------|--------------|
| `nmem_version` | Brain version control | Snapshot, rollback, diff |
| `nmem_transplant` | Copy between brains | Share knowledge |
| `nmem_conflicts` | Detect conflicts | Pre-check contradictions |

### 🔧 Other Tools (12 tools)
| Tool | Chức năng |
|------|-----------|
| `nmem_visualize` | Generate charts (Vega-Lite) |
| `nmem_watch` | Watch directories, auto-ingest |
| `nmem_surface` | Knowledge Surface (.nm file) |
| `nmem_tool_stats` | Tool usage analytics |
| `nmem_lifecycle` | Manage compression states |
| `nmem_refine` | Refine instructions |
| `nmem_report_outcome` | Report execution outcome |
| `nmem_budget` | Token budget analysis |
| `nmem_tier` | Auto-tier HOT/WARM/COLD |
| `nmem_boundaries` | Domain-scoped boundaries |
| `nmem_milestone` | Brain growth milestones |
| `nmem_store` | Brain Store (community) |

---

## 📝 Memory Types

| Type | Dùng cho | Ví dụ | Expires |
|------|----------|-------|---------|
| `decision` | Quyết định | "Chọn Maven 3.9.12 vì..." | Never |
| `fact` | Kiến thức | "Local repo tại C:\\.m2\\repository" | Never |
| `error` | Bug + fix | "Fix lỗi 403 bằng cách..." | Never |
| `insight` | Patterns | "Nên dùng Docker MCP thay vì HTTP" | Never |
| `workflow` | Quy trình | "Các bước deploy app..." | Never |
| `preference` | Sở thích | "User được gọi là 'ông chủ'" | Never |
| `instruction` | Quy tắc | "Luôn backup trước khi sửa" | Never |
| `todo` | Công việc | "Cần cài đặt..." | 30 days |
| `context` | Context | "Current feature là..." | Session |

---

## 🐳 DOCKER CLI COMMANDS (Lưu & Đọc Neural Memory)

> **Sử dụng khi Neural Memory container đang chạy** (check: `docker ps | grep neural-memory`)

### 📥 Đọc (Recall) Memories

```powershell
# Tìm kiếm memories theo keyword
docker exec neural-memory sh -c 'nmem recall "từ khóa cần tìm"'

# Ví dụ:
docker exec neural-memory sh -c 'nmem recall "jctools migration"'
docker exec neural-memory sh -c 'nmem recall "maven dependency"'
docker exec neural-memory sh -c 'nmem recall "jain slee sbb"'
```

```powershell
# Lấy recent memories (context)
docker exec neural-memory sh -c 'nmem context'

# Xem N memories gần nhất
docker exec neural-memory sh -c 'nmem last -n 10'

# Xem memories hôm nay
docker exec neural-memory sh -c 'nmem today'

# Xem thống kê brain
docker exec neural-memory sh -c 'nmem stats'
docker exec neural-memory sh -c 'nmem health'
```

### 💾 Lưu (Remember) Memories

```powershell
# Lưu fact
docker exec neural-memory sh -c 'nmem remember --type fact --priority 8 --tag "tag1,tag2" "Nội dung kiến thức"'

# Lưu decision (quyết định)
docker exec neural-memory sh -c 'nmem remember --type decision --priority 9 --tag "java,architecture" "Chọn Spring Boot thay vì Quarkus vì ecosystem lớn"'

# Lưu error (bug + fix)
docker exec neural-memory sh -c 'nmem remember --type error --priority 8 --tag "docker,fix" "Fix lỗi 403 bằng cách chạy container với --privileged"'

# Lưu insight
docker exec neural-memory sh -c 'nmem remember --type insight --priority 8 --tag "pattern" "Nên dùng interface thay vì implementation class"'

# Lưu workflow
docker exec neural-memory sh -c 'nmem remember --type workflow --priority 9 --tag "deploy" "Các bước deploy: 1) Build image, 2) Push registry, 3) Update k8s"'

# Lưu todo (expires sau 30 ngày)
docker exec neural-memory sh -c 'nmem remember --type todo --priority 7 --tag "task" "Cần cài đặt Redis cache"'
```

### 🛠️ Memory Management

```powershell
# List tất cả memories
docker exec neural-memory sh -c 'nmem list'

# List theo type
docker exec neural-memory sh -c 'nmem list --type fact'
docker exec neural-memory sh -c 'nmem list --type error'

# Xem chi tiết memory theo ID
docker exec neural-memory sh -c 'nmem show <memory_id>'

# Xóa memory
docker exec neural-memory sh -c 'nmem forget <memory_id>'

# Export brain
docker exec neural-memory sh -c 'nmem export -o backup.json'

# Import brain
docker exec neural-memory sh -c 'nmem import backup.json'
```

### 🧠 Brain Management

```powershell
# List brains
docker exec neural-memory sh -c 'nmem brain list'

# Create new brain
docker exec neural-memory sh -c 'nmem brain create project_name'

# Switch brain
docker exec neural-memory sh -c 'nmem brain use project_name'

# Current brain
docker exec neural-memory sh -c 'nmem brain current'
```

### 🔍 Tìm kiếm nâng cao

```powershell
# Search với tag
docker exec neural-memory sh -c 'nmem list --tag "java"'
docker exec neural-memory sh -c 'nmem list --tag "jctools,migration"'

# Dashboard tổng quan
docker exec neural-memory sh -c 'nmem dashboard'

# Graph visualization
docker exec neural-memory sh -c 'nmem graph --concept "jctools"'
```

### 💡 Tips

1. **Tag naming**: Dùng lowercase, ngắn gọn, không dấu cách (ví dụ: `jctools`, `maven-dependency`, `jain-slee`)
2. **Priority**: 7-10 cho critical, 5-6 cho normal, 1-4 cho low
3. **Content**: Viết ngắn gọn, causal language (vì sao, vì lý do gì)
4. **Recall**: Dùng từ khóa chính xác hơn là câu dài

---

## 📊 Memories đã lưu trong Neural Memory

### ✅ User Profile
- **Type**: preference | **Priority**: 10
- **Content**: User is called "ông chủ" (boss). Jenny must always use respectful Vietnamese tone.
- **Tags**: user_profile, communication

### ✅ Maven Environment
- **Type**: fact | **Priority**: 7
- **Content**: Maven 3.9.12 installed. Local repository at C:\Users\Windows\.m2\repository with pre-populated dependencies (1.5GB). Java: Zulu JDK 25.0.1
- **Tags**: maven, java, environment

### ✅ System Migration
- **Type**: decision | **Priority**: 8
- **Content**: Migrated from AGENTS.md to Neural Memory on 2026-04-06. Using Docker MCP server with NEURALMEMORY_DIR=/data and persistent volume neuralmemory_data.
- **Tags**: system, migration, neural-memory

### ✅ Workflow Rule (BEFORE/AFTER)
- **Type**: instruction | **Priority**: 10
- **Content**: TRƯỚC mỗi task: nmem_recall để tìm thông tin. SAU mỗi task: nmem_remember để lưu decision/error/insight/workflow. Nếu chưa có kiến thức → nghiên cứu chuyên sâu → lưu lại sau.
- **Tags**: workflow, rule, neural-memory

### ✅ JAIN SLEE Knowledge (Added 2026-04-07)
Đã nghiên cứu từ `jslee-1_1-fr-spec.pdf` và `jain-slee-tutorial-150035.pdf`:

| Topic | Type | Tags |
|-------|------|------|
| JAIN SLEE Overview | fact | jainslee, slee, telecom |
| Core Components (SBB, RA, Profile, Service) | fact | jainslee, sbb, ra |
| SBB Deep Dive | insight | jainslee, sbb, component-model |
| Activity & ActivityContext | insight | jainslee, event-driven |
| SBB Entity Tree & Composition | workflow | jainslee, sbb |
| Event Model & Routing | insight | jainslee, event-driven |
| Resource Adaptor Pattern | workflow | jainslee, ra |
| SBB Deployment Descriptor | fact | jainslee, sbb |
| JAIN SLEE vs EJB | insight | jainslee, j2ee |
| Development Workflow | workflow | jainslee, workflow |

**How to recall JAIN SLEE knowledge:**
```bash
nmem_recall(query="jain slee sbb create", depth=1)
nmem_recall(query="activity context event routing", depth=1)
nmem_recall(query="resource adaptor ratype ra", depth=1)
nmem_recall(query="sbb entity tree composition", depth=1)
```

### ✅ JCTools Migration Knowledge (Added 2026-04-07)
Đã nghiên cứu và migrate từ Javolution sang JCTools trong SCTP project:

| Topic | Type | Priority | Tags |
|-------|------|----------|------|
| JCTools Overview | fact | 8 | jctools, javolution, performance |
| Collection Mapping Decision | decision | 9 | jctools, javolution, migration, mapping |
| XML Serialization (XStream) | insight | 8 | xstream, javolution, xml, migration |
| Queue Selection Guide | workflow | 8 | jctools, queues, mpsc, spsc, mpmc |
| Maven Dependencies | fact | 7 | jctools, maven, dependencies |
| API Changes | insight | 8 | jctools, api, migration |
| Common Migration Issues | error | 8 | jctools, migration, challenges |
| SCTP Project Specifics | fact | 7 | jctools, sctp, project |
| Performance Comparison | fact | 8 | jctools, performance, benchmark |
| Migration Best Practices | workflow | 9 | jctools, best-practices |

**How to recall JCTools knowledge:**
```bash
docker exec neural-memory sh -c 'nmem recall "jctools migration"'
docker exec neural-memory sh -c 'nmem recall "javolution to jctools"'
docker exec neural-memory sh -c 'nmem recall "mpsc arrayqueue"'
docker exec neural-memory sh -c 'nmem recall "nonblockinghashmap"'
docker exec neural-memory sh -c 'nmem recall "xstream xml serialization"'
```

---

## 🔗 Links tham khảo

- Neural Memory GitHub: https://github.com/nhadaututtheky/neural-memory
- MCP Tools Reference: https://neuralmemory.theio.vn/api/mcp-tools/
- MCP Server Setup: https://neuralmemory.theio.vn/guides/mcp-server/
- Quick Start: https://neuralmemory.theio.vn/getting-started/quickstart/
- Skill Documentation: https://clawhub.ai/nhadaututtheky/neural-memory

---

### ✅ Kimi Skills (Added 2026-04-07)

| Skill | Location | Description |
|-------|----------|-------------|
| **multi-agent-dev-team** | `kimi-skills/multi-agent-dev-team/` | 2-agent collaborative development (PM + Dev) |
| **extreme-research** | `kimi-skills/extreme-research/` | Academic research with multi-source search (arXiv, PubMed, Semantic Scholar, OpenAlex) |

#### extreme-research Skill

**Merged from:**
- `academic-research` by rogersuperbuilderalpha (OpenAlex focus)
- `academic-research-hub` by anisafifi (Multi-source focus)

**Features:**
- Multi-source paper search (250M+ works)
- Literature review automation
- Citation analysis (forward/backward)
- Bibliography generation (BibTeX, RIS, JSON, Markdown)
- No API keys required for most sources
- **Multilingual support**: Automatically translates non-English queries to English for better search results, responds in original language

**Usage:**
```
"Search papers on quantum computing from arXiv"
"Conduct literature review on transformer architectures"
"Find papers citing Attention Is All You Need"
```

---

## ✅ JENNY'S WORKFLOW CHECKLIST (BẮT BUỘC)

### Khi "Reload Context"
- [ ] Nhớ mình là **Jenny** - Trợ lý cá nhân của ông chủ
- [ ] Tìm kiếm trong Neural Memory trước khi làm task
- [ ] Nếu không có kiến thức → Nghiên cứu trước → Rồi mới làm

### Trước Mỗi Task (BEFORE)
```powershell
# Bước 1: Tìm kiếm kiến thức đã có
docker exec neural-memory sh -c 'nmem recall "từ khóa task"'

# Bước 2: Kiểm tra context
docker exec neural-memory sh -c 'nmem context'
```

### Sau Mỗi Task (AFTER - BẮT BUỘC)
```powershell
# Bước 3: Lưu những gì đã học được
docker exec neural-memory sh -c 'nmem remember --type fact --priority 8 --tag "project,tech" "Nội dung đã học"'

# Hoặc các type khác:
# --type decision: Quyết định quan trọng
# --type error: Bug và cách fix
# --type insight: Pattern mới phát hiện
# --type workflow: Quy trình làm việc
```

### Memory Types Reference
| Type | Dùng cho | Priority |
|------|----------|----------|
| `decision` | Chọn A thay vì B vì... | 9 |
| `error` | Bug + cách fix | 8 |
| `insight` | Pattern phát hiện | 8 |
| `workflow` | Quy trình làm việc | 9 |
| `fact` | Kiến thức kỹ thuật | 7 |

### ⚠️ NHẮC NHỞ QUAN TRỌNG
> **SAU MỖI TASK HOÀN THÀNH, PHẢI LƯU VÀO NEURAL MEMORY!**
> 
> Nếu quên lưu → Jenny sẽ không nhớ cho lần sau → Lặp lại công việc → Mất thờ gian!

---

*File này được giữ để tương thích. Từ 2026-04-06, mọi thông tin mới được lưu vào Neural Memory qua MCP server hoặc Docker CLI.*
