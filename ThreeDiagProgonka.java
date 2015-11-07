package diff_equation;

import static java.lang.Math.E;
import static java.lang.String.format;
import java.util.Scanner;
import static java.lang.Math.abs;
/**
 * 
 * @author Ялунин Максим 314 гр
 */
public class ThreeDiagProgonka {
    
    private final int n;  
    private final double h;
    private final double a;
    private final double b;
    private final double c;
    private final double matrix[][];
    private final double reverce[][];
    private final double f[];
    private final double u[];
    private final double r[];
    private final double p[];
    private final double q[];

    public static void main(String[] args) {
    //1)

        System.out.println("число разбиений отрезка (n): 5");

        ThreeDiagProgonka myProgonka = new ThreeDiagProgonka(5);
//        myProgonka.printControlPoints();
        myProgonka.printSLAU();
//        myProgonka.printCoeffPQ();
//        myProgonka.printResult();
//        myProgonka.printNormaNevyaazkiL1(); 
//        myProgonka.printChisloObusl();
    
    //2)    
        //Зависимость нормы невязки от n
        System.out.println("\nЗависимость нормы невязки от n :"); 
        for(int i = 100; i < 101; i=i+5){        
        ThreeDiagProgonka myProgonka1 = new ThreeDiagProgonka(i);
        myProgonka1.printNormaNevyaazkiL1(); 
        }
    
    //3)
        //Зависимость числа обусл. от n
        System.out.println("\nЗависимость числа обусл от n :"); 
        for(int i = 100; i < 101; i=i+5){        
        ThreeDiagProgonka myProgonka2 = new ThreeDiagProgonka(i);
        myProgonka2.printChisloObusl();
        }        
        
        
        
    //4)    
        //Продолжительность вычислений от n
//        for(int i = 5; i < 100; i=i+5){ 
//            long t1 = System.nanoTime();
//            ThreeDiagProgonka threeDiagProgonka = new ThreeDiagProgonka(3);
//            System.out.println((System.nanoTime() - t1)/1000); 
//        }
        
    }
    
    ThreeDiagProgonka(int number){
        
        n = number;
        p = new double[n+1];
        q = new double[n+1]; 
        r = new double[n+1];
        matrix = new double[n+1][n+1];
        reverce = new double[n+1][n+1];
        f = new double[n+1];
        u = new double[n+1];
        // Найдем необходимые коэффициенты :
        h = 1.0/n; 
        a = -1.0/(2.0*h)-1.0/(h*h);
        b = 2.0/(h*h);
        c = 1.0/(2.0*h)-1.0/(h*h);
        // Зададим матрицу :
        for (int i = 1; i < n; i++) {
            matrix[i][i-1] = a;
            matrix[i][i] = b;
            matrix[i][i+1] = c;            
            for (int j = 0; j <= n; j++) {
                if( j<i-1 || j>i+1 ) matrix[i][j] = 0.0;
            }
        }
        matrix[0][0] = 1;
        for(int i = 1; i <= n ; i++) matrix[0][i] = 0.0;
        matrix[n][n] = 1;        
        for(int i = 0; i < n ; i++) matrix[n][i] = 0.0;
        // Зададим f[i] :
        f[n] = E - 1; 
        for (int i = 0; i < n; i++) f[i] = 0.0;
        
        //printSLAU();
        
        // Найдем коэффициенты p[i] и q[i]:
        p[0] = matrix[0][1] / matrix[0][0];
        q[0] = f[0] /  matrix[0][0];
        for (int i = 1; i <= n ; i++){
           if (i != n) p[i] = matrix[i][i+1] / (matrix[i][i] - p[i-1]*matrix[i][i-1]);
           q[i] = (f[i] - q[i-1]*matrix[i][i-1]) / (matrix[i][i] - p[i-1]*matrix[i][i-1]);
        }
        //printCoeffPQ();
        
        // Вычислим искомое решение u[i] по найденным p[i] и q[i]:
        u[n] = q[n];
        for(int i = n-1; i >= 0 ; i--) u[i] = q[i] - p[i]*u[i+1];
        //printControlPoints();
        //printResult();
        
    }
    
