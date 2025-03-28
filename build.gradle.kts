import toni.blahaj.setup.modImplementation

plugins {
	id("toni.blahaj")
}

blahaj {
	config {

	}
	setup {
		deps.modImplementation("toni.txnilib:${mod.loader}-${mod.mcVersion}:1.0.23") { exclude("org.jetbrains.kotlin") }
		build.mod.depends.putIfAbsent("txnilib", "*")
		markRequiredAll("txnilib")

		forgeConfig()

		deps.modImplementation("toni.chunkactivitytracker:${mod.loader}-${mod.mcVersion}:1.0.0") { isTransitive = false }
		build.mod.depends.putIfAbsent("chunkactivitytracker", "*")
		markRequiredAll("chunk-activity-tracker")

		deps.modImplementation("curse.maven:open-parties-and-claims-636608:${property("deps.opac")}")
		deps.modRuntimeOnly("curse.maven:architectury-api-419699:${property("deps.architectury")}")

		if (mod.isForge) {
			deps.compileOnly(deps.annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
			deps.implementation(deps.include("io.github.llamalad7:mixinextras-forge:0.4.1")!!)
		}

		if(mod.isFabric) {
			deps.modImplementation("curse.maven:ftb-chunks-fabric-472657:${property("deps.ftbchunks")}")
			deps.modImplementation("curse.maven:ftb-library-fabric-438495:${property("deps.ftblib")}")
			deps.modRuntimeOnly("curse.maven:ftb-teams-fabric-438497:${property("deps.ftbteams")}")
		} else {
			deps.modImplementation("curse.maven:ftb-chunks-forge-314906:${property("deps.ftbchunks")}")
			deps.modImplementation("curse.maven:ftb-library-forge-404465:${property("deps.ftblib")}")
			deps.modRuntimeOnly("curse.maven:ftb-teams-forge-404468:${property("deps.ftbteams")}")
		}
	}
}