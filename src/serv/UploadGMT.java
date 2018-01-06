import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/uploadgmt")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
                 maxFileSize=1024*1024*100,      // 100MB
                 maxRequestSize=1024*1024*150)   // 150MB

public class UploadGMT extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Name of the directory where uploaded files will be saved, relative to
     * the web application directory.
     */
    private static final String SAVE_DIR = "uploadFiles";
     
    /**
     * handles file upload
     */
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		
    		String name = request.getParameter("gmtname");
    		String description = request.getParameter("description");
    		String text = request.getParameter("text");
        
    		Part filePart = request.getPart("file");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
        InputStream fileStream = filePart.getInputStream();
        
        System.out.println(name+" - "+description+" - "+text+" - "+fileName);

        int i = 0;
        char c = 'c';
        
        StringBuffer fileBuffer = new StringBuffer();
        
        while((i = fileStream.read()) != -1){
        		c = (char)i;
        		fileBuffer.append(c);
        }
        String fileContent = fileBuffer.toString();
        
        SQLmanager sql = new SQLmanager();
        GMT gmt = new GMT(0, name, description, text);
        
        String[] lines = fileContent.split("\n");
        for(String l : lines) {
        		String[] sp = l.split("\t");
        		
        		HashSet<String> genes = new HashSet<String>();
        		for(int j=2; j<sp.length; j++) {
        			genes.add(sp[j].split(",")[0].toUpperCase());
        		}
        		
        		GMTGeneList gl = new GMTGeneList(0, sp[0], sp[1], Arrays.toString(genes.toArray(new String[0])), genes);
        		gmt.genelists.add(gl);
        }
        
        System.out.println("Start writing to DB");
        gmt.writeGMT(sql);
        System.out.println("Done");
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SQLmanager sql = new SQLmanager();
        
		long time = System.currentTimeMillis();
		GMT gmt = new GMT();
        gmt.loadGMT(sql, 1);
        System.out.println(System.currentTimeMillis() - time);
        System.out.println(gmt.toString());
	}
    
}