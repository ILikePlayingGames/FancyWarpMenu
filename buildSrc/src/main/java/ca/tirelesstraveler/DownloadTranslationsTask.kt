/*
 * Copyright (c) 2023. TirelessTraveler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ca.tirelesstraveler

import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/*
 * Copyright (c) 2023. TirelessTraveler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * This task uses the Crowdin CLI to download translations to the folder configured in crowdin.yml.
 * It is incremental and will re-run when the Crowdin project's translation progress changes.
 */
abstract class DownloadTranslationsTask: DefaultTask() {
    @Inject
    protected abstract fun getProjectLayout(): ProjectLayout
    @Inject
    protected abstract fun getExecOperations(): ExecOperations
    // Used for incremental builds only
    @Input
    protected fun getProofreadingStatus(): String {
        val outputStream = ByteArrayOutputStream()

        getExecOperations().exec {
            it.workingDir = getProjectLayout().projectDirectory.asFile
            it.executable = "crowdin"
            it.args = mutableListOf("status", "proofreading", "--verbose", "--no-progress", "--no-colors")
            it.standardOutput = outputStream
        }

        return outputStream.toString()
    }
    @get:Input
    abstract val exportOnlyApproved: Property<Boolean>

    init {
        // This is the way recommended in Gradle docs
        @Suppress("LeakingThis")
        exportOnlyApproved.convention(false)
    }

    @TaskAction
    fun downloadTranslations() {
        val argList = mutableListOf("download", "--skip-untranslated-files", "--no-progress", "--no-colors")

        if (exportOnlyApproved.get()) {
            argList.add(1, "--export-only-approved")
        }

        getExecOperations().exec {
            it.workingDir = getProjectLayout().projectDirectory.asFile
            it.executable = "crowdin"
            it.args = argList
        }
    }
}