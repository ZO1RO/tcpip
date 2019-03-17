import java.io.*;

public class testa{
    public static void main(String[] args) throws IOException {
		DataInputStream dis= new DataInputStream(new FileInputStream(""));
		PrintWriter dos = new PrintWriter("");
          int z = dis.available();
         
           while(z!=0){
        	for(int i=0;i<2;i++)      
        	{
        	short n = dis.readShort();
        	for(int j = 0;j<n;j++)       
        	{
        		char d = dis.readChar(); 
        		System.out.print(d);
        		dos.print(d);
        	}
        	System.out.print(",");
        	dos.print(",");
        	}
                short n = dis.readShort();
        	System.out.println(n);    
        	dos.println(n);
                z = dis.available();
        }
         dis.close();
         dos.flush();
        dos.close();
      }
}
