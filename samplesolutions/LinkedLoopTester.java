import java.util.*;

public class LinkedLoopTester {

	public static void main(String[] args) {
	    testEmpty();
		testExceptions();
		testGetCurrent();
		testIterator();
		testLarge();

	}
	private static void testEmpty() {
        System.out.println("===== testEmpty =====");
        LinkedLoop loop = new LinkedLoop<String>();  
        if (!loop.isEmpty()) {
            System.out.println("incorrect for isEmpty() when empty");
        }

	}

	private static void testExceptions() {
        System.out.println("===== testExceptions =====");
        LinkedLoop<String> loop = new LinkedLoop<String>();  
        try {
            String cur = loop.getCurrent();
        } catch (EmptyLoopException e) {
        } catch (Exception e) {
            System.out.println("didn't throw EmptyLoopException for getCurrent()");
        }
        try {
            loop.forward();
        } catch (EmptyLoopException e) {
        } catch (Exception e) {
            System.out.println("didn't throw EmptyLoopException for forward()");
        }
        try {
            loop.backward();
        } catch (EmptyLoopException e) {
        } catch (Exception e) {
            System.out.println("didn't throw EmptyLoopException for backward()");
        }
        try {
            loop.removeCurrent();
        } catch (EmptyLoopException e) {
        } catch (Exception e) {
            System.out.println("didn't throw EmptyLoopException for removeCurrent()");
        }
	}

    private static void testGetCurrent() {
        System.out.println("===== testGetCurrent =====");
        LinkedLoop loop = new LinkedLoop<String>();  
        String s1 = "test1";	
        try {
            loop.insert(s1);
            if (!loop.getCurrent().equals(s1)) {
                System.out.println("incorrect result for getCurrent()");
            }
        } catch (Exception e) {
            System.out.println("didn't throw EmptyLoopException for getCurrent()");
        }
    }

    private static void testIterator() {
        System.out.println("===== testIterator =====");
        LinkedLoop<String> loop = new LinkedLoop<String>();  
        String s = "t";	
        for(int i = 0; i < 20; i++) {
            loop.insert(s + String.valueOf(i));
        }
        Iterator<String> it = loop.iterator();
        int j = 19;
        while(it.hasNext()) {
            String tmp = it.next();
            if (!tmp.equals(s + String.valueOf(j))) {
                System.out.println("incorrect result when call next() of LinkedLoopIterator");
                break;
            }
            j--;
        }
        loop.backward();
        if (!loop.getCurrent().equals("t0")) {
            System.out.println("incorrect result when backward at head");
        }
        loop.forward();
        if (!loop.getCurrent().equals("t19")) {
            System.out.println("incorrect result when forward at tail");
        }
    }

    private static void testLarge() {
        System.out.println("===== testLarge =====");
        LinkedLoop loop = new LinkedLoop<String>();  
        String s = "t";	
        for(int i = 0; i < 1000; i++) {
            loop.insert(s + String.valueOf(i));
        }
        if (loop.size() != 1000) { 
            System.out.println("incorrect size");
        }
        for(int i = 0; i < 1000; i++) {
            loop.removeCurrent();
        }
        if (loop.size() != 0) { 
            System.out.println("incorrect size when empty (0)");
        }
    }


}
