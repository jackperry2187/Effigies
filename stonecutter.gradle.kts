plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.11-fabric" /* [SC] DO NOT EDIT */

// Configure Stonecutter parameters for conditional processing
stonecutter.parameters {
    val loader = stonecutter.current.version.substringAfter("-")
    consts["fabric"] = loader == "fabric"
    consts["neoforge"] = loader == "neoforge"
}

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledPublish", stonecutter.chiseled) {
    group = "project"
    ofTask("publish")
}

stonecutter registerChiseled tasks.register("chiseledRunClient", stonecutter.chiseled) {
    group = "project"
    ofTask("runClient")
}
