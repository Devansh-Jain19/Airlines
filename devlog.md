# Team Development Guide & Git Workflow (`devlog.md`)

Welcome to the **Airlines Booking System** Team Development Guide. This document defines our team's standard operating procedures for executing build commands, managing Git branches, maintaining up-to-date branch histories, and safely committing and pushing code to `main`.

---

## 1. Core Principles

To maintain build stability and prevent integration bottlenecks:
1. **`main` is Always Production-Ready**: Code on `main` must compile cleanly and pass all automated tests.
2. **Never Let Feature Branches Fall Behind**: Update your working branch with `main` daily and immediately before pushing code.
3. **Test Before You Push**: Always execute backend and frontend test suites locally before committing or opening a pull request.
4. **Clean & Descriptive Commit History**: Use conventional commit messages so changes are easy to trace.

---

## 2. Essential Development Commands

### Backend Commands (Spring Boot 3.x)

Execute these commands from the root directory or backend project folder:

| Task | Command | Description |
| :--- | :--- | :--- |
| **Run Application** | `./mvnw spring-boot:run` *(or `./gradlew bootRun`)* | Starts local Spring Boot server on port `8080`. |
| **Run Unit Tests** | `./mvnw test` *(or `./gradlew test`)* | Executes JUnit 5 test suite. Must pass before merging. |
| **Package Application** | `./mvnw clean package -DskipTests=false` | Builds executable JAR file while validating tests. |
| **Clean Build Directory** | `./mvnw clean` | Removes `/target` or `/build` output folder. |

### Frontend Commands (Android / Gradle)

Execute these commands from the root directory or android project folder:

| Task | Command | Description |
| :--- | :--- | :--- |
| **Build Debug APK** | `./gradlew assembleDebug` | Compiles Android app and generates debug APK. |
| **Run Unit Tests** | `./gradlew testDebugUnitTest` | Runs local JVM unit tests for ViewModels & Repositories. |
| **Check Lint & Style** | `./gradlew lint` | Audits code quality and potential Android issues. |
| **Clean Project** | `./gradlew clean` | Flushes Gradle build cache and intermediate artifacts. |

---

## 3. Systematic Git Branching Workflow

```
[origin/main] ───────┬───────────────────────┬───────────────► (Main Stream)
                     │                       │
                     ▼                       ▲ (Rebase & Merge)
[feature/login] ─────┴───► [Sync main] ──────┴───────────────► (Feature Branch)
```

### Phase 1: Starting New Work

Always create feature branches off the latest `main` branch.

```bash
# 1. Switch to main branch
git checkout main

# 2. Fetch and pull latest updates from remote main
git pull origin main

# 3. Create and switch to your feature or bugfix branch
# Naming convention: feature/<name>, bugfix/<issue-id>, refactor/<topic>
git checkout -b feature/auth-jwt-login
```

---

### Phase 2: Keeping Your Branch Up-To-Date with `main`

> **CRITICAL RULE:** Run this synchronization routine **every morning** and **before merging or pushing code**.

If another team member pushes changes to `main`, your feature branch will fall behind. To sync your branch without messy merge commits, use `git rebase`:

#### Standard Sync Procedure (Rebase Method - Recommended):

```bash
# Step 1: Save any uncommitted local work to stash
git stash

# Step 2: Fetch latest main branch from remote
git fetch origin main

# Step 3: Rebase your feature branch on top of updated origin/main
git rebase origin/main

# Step 4: Restore your stashed local work (if you stashed in Step 1)
git stash pop

# Step 5: Verify build & tests pass after rebase
./mvnw test                       # For Backend
./gradlew testDebugUnitTest       # For Frontend
```

#### Alternative Sync Procedure (Merge Method):

If your team prefers merge commits over rebase:

```bash
git fetch origin main
git merge origin/main
```

---

### Phase 3: Committing Code

