/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tools.metalava

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.PrintWriter
import java.io.StringWriter

@Suppress("PrivatePropertyName")
class OptionsTest : DriverTest() {
    private val DESCRIPTION = """
$PROGRAM_NAME extracts metadata from source code to generate artifacts such as the signature
files, the SDK stub files, external annotations etc.
""".trimIndent()

    private val FLAGS = """
Usage: $PROGRAM_NAME <flags>

General:
--help                                    This message.
--version                                 Show the version of metalava.
--quiet                                   Only include vital output
--verbose                                 Include extra diagnostic output
--color                                   Attempt to colorize the output (defaults to true
                                          if ${"$"}TERM is xterm)
--no-color                                Do not attempt to colorize the output
--no-docs                                 Cancel any other documentation flags supplied to
                                          metalava. This is here to make it easier
                                          customize build system tasks.
--update-api                              Cancel any other "action" flags other than
                                          generating signature files. This is here to make
                                          it easier customize build system tasks,
                                          particularly for the "make update-api" task.

API sources:
--source-files <files>                    A comma separated list of source files to be
                                          parsed. Can also be @ followed by a path to a
                                          text file containing paths to the full set of
                                          files to parse.
--source-path <paths>                     One or more directories (separated by `:`)
                                          containing source files (within a package
                                          hierarchy)
--classpath <paths>                       One or more directories or jars (separated by
                                          `:`) containing classes that should be on the
                                          classpath when parsing the source files
--merge-qualifier-annotations <file>      An external annotations file to merge and
                                          overlay the sources, or a directory of such
                                          files. Should be used for annotations intended
                                          for inclusion in the API to be written out, e.g.
                                          nullability. Formats supported are: IntelliJ's
                                          external annotations database format, .jar or
                                          .zip files containing those, Android signature
                                          files, and Java stub files.
--merge-inclusion-annotations <file>      An external annotations file to merge and
                                          overlay the sources, or a directory of such
                                          files. Should be used for annotations which
                                          determine inclusion in the API to be written
                                          out, i.e. show and hide. The only format
                                          supported is Java stub files.
--validate-nullability-from-merged-stubs  Triggers validation of nullability annotations
                                          for any class where
                                          --merge-qualifier-annotations includes a Java
                                          stub file.
--validate-nullability-from-list          Triggers validation of nullability annotations
                                          for any class listed in the named file (one
                                          top-level class per line, # prefix for comment
                                          line).
--nullability-warnings-txt <file>         Specifies where to write warnings encountered
                                          during validation of nullability annotations.
                                          (Does not trigger validation by itself.)
--nullability-errors-non-fatal            Specifies that errors encountered during
                                          validation of nullability annotations should not
                                          be treated as errors. They will be written out
                                          to the file specified in
                                          --nullability-warnings-txt instead.
--input-api-jar <file>                    A .jar file to read APIs from directly
--manifest <file>                         A manifest file, used to for check permissions
                                          to cross check APIs
--hide-package <package>                  Remove the given packages from the API even if
                                          they have not been marked with @hide
--show-annotation <annotation class>      Unhide any hidden elements that are also
                                          annotated with the given annotation
--show-single-annotation <annotation>     Like --show-annotation, but does not apply to
                                          members; these must also be explicitly
                                          annotated
--hide-annotation <annotation class>      Treat any elements annotated with the given
                                          annotation as hidden
--show-unannotated                        Include un-annotated public APIs in the
                                          signature file as well
--java-source <level>                     Sets the source level for Java source files;
                                          default is 1.8.

Documentation:
--public                                  Only include elements that are public
--protected                               Only include elements that are public or
                                          protected
--package                                 Only include elements that are public, protected
                                          or package protected
--private                                 Include all elements except those that are
                                          marked hidden
--hidden                                  Include all elements, including hidden

Extracting Signature Files:
--api <file>                              Generate a signature descriptor file
--private-api <file>                      Generate a signature descriptor file listing the
                                          exact private APIs
--dex-api <file>                          Generate a DEX signature descriptor file listing
                                          the APIs
--private-dex-api <file>                  Generate a DEX signature descriptor file listing
                                          the exact private APIs
--dex-api-mapping <file>                  Generate a DEX signature descriptor along with
                                          file and line numbers
--removed-api <file>                      Generate a signature descriptor file for APIs
                                          that have been removed
--format=<v1,v2,v3,...>                   Sets the output signature file format to be the
                                          given version.
--output-kotlin-nulls[=yes|no]            Controls whether nullness annotations should be
                                          formatted as in Kotlin (with "?" for nullable
                                          types, "" for non nullable types, and "!" for
                                          unknown. The default is yes.
--output-default-values[=yes|no]          Controls whether default values should be
                                          included in signature files. The default is
                                          yes.
--compatible-output=[yes|no]              Controls whether to keep signature files
                                          compatible with the historical format (with its
                                          various quirks) or to generate the new format
                                          (which will also include annotations that are
                                          part of the API, etc.)
--omit-common-packages[=yes|no]           Skip common package prefixes like java.lang.*
                                          and kotlin.* in signature files, along with
                                          packages for well known annotations like
                                          @Nullable and @NonNull.
--include-signature-version[=yes|no]      Whether the signature files should include a
                                          comment listing the format version of the
                                          signature file.
--proguard <file>                         Write a ProGuard keep file for the API
--sdk-values <dir>                        Write SDK values files to the given directory

Generating Stubs:
--stubs <dir>                             Generate stub source files for the API
--doc-stubs <dir>                         Generate documentation stub source files for the
                                          API. Documentation stub files are similar to
                                          regular stub files, but there are some
                                          differences. For example, in the stub files,
                                          we'll use special annotations like
                                          @RecentlyNonNull instead of @NonNull to indicate
                                          that an element is recently marked as non null,
                                          whereas in the documentation stubs we'll just
                                          list this as @NonNull. Another difference is
                                          that @doconly elements are included in
                                          documentation stubs, but not regular stubs,
                                          etc.
--exclude-annotations                     Exclude annotations such as @Nullable from the
                                          stub files
--exclude-documentation-from-stubs        Exclude element documentation (javadoc and kdoc)
                                          from the generated stubs. (Copyright notices are
                                          not affected by this, they are always included.
                                          Documentation stubs (--doc-stubs) are not
                                          affected.)
--write-stubs-source-list <file>          Write the list of generated stub files into the
                                          given source list file. If generating
                                          documentation stubs and you haven't also
                                          specified --write-doc-stubs-source-list, this
                                          list will refer to the documentation stubs;
                                          otherwise it's the non-documentation stubs.
--write-doc-stubs-source-list <file>      Write the list of generated doc stub files into
                                          the given source list file
--register-artifact <api-file> <id>       Registers the given id for the packages found in
                                          the given signature file. metalava will inject
                                          an @artifactId <id> tag into every top level
                                          stub class in that API.

Diffs and Checks:
--input-kotlin-nulls[=yes|no]             Whether the signature file being read should be
                                          interpreted as having encoded its types using
                                          Kotlin style types: a suffix of "?" for nullable
                                          types, no suffix for non nullable types, and "!"
                                          for unknown. The default is no.
--check-compatibility:type:state <file>   Check compatibility. Type is one of 'api' and
                                          'removed', which checks either the public api or
                                          the removed api. State is one of 'current' and
                                          'released', to check either the currently in
                                          development API or the last publicly released
                                          API, respectively. Different compatibility
                                          checks apply in the two scenarios. For example,
                                          to check the code base against the current
                                          public API, use
                                          --check-compatibility:api:current.
--check-kotlin-interop                    Check API intended to be used from both Kotlin
                                          and Java for interoperability issues
--migrate-nullness <api file>             Compare nullness information with the previous
                                          stable API and mark newly annotated APIs as
                                          under migration.
--warnings-as-errors                      Promote all warnings to errors
--lints-as-errors                         Promote all API lint warnings to errors
--error <id>                              Report issues of the given id as errors
--warning <id>                            Report issues of the given id as warnings
--lint <id>                               Report issues of the given id as having
                                          lint-severity
--hide <id>                               Hide/skip issues of the given id

JDiff:
--api-xml <file>                          Like --api, but emits the API in the JDiff XML
                                          format instead
--convert-to-jdiff <sig> <xml>            Reads in the given signature file, and writes it
                                          out in the JDiff XML format. Can be specified
                                          multiple times.

Statistics:
--annotation-coverage-stats               Whether metalava should emit coverage statistics
                                          for annotations, listing the percentage of the
                                          API that has been annotated with nullness
                                          information.
--annotation-coverage-of <paths>          One or more jars (separated by `:`) containing
                                          existing apps that we want to measure annotation
                                          coverage statistics for. The set of API usages
                                          in those apps are counted up and the most
                                          frequently used APIs that are missing annotation
                                          metadata are listed in descending order.
--skip-java-in-coverage-report            In the coverage annotation report, skip java.**
                                          and kotlin.** to narrow the focus down to the
                                          Android framework APIs.
--write-class-coverage-to <path>          Specifies a file to write the annotation
                                          coverage report for classes to.
--write-member-coverage-to <path>         Specifies a file to write the annotation
                                          coverage report for members to.

Extracting Annotations:
--extract-annotations <zipfile>           Extracts source annotations from the source
                                          files and writes them into the given zip file
--include-annotation-classes <dir>        Copies the given stub annotation source files
                                          into the generated stub sources; <dir> is
                                          typically
                                          metalava/stub-annotations/src/main/java/.
--rewrite-annotations <dir/jar>           For a bytecode folder or output jar, rewrites
                                          the androidx annotations to be package private
--copy-annotations <source> <dest>        For a source folder full of annotation sources,
                                          generates corresponding package private versions
                                          of the same annotations.
--include-source-retention                If true, include source-retention annotations in
                                          the stub files. Does not apply to signature
                                          files. Source retention annotations are
                                          extracted into the external annotations files
                                          instead.

Injecting API Levels:
--apply-api-levels <api-versions.xml>     Reads an XML file containing API level
                                          descriptions and merges the information into the
                                          documentation

Extracting API Levels:
--generate-api-levels <xmlfile>           Reads android.jar SDK files and generates an XML
                                          file recording the API level for each class,
                                          method and field
--android-jar-pattern <pattern>           Patterns to use to locate Android JAR files. The
                                          default is
                                          ${"$"}ANDROID_HOME/platforms/android-%/android.jar.
--current-version                         Sets the current API level of the current source
                                          code
--current-codename                        Sets the code name for the current source code
--current-jar                             Points to the current API jar, if any

Environment Variables:
METALAVA_DUMP_ARGV                        Set to true to have metalava emit all the
                                          arguments it was invoked with. Helpful when
                                          debugging or reproducing under a debugger what
                                          the build system is doing.
METALAVA_PREPEND_ARGS                     One or more arguments (concatenated by space) to
                                          insert into the command line, before the
                                          documentation flags.
METALAVA_APPEND_ARGS                      One or more arguments (concatenated by space) to
                                          append to the end of the command line, after the
                                          generate documentation flags.

""".trimIndent()

    @Test
    fun `Test invalid arguments`() {
        val args = listOf(ARG_NO_COLOR, "--blah-blah-blah")

        val stdout = StringWriter()
        val stderr = StringWriter()
        com.android.tools.metalava.run(
            args = args.toTypedArray(),
            stdout = PrintWriter(stdout),
            stderr = PrintWriter(stderr)
        )
        assertEquals(BANNER + "\n\n", stdout.toString())
        assertEquals(
            """

Invalid argument --blah-blah-blah

$FLAGS

""".trimIndent(), stderr.toString()
        )
    }

    @Test
    fun `Test help`() {
        val args = listOf(ARG_NO_COLOR, "--help")

        val stdout = StringWriter()
        val stderr = StringWriter()
        com.android.tools.metalava.run(
            args = args.toTypedArray(),
            stdout = PrintWriter(stdout),
            stderr = PrintWriter(stderr)
        )
        assertEquals("", stderr.toString())
        assertEquals(
            """
$BANNER


$DESCRIPTION

$FLAGS

""".trimIndent(), stdout.toString()
        )
    }
}
