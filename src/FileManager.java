import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class FileManager {
	private FileInputStream fileInputStream;
	private InputStream inputStream;
	private BufferedInputStream bufferReader;
	private File file;
	public FileManager(String _filePath)
	{
		file = new File(_filePath);
	}
	public FileManager(File _file)
	{
		file = _file;
	}
}
