---
name: multi-agent-dev-team
description: 2-agent collaborative software development workflow for Kimi Code CLI. Provides a PM (Project Manager) agent and Dev (Developer) agent that collaborate to build complete software projects from simple descriptions. Use when the user wants to build software projects (websites, web apps, components, scripts) and needs structured project planning and implementation. Triggers on requests like "build me a...", "create a...", "make a..." followed by project type (landing page, website, app, component). Best for Next.js, React, TypeScript, Node.js projects, landing pages, prototypes, and MVPs.
---

# Multi-Agent Dev Team

**2-agent collaborative software development workflow for Kimi Code CLI**

Build complete software projects using AI agents that work together like a real development team.

## Overview

This skill provides:
- **PM Agent** (`coder` subagent with PM role): Orchestrates projects, breaks down requirements, coordinates the Dev agent
- **Dev Agent** (`coder` subagent with Dev role): Implements code, tests functionality, manages Git

**Perfect for:**
- Landing pages and websites
- Small web applications
- Prototypes and MVPs
- Code generation projects
- Learning multi-agent workflows

## How It Works

```
You (User)
    ↓
PM Agent (Orchestrator)
    ↓
Dev Agent (Implementer)
    ↓
Working Code
```

### Workflow

1. **You** describe your project
2. **PM** creates a structured task specification (read `agents/pm-agent/ROLE.md`)
3. **PM** spawns **Dev** subagent with the task spec
4. **Dev** implements the code (read `agents/dev-agent/ROLE.md`)
5. **PM** reviews deliverables
6. **PM** reports completion (or requests revisions)
7. Repeat steps 4-6 if needed (max 3 iterations)

## Quick Start

Simply describe what you want to build:

```
Build me a Next.js landing page with hero section, features, and contact form. 
Use Tailwind CSS and TypeScript.
```

The PM agent will:
1. Break this down into a clear task spec
2. Spawn a Dev agent
3. Coordinate the implementation
4. Report back with results

## Task Specification Format

The PM agent uses this template to communicate with Dev:

```markdown
## Project: [NAME]
## Task: [ACTION]

## Requirements:
1. [Requirement 1]
2. [Requirement 2]

## Technical Constraints:
- [Constraint 1]
- [Constraint 2]

## Acceptance Criteria:
- [ ] [Criterion 1]
- [ ] [Criterion 2]

## Deliverables:
- [Deliverable 1]
- [Deliverable 2]
```

## Supported Project Types

### Works Great
- **Next.js** landing pages & apps
- **React** components & SPAs
- **Node.js** scripts & APIs
- **TypeScript** projects
- **Static sites** (HTML/CSS/JS)
- **Documentation** sites

### Limited Support
- Complex backend systems
- Real-time applications
- Multi-service architectures
- Mobile apps

### Not Recommended
- Large enterprise systems
- Mission-critical production code without human review

## Agent Configuration

Both agents use the `coder` subagent with different role instructions:

### PM Agent
**Role file:** `agents/pm-agent/ROLE.md`

Responsibilities:
- Project intake & planning
- Agent coordination (spawns Dev subagent)
- Quality assurance (basic)
- Director (user) communication

### Dev Agent
**Role file:** `agents/dev-agent/ROLE.md`

Responsibilities:
- Code implementation
- Testing & verification
- Version control (Git)
- Documentation

## Best Practices

### 1. Start Small
Don't ask for everything at once. Start with an MVP:

❌ Bad:
> Build a full e-commerce site with user auth, payments, admin dashboard, and inventory management.

✅ Good:
> Build a simple product landing page with hero, features, and signup form.

### 2. Be Specific
The more specific your requirements, the better the result:

❌ Vague:
> Make a nice website.

✅ Specific:
> Create a Next.js landing page with:
> - Hero section with CTA button
> - 3-column feature grid
> - Contact form with email validation
> - Tailwind CSS styling
> - Dark mode support

### 3. Iterate Incrementally
Build in phases:
- **Phase 1:** Basic structure
- **Phase 2:** Add features
- **Phase 3:** Polish & deploy

### 4. Review Output
Always review the generated code before deploying.

### 5. Provide Examples
If you have a specific style or pattern in mind, share examples:
> Build a landing page similar to https://example.com, but for [your product].

## Examples

### Example 1: Simple Landing Page

```
You: Build a landing page for a SaaS product called "TaskFlow".
Include hero, features (3 cards), and pricing table. Use Next.js
and Tailwind CSS.

PM: Working on it...
[2 minutes later]
PM: TaskFlow landing page complete! Ready for deployment.
```

### Example 2: React Component Library

```
You: Create a reusable Button component library with variants
(primary, secondary, outline) and sizes (sm, md, lg). Use
TypeScript and class-variance-authority.

PM: Task received. Spawning Dev agent...
[3 minutes later]
PM: Button component library complete with Storybook examples.
```

### Example 3: API Integration

```
You: Build a Next.js app that fetches and displays GitHub user
profiles. Include search functionality and responsive cards.

PM: Starting development...
[4 minutes later]
PM: GitHub profile viewer complete with search and error handling.
```

## Troubleshooting

### "Dev agent didn't complete the task"

**Check:**
1. Was the task specification clear?
2. Are required tools available (Node.js, Git)?
3. Did the agent hit resource limits?

**Solution:**
- Simplify the task
- Check Dev agent output
- Try again with clearer requirements

### "Code doesn't work"

**Check:**
1. Dependencies installed? (`npm install`)
2. Environment variables set?
3. Correct Node.js version?

**Solution:**
- Ask PM: "The code has errors. Please review and fix."
- The PM will spawn Dev again for corrections

### "Task took too long"

**Solutions:**
- Break into smaller tasks
- Simplify requirements

## Implementation Notes for Kimi

When using this skill:

1. **Act as PM Agent** - Read `agents/pm-agent/ROLE.md` for full instructions
2. **Spawn Dev Agent** - Use Task tool with `coder` subagent and `agents/dev-agent/ROLE.md`
3. **Monitor Progress** - Check subagent output
4. **Iterate** - Max 3 iterations between PM and Dev

### PM Agent Workflow

```
1. Receive project request from user
2. Read ROLE.md for PM agent behavior
3. Create task specification
4. Spawn Dev subagent:
   - subagent_name: "coder"
   - Include task spec in prompt
   - Include dev agent ROLE.md content
5. Review Dev output
6. Report to user or iterate
```

### Dev Agent Spawning Pattern

When spawning Dev agent, include:
- Task specification (from PM)
- Dev agent role instructions
- Project context
- Technical constraints

Example prompt for Dev agent:
```
You are a Dev Agent in a multi-agent development team.

[TASK SPECIFICATION FROM PM]

[DEV AGENT ROLE INSTRUCTIONS FROM agents/dev-agent/ROLE.md]

Implement the task and report completion with:
- List of deliverables
- Status (all criteria met / issues found)
- Any important notes
```

## References

- **PM Agent Role:** `agents/pm-agent/ROLE.md`
- **Dev Agent Role:** `agents/dev-agent/ROLE.md`

## License

MIT License - Adapted from OpenClaw Multi-Agent Dev Team
