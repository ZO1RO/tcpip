import java.io.*;
class test1{
	 public static void main(String args[]) throws  IOException  {
		 DataInputStream dis = null ;        
	        File f = new File("") ; 
	        dis = new DataInputStream(new FileInputStream(f)) ;    
	        File f1 = new File("") ;
	        OutputStreamWriter dos = new OutputStreamWriter(new FileOutputStream(f1));
	        while(dis!=null){
	        	short booklenth=dis.readShort(); 
	        	for(int i=0;i<booklenth;i++){
	        		dos.write(dis.readChar());
	        		dos.flush();
	        	}
	        dos.write(",");
				Short authorlenth=dis.readShort(); 
	        	for(int i=0;i<authorlenth;i++){
	        		dos.write(dis.readChar());
	        		dos.flush();
	        	}
	        dos.write(",");
	        Short a=dis.readShort();
	        dos.write(""+a);
	        dos.write("\n");
	        dos.flush();
}}}
