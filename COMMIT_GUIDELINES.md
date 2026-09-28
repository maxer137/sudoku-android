# Sudoku Commit Message Guidelines

A commit groups file changes with a title and description of the changes and additional metadata like change date, author, and committer.

These Commit Message Guidelines define and describe how changes shall be structured and described in commits.
Most notably, the text form in which to title the changes.

## General philosophy

- **Each change goes into its own commit**. If you want to summarize what you did with this commit (in the commit message)
and you start using the word "and", you probably want to split the commit up into 2 or more individual commits.

- **Each commit should build** (try to make each commit self-contained so that `./gradlew assembleDebug` succeeds and the
app runs at each commit). This keeps tools like `git bisect` / `jj bisect` useful. If a commit touches logic that has
tests, `./gradlew test` should pass as well.

- **Take your time** when composing the commit message. In order to have a good history the commit messages are essential.
Also if you put effort into the commit message, you'll save work when opening a pull/merge request as the description is
already available.


## Commit message

Commit messages must follow this scheme:

```
TYPE(Scope): Summary

Message Body

Footer
```

The blank lines in between are mandatory. A commit **must** include a `TYPE` and a `Summary` and **may**
additionally contain any of the other components listed here.


### Subject line

The first line ("Subject line") should not exceed 50-70 characters. This is what most tools display at first glance, so it
should contain the most important information. In order for it to be as short and precise as possible, there is the `TYPE` and
optionally a `Scope`. With these it should already be clear what this commit is about in general. The short `Summary` should
then include further information that is important to understand the general idea of this commit at a glance.


#### TYPE

The `TYPE` is one of the following:

| **TYPE** | **Description** | **Example** |
| -------- | --------------- | ----------- |
| BREAK    | A breaking change - not backwards-compatible | Change the format of saved games so old saves no longer load |
| FEAT     | Introduction of a new feature (or extension of an existing one) | |
| FIX      | A bug fix | |
| FORMAT   | Change of formatting - does not influence how the code works | Split a modifier chain onto multiple lines; Update `.idea/codeStyles` |
| DOCS     | Changes to the documentation (either in-source or out-of-source) | Add a KDoc comment to `LogicSolver`; Update these guidelines |
| TEST     | Adds, changes or removes a test-case | Add unit tests for `LogicSolver` difficulty rating |
| MAINT    | Maintenance - Change of non-code files | Update `.gitignore`; Update launcher icons |
| CI       | Changed something for the CI (continuous integration) | Add a workflow that runs `./gradlew test` |
| REFAC    | Code refactoring | Rename variable `x` to `y` |
| BUILD    | Changes related to the build process / buildsystem / dependencies | Bump the Compose BOM in `gradle/libs.versions.toml`; Change `minSdk` |
| TRANSLATION | Translation updates and changes | Add a `values-nl/strings.xml` |
| CHANGE   | Something was changed without falling into existing categories | Change the default difficulty from `MEDIUM` to `EASY` |
| REVERT   | A previous commit had to be reverted because e.g. it was buggy | - |

The `TYPE` has to be in **all-uppercase** in order for it to stand out.

If you feel like you need to use 2 or more types for a single commit but *can't split it* into multiple commits, you can
combine types with `/`: `FEAT/CI: <Summary>`


#### Scope

What area of the project the change is about, written in lowercase. This project uses the following scopes:

| **Scope** | **Area** |
| --------- | -------- |
| data      | Game model and logic in `com.maxer137.sudoku.data` (`Sudoku`, `Cell`, `Digit`, `Pos`, `LogicSolver`, puzzle generation) |
| ui        | Compose screens and components in `com.maxer137.sudoku.ui` (`SudokuScreen`, grid, number pad) |
| theme     | Colors, typography and theming in `com.maxer137.sudoku.ui.theme` |
| android   | App-level Android setup: `MainActivity`, `AndroidManifest.xml`, resources, Gradle project structure |

If none of these fit, pick a short new name and add it to this table. The scope may be omitted if the change is not tied
to a specific area (e.g. `DOCS: Update commit guidelines`). If a change unavoidably touches multiple scopes, list them
separated by a comma: `FEAT(data, ui): <Summary>`


#### Summary

The `Summary` is the heart of the subject line. It should contain a **very brief** summary of what you did in that commit.
In order to make this as short as possible, you may use grammatically incorrect sentences
("Add ability" instead of "add the ability").

In general the `Summary` should answer the question "Applying this commit will ..." where "..." is your `Summary`.
Use the imperative mood and start with a capital letter ("Add", "Fix", "Remove" - not "Added" or "adds").

If your `Summary` contains "and", you should probably split your commit up.

Note: Issue references (such as #2305) **must not** be used in the `Summary`!


### Message Body

Here you give more details about the commit. Why is it necessary and what are the details of the change. You can use
multiple paragraphs for this and be as verbose as you want. Wrap lines at around 72 characters.

The `Message Body` should reference issues that are related to this change, but also provide a short summary of what that
issue is about (so that it can be understood without having to open that issue).

The `Message Body` should contain enough information for someone to be able to look at this commit at some point in the
future and know exactly what it does and why it was needed.


### Footer

The `Footer` contains a list of issue references prefixed by a keyword like `Closes`, `Fixes` or `Implements`.

Each reference should be on its own line:

```
Implements #1234
Closes #2215
```

Additionally the `Footer` may contain another paragraph indicating that a given commit was co-authored by other people.
Each co-author should be listed in a new line like so:

```
Co-Authored-By: Author name <author@example.com>
Co-Authored-By: Other author <other@example.com>
```


### References

We have used the term "reference" a few times in these guidelines. A *reference* has the form `#<ID>` where `<ID>` is the
ID the issue tracker assigns to the respective issue or pull request. Most hosting platforms automatically turn these
references into links when viewing the commit message. You don't have to use a link in your commit message. In fact you
*should not* use a link for this purpose as this is just unnecessarily verbose and makes it hard to read the commit
message outside of the hosting platform.


### Examples

```
FIX(ui): Allow deselecting the selected cell
```
```
FEAT(data): Add difficulty-based puzzle generation

Introduce `LogicSolver`, which applies solving techniques in tiers
(naked/hidden singles, pairs, locked candidates, X-Wing) to determine
the minimum difficulty level required to solve a puzzle.

Update `Sudoku.puzzle()` to accept a `Difficulty` target and generate
puzzles that require exactly that tier of logic, replacing the
previous random clue count approach.
```
```
FEAT(data, ui): Add Given cell type and render givens in bold

Givens must be distinguishable from user input both in the model (so
they can't be overwritten) and on screen. Splitting this into two
commits would leave the UI unable to compile in between.
```
```
BUILD: Bump Compose BOM to 2026.09.00
```

-----

These guidelines are adapted from the [Mumble commit guidelines](https://github.com/mumble-voip/mumble/blob/master/COMMIT_GUIDELINES.md),
which were inspired by https://github.com/bluejava/git-commit-guide
