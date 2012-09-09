package org.karmaware.ccicv.web.extensions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.StripesConstants;
import net.sourceforge.stripes.exception.DefaultExceptionHandler;
import net.sourceforge.stripes.exception.ExceptionHandler;
import net.sourceforge.stripes.util.Log;
import net.sourceforge.stripes.validation.SimpleError;

import org.karmaware.ccicv.web.action.ConvertActionBean;

public class ConvExceptionHandler extends DefaultExceptionHandler implements ExceptionHandler {
	static Log log = Log.getInstance(ConvertActionBean.class);
	
	@Override
	public void init(Configuration configuration) throws Exception {	}

	@Override
	public void handle(Throwable throwable, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ActionBean bean = (ActionBean)
	        request.getAttribute(StripesConstants.REQ_ATTR_ACTION_BEAN);

	    if (bean != null) {
	        bean.getContext().getValidationErrors().addGlobalError(
	            new SimpleError("Error in the conversion. "+ 
	        throwable!=null&&throwable.getCause()!=null?throwable.getCause().getMessage():""));
	        try {
	        	response.reset();
	        	new ForwardResolution("/WEB-INF/jsp/home.jsp").execute(request, response);
			} catch (Exception e) {
				log.error(e);
				super.handle(throwable, request, response);
			}
	    }
	    else {
	    	super.handle(throwable, request, response);
	    }
	}

}
