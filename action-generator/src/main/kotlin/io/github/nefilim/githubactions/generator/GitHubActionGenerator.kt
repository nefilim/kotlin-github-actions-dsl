package io.github.nefilim.githubactions.generator

import com.charleskorn.kaml.MultiLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.typeNameOf
import com.squareup.kotlinpoet.withIndent
import io.github.nefilim.githubactions.domain.GitHubAction
import io.github.nefilim.githubactions.domain.GitHubActionInputParameter
import io.github.nefilim.githubactions.domain.WorkflowCommon
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.time.Duration

private fun String.delimiterCaseToCamelCase(delim: Char): String {
    return this.split(delim).fold(StringBuilder(this.length)) { z, t ->
        z.append(if (z.isBlank()) t else t.replaceFirstChar { it.uppercaseChar() })
    }.toString()
}

fun String.kebabCaseToCamelCase(): String = delimiterCaseToCamelCase('-')
fun String.snakeCaseToCamelCase(): String = delimiterCaseToCamelCase('_')

fun String.normalizeVariableName(): String {
    return when {
        this.contains('-') -> this.kebabCaseToCamelCase()
        this.contains('_') -> this.snakeCaseToCamelCase()
        else -> this
    }
}

fun String.capitalize(): String = this.replaceFirstChar { it.uppercaseChar() }

fun nullableStringProperty(name: String, defaultValue: String = "null"): PropertySpec.Builder {
    return PropertySpec.builder(name, String::class.asTypeName().copy(nullable = true))
}

fun nullableStringParameter(name: String): ParameterSpec.Builder {
    return ParameterSpec.builder(name, String::class.asTypeName().copy(nullable = true))
}

fun nullableStringParameter(name: String, defaultValue: String): ParameterSpec.Builder {
    return ParameterSpec.builder(name, String::class.asTypeName().copy(nullable = true)).defaultValue(defaultValue)
}

@OptIn(ExperimentalStdlibApi::class)
fun generateGitHubAction(
    stub: GitHubActionStub,
    kotlinFilename: String,
    packageName: String,
    className: String,
    actionUses: String
): FileSpec.Builder {
    fun gitHubActionProperty(entry: Map.Entry<String, Input>): PropertySpec.Builder {
        return when {
            entry.value.required != null && entry.value.required == true -> {
                PropertySpec.builder(entry.key.normalizeVariableName(), String::class)
                    .initializer(entry.key.normalizeVariableName())
            }
            else -> {
                PropertySpec.builder(entry.key.normalizeVariableName(), String::class.asTypeName().copy(nullable = true))
                    .initializer(entry.key.normalizeVariableName())
            }
        }
    }
    val constructorBuilder = FunSpec.constructorBuilder()
    stub.inputs.forEach {
        when {
            it.value.required != null && it.value.required == true -> constructorBuilder.addParameter(it.key.normalizeVariableName(), String::class)
            else -> constructorBuilder.addParameter(
                ParameterSpec.builder(it.key.normalizeVariableName(), String::class.asTypeName().copy(nullable = true)).defaultValue("null").build()
            )
        }
    }
    val clazzName = ClassName(packageName, className)
    val clazzBuilder = TypeSpec.Companion.classBuilder(clazzName)
        .addModifiers(KModifier.DATA)
        .addSuperinterface(GitHubAction::class)
        .primaryConstructor(constructorBuilder.build())
    stub.inputs.forEach {
        clazzBuilder.addProperty(gitHubActionProperty(it).build())
    }

    // build inner enum class for input parameters
    val inputParameterConstructorBuilder = FunSpec.constructorBuilder()
        .addParameter(ParameterSpec.builder("parameter", String::class, KModifier.OVERRIDE).build())

    val inputParameterClassName = ClassName(packageName, "InputParameter")
    val inputParameterEnumBuilder = TypeSpec.enumBuilder(inputParameterClassName)
        .addSuperinterface(GitHubActionInputParameter::class)
        .primaryConstructor(inputParameterConstructorBuilder.build())
        .addProperty(PropertySpec.builder("parameter", String::class).initializer("parameter").build())
    val inputParameters = stub.inputs.map {
        val parameterName = it.key.normalizeVariableName().capitalize()
        inputParameterEnumBuilder.addEnumConstant(
            parameterName,
            TypeSpec.anonymousClassBuilder()
                .addSuperclassConstructorParameter("%S", it.key)
                .build()
        )
        it.key to parameterName
    }.toMap()


    // build override fun toStep(id: Step.StepID, name: String, predicate: String?, env: Environment?): Step {
    val paramMemberName = MemberName("io.github.nefilim.githubactions", "param")
    val toStepBlock = CodeBlock.builder().apply {
        add("return %T(name, Uses, \n", WorkflowCommon.Job.Step.Uses::class)
        withIndent {
            add("linkedMapOf(\n")
            withIndent {
                add("*listOfNotNull(\n")
                withIndent {
                    stub.inputs.forEach {
                        when {
                            it.value.required != null && it.value.required == true -> add("%M(%L.%L, ${it.key.normalizeVariableName()}),\n", paramMemberName, inputParameterClassName.simpleName, inputParameters[it.key])
                            else -> add("${it.key.normalizeVariableName()}?.let { %M(%L.%L, it) },\n", paramMemberName, inputParameterClassName.simpleName, inputParameters[it.key])
                        }
                    }
                }
                add(").toTypedArray()),\n")
                add("id,\n")
                add("predicate,\n")
                add("env,\n")
            }
        }
        add(")\n")
    }
    val fullToStep = FunSpec.builder("toStep").addModifiers(KModifier.OVERRIDE)
        .addParameter(ParameterSpec.builder("id", WorkflowCommon.Job.Step.StepID::class.asTypeName().copy(nullable = true)).defaultValue(null).build())
        .addParameter("name", String::class)
        .addParameter(nullableStringParameter("predicate").build())
        .addParameter(ParameterSpec.builder("env", typeNameOf<Map<String, String>>().copy(nullable = true)).build())
        .returns(WorkflowCommon.Job.Step::class)
        .addCode(toStepBlock.build())

    val defaultToStep = FunSpec.builder("toStep").addModifiers(KModifier.OVERRIDE)
        .returns(WorkflowCommon.Job.Step::class)
        .addStatement("return toStep(null, %S, null, null)", stub.name)

    // ok now all together
    return FileSpec.builder(packageName, kotlinFilename)
        .addType(
            clazzBuilder
                .addProperty(PropertySpec.builder("name", String::class.asTypeName()).addModifiers(KModifier.OVERRIDE).initializer("%S", stub.name).build())
                .addProperty(PropertySpec.builder("description", String::class.asTypeName()).addModifiers(KModifier.OVERRIDE).initializer("%S", stub.description).build())
                .addType(TypeSpec.companionObjectBuilder().addProperty(PropertySpec.builder("Uses", String::class).addModifiers(KModifier.CONST).initializer("%S", actionUses).build()).build())
                .addType(inputParameterEnumBuilder.build())
                .addFunction(fullToStep.build())
                .addFunction(defaultToStep.build())
                .build()
        )
}

