# Rule 03: Git Safety & Vibe-Coding Guardrails

1. **Never Directly Push to `main` or `develop`**:
   - `main` and `develop` are protected integration trunks.
   - All code enters through Pull Requests targeting `develop`.
2. **Pre-Commit Verification**:
   - Always run:
     ```bash
     git status
     git diff --stat
     git diff
     ```
   - Inspect every modified line. If an AI coding agent modified unrelated files, revert them before committing.
3. **Commit Message Standard**:
   - Use Conventional Commits (`feat:`, `fix:`, `test:`, `docs:`, `chore:`).
4. **No Destructive Commands**:
   - Never run `git push --force` on shared branches.
   - Never run `git reset --hard` or `git clean -fd` blindly.
5. **Conflict Resolution**:
   - Never use `checkout --ours` or `checkout --theirs` blindly.
   - Manually review conflicting chunks, test with `./gradlew testDebugUnitTest`, and commit the resolution.
