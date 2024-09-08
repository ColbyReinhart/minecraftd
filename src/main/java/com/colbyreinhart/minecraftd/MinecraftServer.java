package com.colbyreinhart.minecraftd;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class MinecraftServer implements Runnable, Closeable
{
	private final Process minecraft;
	private final Set<Writer> outputCopies;

	public MinecraftServer(File workingDirectory, String... command)
	throws IOException
	{
		minecraft = new ProcessBuilder(command)
			.directory(workingDirectory)
			.redirectErrorStream(true)
			.start();
		outputCopies = new HashSet<>();
		System.out.println("Server started");
	}

	@Override
	public void run()
	{
		try (final Scanner scanner = new Scanner(minecraft.getInputStream()))
		{
			while (minecraft.isAlive())
			{
				final String line = scanner.nextLine();
				final Iterator<Writer> it = outputCopies.iterator();
				while (it.hasNext())
				{
					try
					{
						it.next().append(line).append('\n').flush();
					}
					catch (IOException e)
					{
						// The only reason this throws is if the consumer closed their end
						it.remove();
					}
				}
			}
		}
		catch (final NoSuchElementException e)
		{
			// Means the server stopped gracefully
		}
		finally
		{
			try
			{
				close();
			}
			catch (IOException e)
			{
				throw new Error("Server failed to close properly", e);
			}
			finally
			{
				System.out.println("Server stopped at " + Instant.now().toString());
			}
		}
	}

	@Override
	public void close()
	throws IOException
	{
		for (final Writer stream: outputCopies)
		{
			stream.close();
		}
		minecraft.destroy();
	}

	public BufferedInputStream getServerOutput()
	throws IOException
	{
		final PipedOutputStream outCopy = new PipedOutputStream();
		outputCopies.add(new OutputStreamWriter(outCopy));
		return new BufferedInputStream(new PipedInputStream(outCopy));
	}

	public synchronized void command(String command)
	throws IOException
	{
		if (!isAlive())
		{
			throw new IllegalStateException("Server is not alive");
		}
		minecraft.outputWriter().append(command).append('\n').flush();
	}

	public boolean isAlive()
	{
		return minecraft.isAlive();
	}
}
