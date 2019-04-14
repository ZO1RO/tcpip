class MutableInteger {  
    int value;  
  
    MutableInteger(int v) {  
        this.value = v;  
    }  
}  

public class test2 implements Runnable {  
  
    private final MutableInteger sum;  
    private final int            fromInt;  
    private final int            toInt;  
    private final int            threadNo;

    private boolean isPrime(long n){
      if (n == 1)  return false;
      for (long i=2; i<=Math.sqrt(n);++i)
          if (n % i == 0) return false;            
      return true;
    }  
  
    public test2(MutableInteger sum, int fromInt, int toInt, int threadNo) {  
        this.sum = sum;  
        this.fromInt = fromInt;  
        this.toInt = toInt;  
        this.threadNo = threadNo;  
    }  
  
    /*** 
     * 对sum进行加和计算，在sum原始值的基础上从 fromInt 开始计算，一直到 toInt 结束（包含fromInt 和 toInt）的数值 
     */  
    public void run() {   
  
        for (int i = fromInt; i <= toInt; i++) {  
            if(isPrime(i)){
                this.sum.value += 1;
            }
        }  

        System.out.println("Thread." + threadNo + " executes isPrimeSum from " + fromInt + " to " + toInt + " Primesum is: " + sum.value);  
  
    }  
  
    public static void main(String[] args) {  
        Integer toMax = 200000; //对从1到200,000进行加和  
        Integer sumInteger = 0;  
        int threads = 8; //计算线程数  
  
        //每个线程计算一段质数，并将质数个数保存在数组中。  
        MutableInteger[] subSum = new MutableInteger[threads];  
        for (int i = 0; i < threads; i++) {  
            subSum[i] = new MutableInteger(0);  
        }  
  
        for (int i = 0; i < threads; i++) {  
            int fromInt = toMax * i / threads + 1; //边界条件  
            int toInt = toMax * (i + 1) / threads; //边界条件  
            new Thread(new test2(subSum[i], fromInt, toInt, i)).start();  
        }  
        try {  
            Thread.sleep(100); //等待子线程程序执行结束  
  
            for (int i = 0; i < threads; i++) {  
                sumInteger += subSum[i].value;  
            }  
            System.out.println("The sum is :" + sumInteger);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
  
    }  
}  