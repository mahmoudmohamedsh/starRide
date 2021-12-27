package river.ride;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class ReadFile {
    public static void main(String[] args) {
        
        writefile("player71", 10);
        readfile();
  }
    
    public static void writefile(String name , int score){
        File myObj = new File("filename.txt");
         
          try {
      
      if (myObj.createNewFile()) {
        System.out.println("File created: " + myObj.getName());
         FileWriter myWriter = new FileWriter("filename.txt");
         for(int i = 0 ; i < 5; i ++)
            myWriter.write("test-0\n");
         myWriter.close();
      } 
      else {
        System.out.println("File already exists.");
      }
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
          
         String [] oldScore = readfile();
         
         for(int i = 0; i < 5 ; i ++){
             
             if(score > Integer.parseInt(oldScore[i].split("-")[1])){
                 for(int j = 4 ; j > i;j-- )
                     oldScore[j] = oldScore[j-1];
                 oldScore[i] =name+"-"+ score;
                 break;
             }
         }
         
        try {
      FileWriter myWriter = new FileWriter("filename.txt");
      for(int i = 0 ; i < 5; i ++)
        myWriter.write(oldScore[i]+"\n");
      myWriter.close();
      System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    }
    public static String[] readfile(){
        String [] data = new String[5];
        
         try {
      File myObj = new File("filename.txt");
      if (myObj.exists()) {
      Scanner myReader = new Scanner(myObj);
      for (int i = 0; i < 5 ; i ++) {
//            if(!myReader.hasNextLine()){ break ;}
            data[i] = myReader.nextLine();
      }
      myReader.close();
      }
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
         return data;
    }
}
