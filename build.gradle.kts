plugins {
	id("java")
}

tasks {
	jar {
		manifest {
			attributes["Main-Class"] = "com.colbyreinhart.minecraftd.Daemon"
		}
	}
}