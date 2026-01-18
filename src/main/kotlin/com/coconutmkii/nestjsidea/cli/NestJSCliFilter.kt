package com.coconutmkii.nestjsidea.cli

import com.intellij.execution.filters.AbstractFileHyperlinkFilter
import com.intellij.execution.filters.FileHyperlinkRawData
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil

const val CREATE = "create "
const val UPDATE = "update "
class NestJSCliFilter(project: Project, baseDir: String) : AbstractFileHyperlinkFilter(project, baseDir) {
    override fun parse(line: String): List<FileHyperlinkRawData?> =
        doParse(line, CREATE).takeIf { it.isNotEmpty() } ?: doParse(line, UPDATE)

    override fun supportVfsRefresh(): Boolean = true

    private fun doParse(line: String, prefix: String): List<FileHyperlinkRawData> {
        val index = StringUtil.indexOfIgnoreCase(line, prefix, 0)
        if (index >= 0) {
            val start = index + prefix.length
            var end = line.indexOf(" (", start)
            if (end == -1) end = line.length
            val fileName = line.substring(start, end).trim { it <= ' ' }
            return listOf(FileHyperlinkRawData(fileName, -1, -1, start, start + fileName.length))
        }
        return emptyList()
    }
}