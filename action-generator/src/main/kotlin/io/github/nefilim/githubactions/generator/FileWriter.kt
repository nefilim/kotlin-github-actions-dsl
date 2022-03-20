package io.github.nefilim.githubactions.generator

import com.charleskorn.kaml.MultiLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import java.nio.file.Path

@OptIn(ExperimentalStdlibApi::class)
fun main(args: Array<String>) {
    val gitHubActionsYAMLParser = Yaml(
        configuration = YamlConfiguration(
            strictMode = false,
            multiLineStringStyle = MultiLineStringStyle.Literal,
        )
    )

    BundledActions.map {
        it to processActionToGenerate(it, gitHubActionsYAMLParser)
    }.forEach {
        when {
            args.isEmpty() -> it.second.build().writeTo(System.out)
            else -> {
                val path = Path.of(args[0])
                path.toFile().mkdirs()
                it.second.build().writeTo(path)
            }
        }
    }
}