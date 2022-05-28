package rebound.richsheets.impls.localfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import javax.annotation.Nonnull;
import rebound.exceptions.BinarySyntaxException;
import rebound.richsheets.api.model.RichsheetsTable;
import rebound.richsheets.api.operation.RichsheetsUnencodableFormatException;

public interface RichsheetsLocalFileFormatTranscoder
{
	public RichsheetsTable read(InputStream in) throws IOException, BinarySyntaxException, RichsheetsUnencodableFormatException;
	
	public void write(RichsheetsTable data, @Nonnull Set<Integer> columnsToAutoresize, OutputStream out) throws IOException, RichsheetsUnencodableFormatException;
	
	
	public boolean isCapableOfAutoresizingColumns();
}
