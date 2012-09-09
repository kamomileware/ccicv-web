package org.karmaware.ccicv.web.action;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.exception.StripesRuntimeException;
import net.sourceforge.stripes.localization.LocalePicker;
import net.sourceforge.stripes.util.Log;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.karmaware.cciconv.Conversor;
import org.karmaware.cciconv.Structure;

@UrlBinding("/home.htm")
public class ConvertActionBean extends BaseActionBean {
	static Log log = Log.getInstance(ConvertActionBean.class);
	static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
	static Structure struct;
	static {
		struct = Conversor.buildStructure();
	}
	
	@Validate
	private FileBean xlsAttachment;
	private boolean debug = false;
	private boolean compresed = true;
	private String filenameWoExt;
	private boolean error = false;
	
    @DefaultHandler
    @HandlesEvent("convert")
    public Resolution view() {
    	if(xlsAttachment!=null && !error){
    		String filename = xlsAttachment.getFileName();
    		filenameWoExt = df.format(new Date()).concat("-")
    				.concat(filename.substring(0,filename.lastIndexOf('.')))
    				.concat(debug?"-debug":"");
			return new StreamingResolution((compresed?"application/zip":"text/plain")+"; charset=iso-8859-1"){
				@Override
				protected void stream(HttpServletResponse resp)
						throws Exception {
					resp.setContentType((compresed?"application/zip":"text/plain")+"; charset=iso-8859-1");
					resp.setCharacterEncoding("ISO-8859-1");
					
					setLastModified(System.currentTimeMillis());
					setCharacterEncoding("ISO-8859-1");
					
					OutputStream out = resp.getOutputStream();
					if(compresed){
//						out = new GZIPOutputStream(out);
						ZipOutputStream zout = new ZipOutputStream(out);
						zout.putNextEntry(new ZipEntry(filenameWoExt.concat(".txt")));
						out = zout;
					}
					try{
					Conversor.convert(out, xlsAttachment.getInputStream(), 
							new Date(), ConvertActionBean.struct, debug);
					} catch (Exception e) {
						log.error(e);
						throw new StripesRuntimeException("\""+
								xlsAttachment.getFileName() + "\" - " + e.getLocalizedMessage(),e); 
					}
					if(compresed){
//						((GZIPOutputStream)out).close();
						((ZipOutputStream)out).closeEntry();
						((ZipOutputStream)out).finish();
					}
					xlsAttachment.delete();
				}
			}.setFilename(filenameWoExt.concat(compresed?".zip":".txt"));
    	}else{
    		return new ForwardResolution("/WEB-INF/jsp/home.jsp");
    	}
    }

    @ValidationMethod(on="convert")
    public void avoidNoXlsFormat(ValidationErrors errors) {
        if (this.xlsAttachment!=null &&
        		!(this.xlsAttachment.getFileName().endsWith(".xls") 
        		|| this.xlsAttachment.getFileName().endsWith(".xlsx"))) {
            errors.add("xlsAttachment", new SimpleError("Formato no permitido. Debe seleccionar un fichero con extensión XLS ó XLSX"));
        }
    }
    
    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public String getOsName() {
        return System.getProperty("os.name");
    }

    public FileBean getXlsAttachment() {
        return xlsAttachment;
    }

    public void setXlsAttachment(FileBean xlsAttachment) {
        this.xlsAttachment = xlsAttachment;
    }

	public boolean isCompresed() {
		return compresed;
	}

	public void setCompresed(boolean compresed) {
		this.compresed = compresed;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
