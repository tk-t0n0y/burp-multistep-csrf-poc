package requestparsergenerator.factory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

import org.multistepcsrfpoc.model.request.RequestModel;
import org.python.core.PyFile;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import requestparsergenerator.api.ParserBuilderType;

public class ParserBuilderTypeFactory {
	public static final String fileUploadFolder = "file_uploads";

	public static ParserBuilderType createParserBuilderType(ArrayList<RequestModel> requests) {

		//get Burp temp folder
		File currentClassFile = new File(ParserBuilderTypeFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String burpTempFolder = currentClassFile.getAbsoluteFile().getParent()+java.io.File.separator+ParserBuilderTypeFactory.fileUploadFolder;
		//System.out.println("Temp folder path: "+burpTempFolder);

		PythonInterpreter pyInterpreter = new PythonInterpreter();
		pyInterpreter.exec("from parserbuilder.request_parser_builder import ParserBuilder");
		PyObject parserBuilderClassObj = pyInterpreter.get("ParserBuilder");
		pyInterpreter.close();
		ArrayList<PyFile> requestStreamList = new ArrayList<PyFile>();
		ArrayList<String> protocolList = new ArrayList<String>();
		for (RequestModel request: requests) {
			ByteArrayInputStream requestStream = new ByteArrayInputStream(request.getRequest());
			requestStreamList.add(new PyFile(requestStream));
			protocolList.add(request.getProtocol());
		}
		PyObject parserBuilderPyObj = parserBuilderClassObj.__call__(new PyString(burpTempFolder), new PyList(requestStreamList), new PyList(protocolList));
		return (ParserBuilderType)parserBuilderPyObj.__tojava__(ParserBuilderType.class);
	}
}
