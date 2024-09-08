package com.colbyreinhart.minecraftd;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Console implements Runnable, Closeable
{
	private static final int PORT = 7788;

	private final MinecraftServer server;
	private Socket sock = null;

	public Console(final MinecraftServer server)
	throws IOException
	{
		this.server = server;
	}

	@Override
	public void run()
	{
		try (final ServerSocket listener = new ServerSocket(PORT))
		{
			while (server.isAlive())
			{
				sock = listener.accept();
				try (final Scanner clientInput = new Scanner(sock.getInputStream()))
				{
					final BlockingPipe consoleOutput = new BlockingPipe(server.getServerOutput(), sock.getOutputStream());
					final Thread consoleOutputThread = new Thread(consoleOutput);
					consoleOutputThread.start();
					while (!sock.isClosed() && server.isAlive())
					{
						server.command(clientInput.nextLine());
					}
					consoleOutputThread.interrupt();
				}
			}
		}
		catch (final NoSuchElementException e)
		{
			// Graceful shutdown
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Console stopped");
		}
	}

	@Override
	public void close()
	throws IOException
	{
		System.out.println("Test");
		sock.close();
	}
}
