package com.colbyreinhart.minecraftd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;

public class Daemon
{
	private static final String OUTPUT_PATH = "minecraftd.log";

	public static void main(final String[] args)
	throws IOException, UnknownHostException
	{
		final File workingDirectory = new File(System.getenv("MC_SERVER_WD"));
		if (!workingDirectory.exists())
		{
			throw new FileNotFoundException("Working directory not found");
		}
		final File logFile = new File(OUTPUT_PATH);
		if (!logFile.exists())
		{
			logFile.createNewFile();
		}

		try
		(
			final MinecraftServer minecraft = new MinecraftServer(workingDirectory, args);
			final OutputStream logOutput = new FileOutputStream(logFile);
			final Console console = new Console(minecraft);
		)
		{
			final Thread consoleThread = new Thread(console);
			consoleThread.start();
			final Thread minecraftThread = new Thread(minecraft);
			minecraftThread.start();
			new BlockingPipe(minecraft.getServerOutput(), logOutput).run();
		}
		finally
		{
			System.out.println("Daemon stopped");
		}
	}
}
