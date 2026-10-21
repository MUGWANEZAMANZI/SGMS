# Git Workflow — Student Grade Management System

## Branch Structure

```
main
 └── dev
      ├── feat/feature-name
      └── fix/fix-name
```

| Branch | Purpose |
|--------|---------|
| `main` | Production-ready, stable code only. Never commit directly here. |
| `dev` | Integration branch. All feature and fix branches merge here first. Tests must pass before anything goes to `main`. |
| `feat/*` | One branch per new feature (e.g. `feat/import-watcher`, `feat/pattern-search`). |
| `fix/*` | One branch per bug fix (e.g. `fix/ranking-tie-break`, `fix/stub-range`). |

---

## Daily Workflow

### 1. Start a new feature or fix

Always branch off `dev`, never off `main`.

```bash
git checkout dev
git pull origin dev
git checkout -b feat/your-feature-name
```

### 2. Work and commit

Make small, focused commits. Use the present-tense imperative style.

```bash
git add src/services/ImportWatcher.java
git commit -m "feat: add NIO.2 WatchService directory monitor for auto-import"
```

**Commit prefix conventions:**

| Prefix | When to use |
|--------|-------------|
| `feat:` | New feature or capability |
| `fix:` | Bug fix |
| `test:` | Adding or updating tests |
| `docs:` | Documentation only |
| `refactor:` | Code change with no behaviour change |
| `chore:` | Build config, dependencies, tooling |

### 3. Push your branch to remote

```bash
git push -u origin feat/your-feature-name
```

### 4. Open a pull request → `dev`

- Target: `dev` (not `main`)
- Title: matches your commit prefix and describes the change clearly
- Body: what changed, why, and how to test it

### 5. Run tests on `dev` after merge

After your branch is merged into `dev`, verify the full suite passes:

```bash
git checkout dev
git pull origin dev
mvn verify
```

`mvn verify` runs unit tests, integration tests, and enforces the JaCoCo 85% instruction-coverage threshold. The build fails if coverage drops below 85%.

### 6. Merge `dev` → `main` (release)

Only merge to `main` when `dev` is stable and all tests are green.

```bash
git checkout main
git pull origin main
git merge --no-ff dev -m "release: merge dev into main vX.Y.Z"
git push origin main
```

Use `--no-ff` to preserve the merge commit and keep the history readable.

---

## Quick Reference

```
# Start work
git checkout dev && git pull && git checkout -b feat/my-thing

# Push and open PR → dev
git push -u origin feat/my-thing

# After PR merged, verify dev is green
git checkout dev && git pull && mvn verify

# Release to main
git checkout main && git merge --no-ff dev && git push
```

---

## Testing Requirements (must pass before merging to `main`)

| Requirement | Threshold |
|-------------|-----------|
| Unit tests (collections, regex, streams, file I/O) | 25+ |
| Integration tests (mock executors, mock file system) | 10+ |
| JaCoCo instruction coverage | ≥ 85% |

Run `mvn verify` — it generates the HTML coverage report at
`target/site/jacoco/index.html` and fails the build if coverage is below
the threshold.
