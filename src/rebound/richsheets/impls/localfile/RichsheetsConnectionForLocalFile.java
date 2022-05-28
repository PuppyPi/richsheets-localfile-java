package rebound.richsheets.impls.localfile;

import static java.util.Objects.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.file.FSUtilities;
import rebound.richsheets.api.model.RichsheetsTable;
import rebound.richsheets.api.operation.RichsheetsConnection;
import rebound.richsheets.api.operation.RichsheetsOperation;
import rebound.richsheets.api.operation.RichsheetsOperation.RichsheetsOperationWithDataTimestamp;
import rebound.richsheets.api.operation.RichsheetsWriteData;

public class RichsheetsConnectionForLocalFile
implements RichsheetsConnection
{
	protected final @Nonnull File file;
	protected final @Nonnull RichsheetsLocalFileFormatTranscoder fileFormat;
	
	public RichsheetsConnectionForLocalFile(@Nonnull File file, @Nonnull RichsheetsLocalFileFormatTranscoder fileFormat)
	{
		this.file = requireNonNull(file);
		this.fileFormat = requireNonNull(fileFormat);
	}
	
	public File getFile()
	{
		return file;
	}
	
	public RichsheetsLocalFileFormatTranscoder getFileFormat()
	{
		return fileFormat;
	}
	
	
	
	@Override
	public boolean isCapableOfAutoresizingColumns()
	{
		return fileFormat.isCapableOfAutoresizingColumns();
	}
	
	@Override
	public Date getCurrentLastModifiedTimestamp() throws IOException
	{
		return file.isFile() ? new Date(file.lastModified()) : null;
	}
	
	
	
	@Override
	public void perform(Integer maxRowsToRead, RichsheetsOperation operation) throws IOException
	{
		//todolp actually use maxRowsToRead in this ^^'
		
		@Nullable RichsheetsTable input;
		@Nullable Date lastModifiedTimeOfOriginalData;
		
		if (file.isFile())
		{
			lastModifiedTimeOfOriginalData = new Date(file.lastModified());
			
			try (InputStream in = new FileInputStream(file))
			{
				input = fileFormat.read(in);
			}
		}
		else
		{
			input = null;
			lastModifiedTimeOfOriginalData = null;
		}
		
		
		
		@Nullable RichsheetsWriteData output = operation instanceof RichsheetsOperationWithDataTimestamp ? ((RichsheetsOperationWithDataTimestamp)operation).performInMemory(input, lastModifiedTimeOfOriginalData) : operation.performInMemory(input);
		
		
		
		if (output != null)
		{
			FSUtilities.performSafeFileSystemWriteTwoStageAndCopy(file, out ->
			{
				fileFormat.write(output.getTable(), output.getColumnsToAutoresize(), out);
			});
		}
	}
}