Make frequent, atomic commits with meaningful descriptions following the **Conventional Commits** specification (`feat`, `fix`, `docs`, `refactor`, `test`, `chore`).

```bash
# 1. Review changed and untracked files
git status

# 2. Inspect your changes to ensure no unwanted files or temporary debug statements remain
git diff

# 3. Stage the modified files
git add path/to/AuthService.java path/to/AuthController.java

# 4. Commit with a descriptive message
git commit -m "feat(auth): add JWT token generation and validation filter"
```

#### Conventional Commit Types:
- `feat:` A new feature for the user or system
- `fix:` A bug fix
- `docs:` Documentation-only changes
- `style:` Code formatting, missing semi-colons, etc. (no functional change)
- `refactor:` Code refactoring without adding features or fixing bugs
- `test:` Adding or updating unit/integration tests
- `chore:` Updating build scripts, dependencies, or configuration

---

### Phase 4: Pushing Your Feature Branch to Remote

Push your branch to GitHub/GitLab so your team can review it.

```bash
# First push of a new branch (sets upstream tracking)
git push -u origin feature/auth-jwt-login

# Subsequent pushes
git push
```

> **Note on Rebased Branches:** If you rebased your branch *after* already pushing it to origin, you must push using `--force-with-lease`:
> ```bash
> git push --force-with-lease
> ```
> *Never use raw `git push --force` as it can overwrite colleagues' work.*

---

### Phase 5: When and How to Push/Merge into `main`

#### Prerequisites for Merging to `main`:
1. ✅ Your branch is fully updated with `origin/main` (rebased/merged).
2. ✅ All backend unit tests (`./mvnw test`) and frontend tests pass cleanly.
3. ✅ Code builds without errors (`./mvnw clean package` / `./gradlew assembleDebug`).
4. ✅ Pull Request (PR) reviewed and approved by at least one team member.

#### Option A: Merging via Pull Request (Recommended Workflow)
1. Open a Pull Request on GitHub/GitLab from `feature/auth-jwt-login` into `main`.
2. Request a code review.
3. Once CI checks pass and approval is received, select **"Squash and Merge"** or **"Rebase and Merge"**.

#### Option B: Merging directly via Command Line (Local Merge)

```bash
# 1. Switch to main and pull latest main
git checkout main
git pull origin main

# 2. Merge your feature branch
git merge feature/auth-jwt-login

# 3. Verify final build on main
./mvnw test

# 4. Push updated main to remote repository
git push origin main

# 5. Clean up local and remote feature branches
git branch -d feature/auth-jwt-login
git push origin --delete feature/auth-jwt-login
```

---

## 4. Handling Merge Conflicts

When rebasing or merging, Git may report conflicts if `main` modified the same code lines.

### Conflict Resolution Steps:
1. Identify conflicting files listed by Git (`git status`).
2. Open each conflicting file and look for conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`).
3. Resolve the conflict by keeping valid code and removing markers.
4. Stage resolved files:
   ```bash
   git add path/to/resolved-file.java
   ```
5. Continue the rebase (or commit the merge):
   ```bash
   git rebase --continue
   ```
6. If things get messy and you want to start over safely, abort:
   ```bash
   git rebase --abort
   ```

---

## 5. Daily Developer Checklist

Print or keep this quick checklist handy during daily development:

```
[ ] MORNING ROUTINE:
    git checkout main && git pull origin main
    git checkout feature/my-branch && git rebase main

[ ] DURING DEVELOPMENT:
    - Write code & unit tests
    - Keep commits small & atomic (git commit -m "feat(...)")

[ ] BEFORE PUSHING:
    - Run build & tests locally (./mvnw test / ./gradlew testDebugUnitTest)
    - Rebase latest main: git fetch origin && git rebase origin/main
    - Push branch: git push -u origin feature/my-branch

[ ] MERGING TO MAIN:
    - Ensure PR is approved & CI passes
    - Merge into main & delete feature branch
```
