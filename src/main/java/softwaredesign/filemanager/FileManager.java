/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package softwaredesign.filemanager;


import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Scanner;

/**
 *
 * @author bcb24
 */
public class FileManager {
    private static Object calcLock = new Object();
    //observer for average
    static class AverageObserver{
        private ExecutorService executorService = Executors.newSingleThreadExecutor();

        public void updateAsync(List<Integer> grades) {
            synchronized(calcLock){
            executorService.submit(() -> {
                
                if(!grades.isEmpty()){
                    double average = calcAverage(grades);
                    System.out.println("Average grade: " + average);
                }
                else System.out.println("Average grade: " + 0.0);
                
            });
            
            }
        }
        private double calcAverage(List<Integer> grades){
            if(grades.isEmpty()){
                return 0.0;
            }
            double total = 0.0;
            for(double grade : grades){
                total += grade;
            }
            return total/grades.size();
        }
    
    }
    //observer for grades
    static class GradeObserver{
        private ExecutorService executorService = Executors.newSingleThreadExecutor();

        public void updateAsync(List<Integer> grades) {
            synchronized(calcLock){
            executorService.submit(() -> {
                
                if(!grades.isEmpty()){
                System.out.print("Grades: ");
                for (int grade : grades) {
                    System.out.print(grade + " ");
                }
                System.out.println();
                }
                else System.out.println("No Grades in File");
                 
            });
            
        }
    }
    }
    private static FileManager instance = null;
    static File gradeFile = new File("grades.txt");
    private ArrayList<Integer> grades = new ArrayList<>();
    private List<GradeObserver> gradeObservers = new ArrayList<>();
    private List<AverageObserver> averageObservers = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private FileManager(){
        
        
    }
    //threadsafe fileManager
    private static synchronized FileManager getInstance(){
        
                if(instance == null){
                    instance = new FileManager();
                }
        return instance;
    } 
    //adds and writes a grade to the file while notifying the Observers
    public void AddGrade(int grade){
        synchronized(calcLock){
        try(FileWriter writer = new FileWriter(gradeFile,true)){
            writer.write(grade + "\n");
            grades.add(grade);
            notifyGObserversAsync(gradeObservers);
            
            notifyAObserversAsync(averageObservers);
            
            
        }
        catch(IOException e){
            System.out.println("could not write to file");
        }
        }
    }
    
    public Integer getFirstGrade() {
        if (grades.isEmpty()) {
            return null;
        }
        return grades.get(0);
    }
    public ArrayList<Integer> GetAllGrades(){
        return grades;
    }
    public void DeleteAllGrades() {
        try (FileWriter writer = new FileWriter(gradeFile)) {
            // Clear the grades list and delete the contents of the file
            grades.clear();
            writer.write("");
            writer.flush();
            notifyGObserversAsync(gradeObservers);
            notifyAObserversAsync(averageObservers);

        } catch (IOException e) {
            System.out.println("Error");
        }
    }
    
    //adds a subscriber to gradeObserver
    private void addObserver(GradeObserver observer){
        gradeObservers.add(observer);
    }
    //removes a subscriber from gradeObserver
    private void removeObserver(GradeObserver observer){
        gradeObservers.remove(observer);
    }
    private void addObserver( AverageObserver observer){
        averageObservers.add(observer);
    }
    private void removeObserver(AverageObserver observer){
        averageObservers.remove(observer);
    }
    
    private void Shutdown(){
        executorService.shutdownNow();
    }
    //notifies observers which will update Asynchronously
    private void notifyGObserversAsync( List<GradeObserver> gradesObservers ) {
        for (GradeObserver observer : gradesObservers) {
            observer.updateAsync(grades);
        }
        
        
    }
    private void notifyAObserversAsync( List<AverageObserver> averageObservers ) {
        for (AverageObserver observer : averageObservers) {
            observer.updateAsync(grades);
        }
        
    
    }
    
    

    public static void main(String[] args) {
        FileManager fm;
        fm = FileManager.getInstance();
        AverageObserver averageObserver = new AverageObserver();
        GradeObserver gradesObserver = new GradeObserver();

        fm.addObserver(averageObserver);
        fm.addObserver(gradesObserver);
        int read = 0;
        Scanner scan = new Scanner(System.in);
        System.out.println("Displaying Grades When Empty: " + fm.GetAllGrades());
        System.out.println("Calling Get first Grade when Empty" + fm.getFirstGrade());
        System.out.println("Enter 1 to add Grade Ex: 1 36, Enter 2 to DeleteAllGrades, Enter 3 to quit");
        while(read !=3){
            read = scan.nextInt();
            if(read == 1){
                int addNum = scan.nextInt();
                fm.AddGrade(addNum);
                }
            else if (read == 2){
                fm.DeleteAllGrades();
            }
            else if(read == 3){
                fm.Shutdown();
                break;
            }
        }
        
    }
}
    


