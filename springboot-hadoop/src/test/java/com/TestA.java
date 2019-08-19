package com;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

public class TestA {

    @Test
    public void testV(){


        int[] a = new int[]{4,2,7,1};
        sort(a,4);
        System.out.println(JSONObject.toJSONString(a));
    }




    public void sort(int a[],int n)
    {
        int i, j;
        for( i=0; i < n-1; i ++)
        {
            for(j=0; j<n-1-i; j++)
            {
                if(a[j] > a[j +1])
                {
                    int t= a[j]; a[j] = a[j+1]; a[j+1] = t;
                }
            }
        }
    }


    public void sort1(int[] arr){
        int i,j,temp;
        int len = arr.length;
        for (i=0; i<len-1; i++) /* 外循环为排序趟数，len个数进行len-1趟 */
            for (j=0; j<len-1-i; j++) { /* 内循环为每趟比较的次数，第i趟比较len-i次 */
                if (arr[j] > arr[j+1]) { /* 相邻元素比较，若逆序则交换（升序为左大于右，降序反之） */
                    temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
    }

    @Test
    public void test10(){
            int  s =1, t = 1, i, n=3 ;
            for( i=1;i <= n; i++)
            {
//                t= pow(-1,i) + pow(2,i);
                t= (int) (Math.pow(-1,i) * Math.pow(2,i));
                s+=t;
            }
            System.out.println(s);
    }

    @Test
    public void test1(){
        System.out.println(Math.pow(2,1) *Math.pow(-1,1));
    }


    @Test
    public void testMain(){


    }

    int  f(int n)
    {
        int s;
        if (n==1)
            s=1;
        else
            s=n<=1?f(++n):f(--n);
        return s;
    }
}

