package io.github.nefilim.githubactions.dsl

fun param(name: String, value: String): Pair<String, String> = name to value

fun expression(exp: String): String = "\${{ $exp }}"

fun githubRef(name: String): String = expression("github.$name")
fun githubRepository(): String = githubRef("repository")
fun outputRef(stepID: Step.StepID, name: String): String = expression("steps.${stepID.id}.outputs.$name")
fun inputRef(name: String): String = expression("github.event.inputs.$name")
fun secretRef(name: String): String = expression("secrets.$name")
fun envRef(name: String): String = expression("env.$name")

