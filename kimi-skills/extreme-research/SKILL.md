---
name: extreme-research
description: Comprehensive academic research skill combining multi-source paper search (arXiv, PubMed, Semantic Scholar, OpenAlex), literature review automation, citation analysis, and bibliography generation. Use when users need to find scholarly papers, conduct literature reviews, explore citation chains, download research papers, or generate academic bibliographies. Triggers on requests like "find papers on", "search research about", "literature review", "academic search", "get citations", "download papers", or any scholarly research task.
---

# Extreme Research

**Comprehensive academic research skill for Kimi Code CLI**

Search 250M+ academic works across multiple databases. Free, no API keys required for most sources.

## Overview

This skill provides:
- **Multi-source search**: arXiv, PubMed, Semantic Scholar, OpenAlex
- **Literature review automation**: Multi-step synthesis with theme identification
- **Citation analysis**: Forward and backward citation chaining
- **Bibliography generation**: BibTeX, RIS, JSON, Markdown formats
- **Paper download**: Full-text PDF when available

## Multilingual Research Support

This skill supports research in any language. When you submit a query in a non-English language (e.g., Vietnamese, Chinese, Spanish), the skill will:

1. **Detect your input language**
2. **Translate to English** for optimal search results
3. **Conduct research** using English keywords (academic databases have best English coverage)
4. **Respond in your original language**

### Why translate to English?
- **90%+ of scientific papers** are published in English
- **Better search results**: Academic APIs (arXiv, PubMed, Semantic Scholar) optimized for English
- **Comprehensive coverage**: English keywords return more results
- **Standard practice**: International research community uses English

### Workflow
```
Your Request (Any Language)
    ↓
Detect Language & Translate to English
    ↓
Search Academic Databases (English keywords)
    ↓
Compile Results
    ↓
Respond (Your Original Language)
```

### Example
```
User (Vietnamese): "Tìm bài báo về học máy"
    ↓
Translate: "machine learning"
    ↓
Search: arXiv, Semantic Scholar for "machine learning"
    ↓
Respond (Vietnamese): "Đã tìm thấy 10 bài báo về học máy..."
```

## Supported Academic Sources

| Source | Coverage | Best For |
|--------|----------|----------|
| **OpenAlex** | 250M+ works | Broad interdisciplinary search |
| **arXiv** | Physics, CS, Math | Preprints, cutting-edge research |
| **PubMed** | Biomedical | Medical, life sciences |
| **Semantic Scholar** | CS-focused | Computer science, AI/ML |

## Quick Start

### Search papers by topic
```
Search academic papers on "transformer architectures" from arXiv
```

### Multi-source search
```
Find research about "CRISPR gene therapy" across PubMed and Semantic Scholar
```

### Literature review
```
Conduct a literature review on "algorithmic literacy in education"
```

### Get citations
```
Find papers that cite "Attention Is All You Need"
```

### Download papers
```
Download the top 5 papers on "quantum computing" from arXiv
```

## Research Workflows

### Workflow 1: Quick Paper Discovery

1. **Search** across multiple sources
2. **Filter** by date, citations, relevance
3. **Review** abstracts and metadata
4. **Export** results in preferred format

### Workflow 2: Deep Literature Review

1. **Broad search** with multiple query variations
2. **Deduplicate** results across sources
3. **Rank** by relevance and citation count
4. **Thematic clustering** of papers
5. **Synthesize** findings into structured review

### Workflow 3: Citation Chain Analysis

1. **Start** with seed paper
2. **Backward**: Papers it cites
3. **Forward**: Papers citing it
4. **Map** research lineage

## Search Capabilities

### By Topic
```
Search for papers on "large language models" published in 2023
```

### By Author
```
Find papers by author "Yann LeCun" on arXiv
```

### By DOI
```
Look up paper with DOI "10.1038/s41586-021-03819-2"
```

### With Filters
- Date range: `--from 2023 --to 2024`
- Min citations: `--min-citations 100`
- Max results: `--limit 20`
- Sort by: `--sort citations|relevance|date`

## Output Formats

### 1. Human-Readable Text (default)
```
Title: Attention Is All You Need
Authors: Vaswani et al.
Published: 2017-06-12
Citations: 15000+
Abstract: The dominant sequence transduction models...
```

### 2. JSON (structured data)
```json
{
  "title": "Attention Is All You Need",
  "authors": ["Vaswani, Ashish", "Shazeer, Noam"],
  "year": 2017,
  "citations": 15000,
  "doi": "10.48550/arXiv.1706.03762",
  "abstract": "..."
}
```

