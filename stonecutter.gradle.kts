plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.11-fabric" /* [SC] DO NOT EDIT */

stonecutter {
    parameters {
        constants {
            val loader = node.metadata.project.substringAfterLast("-")
            put("fabric", loader == "fabric")
            put("neoforge", loader == "neoforge")
            put("mc12011", node.metadata.version == "1.21.11")
            put("mc261", node.metadata.version == "26.1")
        }
    }
}

for (node in stonecutter.tree.nodes) {
    tasks.register("build-${node.metadata.project}") {
        group = "project"
        dependsOn(":${node.metadata.project}:build")
    }
}

tasks.register("chiseledBuild") {
    group = "project"
    for (node in stonecutter.tree.nodes) {
        dependsOn(":${node.metadata.project}:build")
    }
}

tasks.register("runAllGameTests") {
    group = "verification"
    description = "Runs the Effigies GameTests for all supported targets."
    for (node in stonecutter.tree.nodes) {
        dependsOn(":${node.metadata.project}:runEffigiesGameTests")
    }
}