    public void printSLAU(){
        double num = (double) n;        
        System.out.println("\nСистема A*u=f : ");
        for (int i = 0; i <= n; i++){
            System.out.print("|");
            for (int j = 0; j <= n; j++){
                double element = matrix[i][j];
                if(element == a) System.out.print("  a  ");
                else if(element == b) System.out.print("  b  ");
                else if(element == c) System.out.print("  c  ");                
                else System.out.print(element + "  ");
            }
            System.out.print("|");
            if(i > num/2 && i < num/2+1 )System.out.print(" x ");
            else System.out.print("   ");
            System.out.print("| u[" + i + "] |");
            if(i > num/2 && i < num/2+1 )System.out.print(" = ");
            else System.out.print("   ");    
            System.out.println("| " + format("%.15f",f[i]) + " |");
        } 
        System.out.println(", где a = " + a + ", b = " + b + ", c = " + c);        
    }
    
    public void printCoeffPQ(){   
        System.out.println("\nКоэффициенты p[i] и q[i] :");
        for(int i = 0 ; i <= n ; i++) System.out.println(" p[" + i + "] = " + format("%.15f",p[i]) + " q[" + i + "] = " + format("%.15f",q[i]) );    
    } 
    
    public void printControlPoints(){
        System.out.println("\nИсходные точки x[i]:");
        for(int i =0; i <= n; i++){
            System.out.println(format("%.15f",h*i));
        }        
    }
    
    public void printResult(){
        System.out.println("\nРешение u[i]:");
        for(int i = 0 ; i <= n ; i++) System.out.printf("%.15f%n", u[i]);        
    }
    
    public void printNormaNevyaazkiL1(){

        double sum = 0.0;
        for(int i = 0; i <= n ; i++){
            for(int j = 0; j <= n ; j++){
                sum = sum + matrix[i][j]*u[j];
            }
            r[i] = sum - f[i];
            sum = 0.0;
        }
        
        double norma = 0.0;
        for(int i = 0; i <= n ; i++){
            norma = norma + abs(r[i]);
        }
//        System.out.print("\nНорма невязки (L1): ||r|| = " + norma);
        System.out.println(format("%.15f",norma));      

    }
    
    public void printChisloObusl(){
        //Найдем обратную матрицу
        double[] p1;
        double[] q1;
        double[] f1;
        for(int k = 0; k <= n ; k++){
            p1 = new double[n+1];
            q1 = new double[n+1];            
            f1 = new double[n+1];
            f1[k] = 1.0;
            
            p1[0] = matrix[0][1] / matrix[0][0];
            q1[0] = f1[0] /  matrix[0][0];
            for (int i = 1; i <= n ; i++){
               if (i != n) p1[i] = matrix[i][i+1] / (matrix[i][i] - p1[i-1]*matrix[i][i-1]);
               q1[i] = (f1[i] - q1[i-1]*matrix[i][i-1]) / (matrix[i][i] - p1[i-1]*matrix[i][i-1]);
            }

            reverce[n][k] = q1[n];
            for(int i = n-1; i >= 0 ; i--) reverce[i][k] = q1[i] - p1[i]*reverce[i+1][k];
        }
//        System.out.println("\nОбратная матрица: ");      
//        for (double[] str : reverce){
//            System.out.print("|    ");
//            for (double element : str){
//                System.out.print(format("%.15f",element) + "    ");
//            }
//            System.out.println("|");
//        } 
        //Получим нормы прямой и обратной матриц:
        double matrixNorm = normaMatrL1(matrix);
        double reverceNorm = normaMatrL1(reverce);
//        System.out.print("\nЧисло обусловленности (L1): ||M|| = ");
        System.out.println(format("%.15f",matrixNorm*reverceNorm));
    } 
    
    private double normaMatrL1(double[][] m){
        double max = 0.0; 
        double sum;
        for(int j = 0; j <= n ; j++){
            sum = 0.0;
            for(int i = 0; i <= n ; i++){
                sum = sum + abs(m[i][j]);
            }
            if(max < sum) max = sum;
        }       
        return max;
    }
        
}