### 3. BibTeX (for LaTeX)
```bibtex
@article{vaswani2017attention,
  title={Attention Is All You Need},
  author={Vaswani, Ashish and Shazeer, Noam},
  journal={arXiv preprint},
  year={2017}
}
```

### 4. RIS (for reference managers)
```
TY - JOUR
TI - Attention Is All You Need
AU - Vaswani, Ashish
PY - 2017
ER -
```

### 5. Markdown (for documentation)
```markdown
# Attention Is All You Need

**Authors:** Vaswani et al.
**Published:** 2017

## Abstract
The dominant sequence transduction models...
```

## API Endpoints Used

### OpenAlex (Primary)
- **Base URL**: `https://api.openalex.org`
- **Works**: `/works` - Search 250M+ papers
- **Authors**: `/authors` - Author lookup
- **No API key required**
- **Rate limit**: 100,000 requests/day

### arXiv
- **Base URL**: `http://export.arxiv.org/api/query`
- **Query params**: `search_query`, `start`, `max_results`
- **No API key required**

### PubMed (E-utilities)
- **Base URL**: `https://eutils.ncbi.nlm.nih.gov/entrez/eutils/`
- **Search**: `esearch.fcgi`
- **Summary**: `esummary.fcgi`
- **No API key required**

### Semantic Scholar
- **Base URL**: `https://api.semanticscholar.org/graph/v1`
- **Paper search**: `/paper/search`
- **No API key required for basic usage**

## Best Practices

### Search Strategy
1. **Start broad** - Use general terms for overview
2. **Iterate** - Refine based on initial results
3. **Cross-reference** - Check multiple sources
4. **Track recent** - Use date filters for current research

### Result Management
1. **Save searches** - Export results immediately
2. **Organize** - Create logical directory structures
3. **Export citations** - Generate BibTeX as you go
4. **Track sources** - Note which database returned which papers

### Research Ethics
1. **Respect copyright** - Don't redistribute downloaded papers
2. **Verify licensing** - Check open access status
3. **Follow policies** - Respect institutional access rules
4. **Cite properly** - Use standard academic citation formats

## Limitations

- Not all papers have downloadable PDFs
- Some content requires institutional access
- Citation counts may be outdated
- Rate limits apply to free API tiers
- Results may vary between sources

## Kimi Tools Used

- `WebSearch` - Search for papers via Google/Scholar
- `FetchURL` - Query academic APIs directly
- `ReadFile` - Read downloaded papers
- `WriteFile` - Save results and bibliographies

## Examples

### Example 1: Quick Topic Search
```
User: Find recent papers on "federated learning"
→ Search OpenAlex for "federated learning" 
→ Filter by year 2023-2024
→ Return top 10 results with abstracts
```

### Example 2: Literature Review
```
User: Conduct literature review on "transformers in medical imaging"
→ Multi-source search (arXiv + PubMed)
→ Deduplicate results
→ Thematic clustering
→ Generate markdown synthesis
```

### Example 3: Citation Analysis
```
User: What papers cite "Attention Is All You Need"?
→ Lookup paper by DOI
→ Get citing papers from Semantic Scholar
→ Sort by citation count
→ Export as BibTeX
```

### Example 4: Author Research
```
User: List Geoffrey Hinton's most cited papers
→ Search by author "Geoffrey Hinton"
→ Sort by citations
→ Return top 20 with abstracts
```

### Example 5: Multilingual Research (Vietnamese)
```
User: "Tìm bài báo về mạng neural trong y tế"
→ Detect: Vietnamese
→ Translate: "neural networks in healthcare"
→ Search: PubMed + arXiv for "neural networks healthcare"
→ Return results in Vietnamese
```

### Example 6: Multilingual Research (Chinese)
```
User: "查找关于量子计算的论文"
→ Detect: Chinese
→ Translate: "quantum computing"
→ Search: arXiv + Semantic Scholar
→ Return results in Chinese
```

## References

- OpenAlex API: https://docs.openalex.org/
- arXiv API: https://arxiv.org/help/api
- PubMed E-utilities: https://www.ncbi.nlm.nih.gov/books/NBK25501/
- Semantic Scholar API: https://api.semanticscholar.org/

---

**Note**: This skill combines features from:
- `academic-research` by Topanga (OpenAlex focus)
- `academic-research-hub` by anisafifi (Multi-source focus)