typealias MetadataProvider = (String) -> String

val downloadMetadataWithHTTP: MetadataProvider = { url: String ->
    val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    val request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .build()

    client.send(request, HttpResponse.BodyHandlers.ofString()).body()
}

data class ActionToGenerate(
    val metadataURL: String,
    val kotlinFilename: String,
    val packageName: String,
    val className: String,
    val uses: String,
)

fun processActionToGenerate(action: ActionToGenerate, yamlParser: Yaml): FileSpec.Builder {
    println("Generating [$action]")
    return downloadMetadataWithHTTP(action.metadataURL).let {
        yamlParser.decodeFromString(GitHubActionStub.serializer(), it)
    }.let {
        generateGitHubAction(it, action.kotlinFilename, action.packageName, action.className, action.uses)
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun main(args: Array<String>) {
    println("generating actions to ${args[0]}")

    val gitHubActionsYAMLParser = Yaml(
        configuration = YamlConfiguration(
            strictMode = false,
            multiLineStringStyle = MultiLineStringStyle.Literal,
        )
    )
    val PackageName = "io.github.nefilim.githubactions.actions"

    listOf(
        ActionToGenerate("https://raw.githubusercontent.com/actions/checkout/v3/action.yml", "CheckoutActionV3", PackageName, "CheckoutActionV3", "actions/checkout@v3"),
        ActionToGenerate("https://raw.githubusercontent.com/actions/setup-java/v3/action.yml", "SetupJavaActionV3", PackageName, "SetupJavaActionV3", "actions/setup-java@v3"),
        ActionToGenerate("https://raw.githubusercontent.com/gradle/gradle-build-action/v2/action.yml", "GradleBuildActionV2", PackageName, "GradleBuildActionV2", "gradle/gradle-build-action@v2"),
        ActionToGenerate("https://raw.githubusercontent.com/slackapi/slack-github-action/v1/action.yml", "SlackActionV1", PackageName, "SlackActionV1", "slackapi/slack-github-action@v1"),
    ).map {
        it to processActionToGenerate(it, gitHubActionsYAMLParser)
    }.forEach { 
        it.second.build().writeTo(Path.of(args[0]))
    }
}