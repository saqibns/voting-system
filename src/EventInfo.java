/**
 * The MIT License (MIT)
Copyright (c) 2015 Saqib Nizam Shamsi
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EventInfo
{
	private File events;
	private String[] eventNames;
	private int noOfEvents;
	public static final int maxEvents = 100;
	
	EventInfo(String fileContainingNames) throws IOException
	{
		int i = 0;
		eventNames = new String[maxEvents];
		String filePath = new File("").getAbsolutePath();
		//System.out.println(filePath);
		filePath = filePath + "\\res\\" + fileContainingNames;
		events = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(events));
		String name = br.readLine();
		while(name != null && name.trim().length() > 0)
		{
			eventNames[i] = name;
			i++;
			name = br.readLine();
		}
		noOfEvents = i;
		br.close();
		/*System.out.println(Arrays.toString(eventNames));
		System.out.println(noOfEvents);*/
	}
	
	public String[] getEventNames() 
	{
		return eventNames;
	}
	public int getNoOfEvents() 
	{
		return noOfEvents;
	}
}
