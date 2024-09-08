package com.colbyreinhart.minecraftd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class BlockingPipe implements Runnable
{
	private final Scanner input;
	private final Writer output;

	public BlockingPipe(InputStream input, OutputStream output)
	{
		this.input = new Scanner(input);
		this.output = new OutputStreamWriter(output);
	}

	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				output.append(input.nextLine()).append('\n').flush();
			}
		}
		catch (final NoSuchElementException e)
		{
			// This means the input is closed
		}
		catch (final IOException e)
		{
			throw new Error("Blocking pipe failed to close properly", e);
		}
		finally
		{
			System.out.println("Output pipe stopped");
		}
	}
}
